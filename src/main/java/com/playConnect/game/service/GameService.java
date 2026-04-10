package com.playConnect.game.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.playConnect.game.dto.CreateGameRequest;
import com.playConnect.game.dto.GameListItemResponse;
import com.playConnect.game.dto.GameListResponse;
import com.playConnect.game.entity.Game;
import com.playConnect.game.entity.GameParticipation;
import com.playConnect.game.repository.GameParticipationRepository;
import com.playConnect.game.repository.GameRepository;
import com.playConnect.security.securityConfig.JwtUtil;
import com.playConnect.utilities.AppConstants;

import jakarta.transaction.Transactional;

@Service
public class GameService {

	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private GameParticipationRepository gameParticipationRepository;

	public Game createGame(String token, CreateGameRequest request) {
		Long userId = jwtUtil.extractUserId(extractBearerToken(token));
		if (request.getTotalPlayers() == null || request.getTotalPlayers() <= 0) {
			throw new RuntimeException("totalPlayers must be greater than 0");
		}
		LocalDateTime startTime = LocalDateTime.of(request.getDate(), request.getTime());
		if (!startTime.isAfter(LocalDateTime.now())) {
			throw new RuntimeException("Cannot create a game in the past");
		}

		Game game = new Game();
		game.setHostId(userId);
		game.setSport(request.getSport());
		game.setArenaId(request.getArenaId());
		game.setLatitude(request.getLatitude());
		game.setLongitude(request.getLongitude());
		game.setDate(request.getDate());
		game.setTime(request.getTime());
		game.setTotalPlayers(request.getTotalPlayers());
		game.setRemainingSpots(request.getTotalPlayers());
		game.setContactNumber(request.getContactNumber());
		game.setEmail(request.getEmail());
		game.setCancelBeforeMinutes(request.getCancelBeforeMinutes());
		game.setStatus(AppConstants.ACTIVE);
		return gameRepository.save(game);
	}

	public GameListResponse getNearbyGames(double lat, double lng, double radius, String sport) {
		List<GameListItemResponse> games = fetchAndFilterGames(lat, lng, radius, sport);
		games.sort(Comparator.comparing(GameListItemResponse::getDistanceKm));
		return new GameListResponse(games);
	}

	public GameListResponse getGamesByArena(Long arenaId) {
		List<Game> games = gameRepository.findUpcomingActiveGamesByArenaId(arenaId);
		Map<Long, Long> joinedCounts = getJoinedCountMap(games);
		List<GameListItemResponse> items = games.stream()
				.map(game -> {
					long joined = joinedCounts.getOrDefault(game.getId(), 0L);
					int slotsLeft = game.getTotalPlayers() - (int) joined;
					double fillRatio = game.getTotalPlayers() == 0 ? 0.0 : (double) joined / game.getTotalPlayers();
					return new GameListItemResponse(game, 0.0, slotsLeft, fillRatio);
				})
				.filter(item -> item.getSlotsLeft() > 0)
				.sorted(Comparator.comparing(GameListItemResponse::getCreatedAt))
				.toList();
		return new GameListResponse(items);
	}

	public GameListResponse getFillingFastGames(double lat, double lng, double radius) {
		List<GameListItemResponse> games = fetchAndFilterGames(lat, lng, radius, null);
		games.sort(Comparator.comparing(GameListItemResponse::getFillRatio).reversed()
				.thenComparing(GameListItemResponse::getDistanceKm));
		return new GameListResponse(games);
	}

	@Transactional
	public Long joinGame(Long gameId, String token) {
		Long userId = jwtUtil.extractUserId(extractBearerToken(token));
		Game game = gameRepository.findById(gameId).orElseThrow(() -> new RuntimeException("Game not found"));
		if (!isUpcomingGame(game)) {
			throw new RuntimeException("Cannot join past games");
		}
		if (game.getHostId().equals(userId)) {
			throw new RuntimeException("Host cannot join their own game");
		}
		if (gameParticipationRepository.existsByGameIdAndLeaderIdAndStatus(gameId, userId, AppConstants.JOINED)) {
			throw new RuntimeException("Cannot join the same game twice");
		}
		long joinedCount = gameParticipationRepository.countByGameIdAndStatus(gameId, AppConstants.JOINED);
		int slotsLeft = game.getTotalPlayers() - (int) joinedCount;
		if (slotsLeft <= 0) {
			throw new RuntimeException("Game is full");
		}
		GameParticipation join = new GameParticipation();
		join.setGameId(gameId);
		join.setLeaderId(userId);
		join.setPlayersCount(1);
		join.setStatus(AppConstants.JOINED);
		GameParticipation saved = gameParticipationRepository.save(join);
		return saved.getId();
	}

	@Transactional
	public void leaveGame(Long gameId, String token) {
		Long userId = jwtUtil.extractUserId(extractBearerToken(token));
		GameParticipation participation = gameParticipationRepository
				.findByGameIdAndLeaderIdAndStatus(gameId, userId, AppConstants.JOINED)
				.orElseThrow(() -> new RuntimeException("Join record not found"));
		participation.setStatus(AppConstants.LEFT);
		gameParticipationRepository.save(participation);
	}

	private List<GameListItemResponse> fetchAndFilterGames(double lat, double lng, double radius, String sport) {
		List<Game> upcomingGames = gameRepository.findUpcomingActiveGames();
		Map<Long, Long> joinedCounts = getJoinedCountMap(upcomingGames);
		return upcomingGames.stream()
				.filter(game -> sport == null || sport.isBlank() || sport.equalsIgnoreCase(game.getSport()))
				.map(game -> toListItem(game, lat, lng, joinedCounts.getOrDefault(game.getId(), 0L)))
				.filter(item -> item.getDistanceKm() <= radius && item.getSlotsLeft() > 0)
				.toList();
	}

	private Map<Long, Long> getJoinedCountMap(List<Game> games) {
		Map<Long, Long> joinedCounts = new HashMap<>();
		if (games.isEmpty()) {
			return joinedCounts;
		}
		List<Long> gameIds = games.stream().map(Game::getId).toList();
		for (Object[] row : gameParticipationRepository.countJoinedByGameIds(gameIds, AppConstants.JOINED)) {
			joinedCounts.put((Long) row[0], (Long) row[1]);
		}
		return joinedCounts;
	}

	private GameListItemResponse toListItem(Game game, double lat, double lng, long joinedCount) {
		int slotsLeft = game.getTotalPlayers() - (int) joinedCount;
		double distance = round2(haversine(lat, lng, game.getLatitude(), game.getLongitude()));
		double fillRatio = game.getTotalPlayers() == 0 ? 0.0 : (double) joinedCount / game.getTotalPlayers();
		return new GameListItemResponse(game, distance, slotsLeft, fillRatio);
	}

	private boolean isUpcomingGame(Game game) {
		return AppConstants.ACTIVE.equalsIgnoreCase(game.getStatus())
				&& LocalDateTime.of(game.getDate(), game.getTime()).isAfter(LocalDateTime.now());
	}

	private String extractBearerToken(String token) {
		if (token == null || token.isBlank()) {
			throw new RuntimeException("Missing authorization token");
		}
		if (token.startsWith("Bearer ")) {
			return token.substring(7);
		}
		return token;
	}

	private double haversine(double lat1, double lon1, double lat2, double lon2) {
		final double earthRadiusKm = 6371.0;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.pow(Math.sin(dLat / 2), 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
						* Math.pow(Math.sin(dLon / 2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return earthRadiusKm * c;
	}

	private double round2(double value) {
		return Math.round(value * 100.0d) / 100.0d;
	}
}
