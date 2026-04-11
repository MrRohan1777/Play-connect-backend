package com.playConnect.profile.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.playConnect.exception.ResourceNotFoundException;
import com.playConnect.game.repository.GamePlayerRepository;
import com.playConnect.game.repository.GameRepository;
import com.playConnect.profile.dto.ProfileResponse;
import com.playConnect.user.entity.User;
import com.playConnect.user.repository.UserRepository;
import com.playConnect.utilities.AppConstants;

@Service
public class ProfileService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private GamePlayerRepository gamePlayerRepository;

	public ProfileResponse getProfile(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		int gamesJoined = Math.toIntExact(gamePlayerRepository.countByUser_IdAndStatus(userId, AppConstants.JOINED));
		int gamesHosted = Math.toIntExact(gameRepository.countByCreatedBy(userId));
		List<String> metStatuses = List.of(AppConstants.JOINED, AppConstants.LEFT);
		long playersMetSum = gamePlayerRepository.sumPlayersMet(userId, metStatuses);
		int playersMet = playersMetSum > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) playersMetSum;

		LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
		int winsThisWeek = Math.toIntExact(gameRepository.countWinsForUserSince(userId, weekAgo));
		int score = (gamesJoined * 2) + (gamesHosted * 5);

		return new ProfileResponse(
				user.getName(),
				score,
				winsThisWeek,
				playersMet,
				gamesJoined,
				gamesHosted);
	}
}
