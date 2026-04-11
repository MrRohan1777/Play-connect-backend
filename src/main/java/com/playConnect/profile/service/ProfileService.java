package com.playConnect.profile.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.playConnect.arena.entity.Arena;
import com.playConnect.arena.repository.ArenaRepository;
import com.playConnect.exception.BadRequestException;
import com.playConnect.exception.ResourceNotFoundException;
import com.playConnect.game.entity.Game;
import com.playConnect.game.repository.GamePlayerRepository;
import com.playConnect.game.repository.GameRepository;
import com.playConnect.profile.dto.ProfileNextGameResponse;
import com.playConnect.profile.dto.ProfileResponse;
import com.playConnect.profile.dto.UpdateProfileRequest;
import com.playConnect.user.entity.User;
import com.playConnect.user.enums.SkillLevel;
import com.playConnect.user.repository.UserRepository;
import com.playConnect.utilities.AppConstants;

@Service
public class ProfileService {

	private static final DateTimeFormatter NEXT_GAME_TIME = DateTimeFormatter.ofPattern("HH:mm");

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private GamePlayerRepository gamePlayerRepository;

	@Autowired
	private ArenaRepository arenaRepository;

	public ProfileResponse getProfile(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		return buildProfileResponse(user);
	}

	@Transactional
	public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (request == null) {
			return buildProfileResponse(user);
		}

		if (request.getName() != null) {
			String name = request.getName().trim();
			if (!name.isEmpty()) {
				user.setName(name);
			}
		}
		if (request.getCity() != null) {
			user.setCity(request.getCity().trim().isEmpty() ? null : request.getCity().trim());
		}
		if (request.getLevel() != null) {
			if (request.getLevel().isBlank()) {
				throw new BadRequestException("Level cannot be blank when provided");
			}
			user.setSkillLevel(parseSkillLevel(request.getLevel()));
		}

		userRepository.save(user);
		return buildProfileResponse(user);
	}

	private ProfileResponse buildProfileResponse(User user) {
		Long userId = user.getId();

		int gamesJoined = Math.toIntExact(gamePlayerRepository.countByUser_IdAndStatus(userId, AppConstants.JOINED));
		int gamesHosted = Math.toIntExact(gameRepository.countByCreatedBy(userId));
		long playersMetLong = gamePlayerRepository.countDistinctPlayersMet(userId, AppConstants.JOINED);
		int playersMet = playersMetLong > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) playersMetLong;

		LocalDateTime weekStart = LocalDate.now().minusDays(7).atStartOfDay();
		int winsThisWeek = Math.toIntExact(gameRepository.countWinsForUserSince(userId, weekStart));
		int score = (gamesJoined * 2) + (gamesHosted * 5) + (winsThisWeek * 10);

		ProfileResponse response = new ProfileResponse();
		response.setName(user.getName());
		response.setEmail(user.getEmail());
		response.setLevel(formatSkillLevel(user.getSkillLevel()));
		response.setCity(user.getCity());
		response.setProfileImage(user.getProfileImage());
		if (user.getCreatedAt() != null) {
			response.setJoinedDate(user.getCreatedAt().toLocalDate());
		}
		response.setScore(score);
		response.setGamesJoined(gamesJoined);
		response.setGamesHosted(gamesHosted);
		response.setWinsThisWeek(winsThisWeek);
		response.setPlayersMet(playersMet);
		response.setNextGame(resolveNextGame(userId));

		return response;
	}

	private ProfileNextGameResponse resolveNextGame(Long userId) {
		List<Game> upcoming = gameRepository.findUpcomingInvolvingUser(userId, PageRequest.of(0, 1));
		if (upcoming.isEmpty()) {
			return null;
		}
		Game g = upcoming.get(0);
		String arenaName = null;
		if (g.getArenaId() != null) {
			arenaName = arenaRepository.findById(g.getArenaId()).map(Arena::getName).orElse(null);
		}
		LocalDateTime start = g.getStartTime();
		return new ProfileNextGameResponse(
				g.getSport(),
				start != null ? start.toLocalDate() : null,
				start != null ? NEXT_GAME_TIME.format(start.toLocalTime()) : null,
				arenaName);
	}

	private static String formatSkillLevel(SkillLevel skillLevel) {
		if (skillLevel == null) {
			return null;
		}
		String n = skillLevel.name();
		return n.charAt(0) + n.substring(1).toLowerCase(Locale.ROOT);
	}

	private static SkillLevel parseSkillLevel(String raw) {
		String t = raw.trim().toUpperCase(Locale.ROOT).replace(' ', '_');
		try {
			return SkillLevel.valueOf(t);
		} catch (IllegalArgumentException e) {
			throw new BadRequestException("Invalid level; use BEGINNER, INTERMEDIATE, or ADVANCED");
		}
	}
}
