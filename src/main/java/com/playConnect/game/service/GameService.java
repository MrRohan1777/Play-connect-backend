package com.playConnect.game.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.playConnect.arena.entity.Arena;
import com.playConnect.arena.repository.ArenaRepository;
import com.playConnect.game.dto.CreateGameRequest;
import com.playConnect.game.dto.CreateGameResponse;
import com.playConnect.game.dto.GameListItemResponse;
import com.playConnect.game.dto.GameListResponse;
import com.playConnect.game.entity.Game;
import com.playConnect.game.entity.GamePlayer;
import com.playConnect.exception.BadRequestException;
import com.playConnect.exception.ConflictException;
import com.playConnect.exception.ForbiddenException;
import com.playConnect.exception.ResourceNotFoundException;
import com.playConnect.exception.UnauthorizedException;
import com.playConnect.game.repository.GamePlayerRepository;
import com.playConnect.game.repository.GameRepository;
import com.playConnect.security.securityConfig.JwtUtil;
import com.playConnect.user.repository.UserRepository;
import com.playConnect.utilities.AppConstants;

import jakarta.transaction.Transactional;

@Service
public class GameService {

	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private GamePlayerRepository gamePlayerRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ArenaRepository arenaRepository;

	@Value("${app.game.arena-location-tolerance-km:1.0}")
	private double arenaLocationToleranceKm;

	@Transactional
	public CreateGameResponse createGame(String token, CreateGameRequest request) {
		Long userId = jwtUtil.extractUserId(extractBearerToken(token));

		if (request.getSport() == null || request.getSport().isBlank()) {
			throw new BadRequestException("sport is required");
		}
		if (request.getStartTime() == null || !request.getStartTime().isAfter(LocalDateTime.now())) {
			throw new BadRequestException("Cannot create past game");
		}
		if (request.getTotalPlayers() == null || request.getTotalPlayers() <= 1) {
			throw new BadRequestException("totalPlayers must be greater than 1");
		}

		Game game = new Game();
		game.setCreatedBy(userId);
		game.setSport(request.getSport().trim());
		game.setStartTime(request.getStartTime());
		game.setTotalPlayers(request.getTotalPlayers());
		game.setStatus(AppConstants.ACTIVE);

		resolveLocationForCreate(request, game);

		Game saved = gameRepository.save(game);

		GamePlayer hostJoin = new GamePlayer();
		hostJoin.setGameId(saved.getId());
		hostJoin.setUser(userRepository.getReferenceById(userId));
		hostJoin.setPlayersCount(1);
		hostJoin.setStatus(AppConstants.JOINED);
		gamePlayerRepository.save(hostJoin);

		return new CreateGameResponse("Game created", saved.getId());
	}

	public GameListResponse getNearbyGames(double lat, double lng, double radius, String sport) {
		List<GameListItemResponse> games = fetchAndFilterGames(lat, lng, radius, sport);
		games.sort(Comparator.comparing(GameListItemResponse::getDistanceKm));
		return new GameListResponse(games);
	}

	public GameListResponse getGamesByArena(Long arenaId, Double userLat, Double userLng) {
		List<Game> games = gameRepository.findUpcomingActiveGamesByArenaId(arenaId);
		Map<Long, Long> joinedCounts = getJoinedCountMap(games);
		boolean hasUserPosition = userLat != null && userLng != null;
		List<GameListItemResponse> items = games.stream()
				.map(game -> {
					long joined = joinedCounts.getOrDefault(game.getId(), 0L);
					int slotsLeft = game.getTotalPlayers() - (int) joined;
					double fillRatio = game.getTotalPlayers() == 0 ? 0.0 : (double) joined / game.getTotalPlayers();
					double distanceKm = distanceKmToGame(userLat, userLng, game);
					return new GameListItemResponse(game, distanceKm, slotsLeft, fillRatio);
				})
				.filter(item -> item.getSlotsLeft() > 0)
				.sorted(hasUserPosition
						? Comparator.comparing(GameListItemResponse::getDistanceKm)
								.thenComparing(GameListItemResponse::getStartTime)
						: Comparator.comparing(GameListItemResponse::getStartTime))
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
		Game game = gameRepository.findById(gameId)
				.orElseThrow(() -> new ResourceNotFoundException("Game not found"));
		if (!isUpcomingGame(game)) {
			throw new BadRequestException("Cannot join past games");
		}
		if (game.getCreatedBy().equals(userId)) {
			throw new ForbiddenException("Cannot join a game you created");
		}
		if (gamePlayerRepository.existsByGameIdAndUser_IdAndStatus(gameId, userId, AppConstants.JOINED)) {
			throw new ConflictException("Cannot join the same game twice");
		}
		long joinedCount = gamePlayerRepository.countByGameIdAndStatus(gameId, AppConstants.JOINED);
		int slotsLeft = game.getTotalPlayers() - (int) joinedCount;
		if (slotsLeft <= 0) {
			throw new ConflictException("Game is full");
		}
		GamePlayer join = new GamePlayer();
		join.setGameId(gameId);
		join.setUser(userRepository.getReferenceById(userId));
		join.setPlayersCount(1);
		join.setStatus(AppConstants.JOINED);
		GamePlayer saved = gamePlayerRepository.save(join);
		return saved.getId();
	}

	@Transactional
	public void leaveGame(Long gameId, String token) {
		Long userId = jwtUtil.extractUserId(extractBearerToken(token));
		GamePlayer participation = gamePlayerRepository
				.findByGameIdAndUser_IdAndStatus(gameId, userId, AppConstants.JOINED)
				.orElseThrow(() -> new ResourceNotFoundException("Join record not found"));
		participation.setStatus(AppConstants.LEFT);
		gamePlayerRepository.save(participation);
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
		for (Object[] row : gamePlayerRepository.countJoinedByGameIds(gameIds, AppConstants.JOINED)) {
			joinedCounts.put((Long) row[0], (Long) row[1]);
		}
		return joinedCounts;
	}

	private double distanceKmToGame(Double userLat, Double userLng, Game game) {
		if (userLat == null || userLng == null || game.getLatitude() == null || game.getLongitude() == null) {
			return 0.0;
		}
		return round2(haversine(userLat, userLng, game.getLatitude(), game.getLongitude()));
	}

	private GameListItemResponse toListItem(Game game, double lat, double lng, long joinedCount) {
		int slotsLeft = game.getTotalPlayers() - (int) joinedCount;
		double distance = distanceKmToGame(lat, lng, game);
		double fillRatio = game.getTotalPlayers() == 0 ? 0.0 : (double) joinedCount / game.getTotalPlayers();
		return new GameListItemResponse(game, distance, slotsLeft, fillRatio);
	}

	private boolean isUpcomingGame(Game game) {
		return AppConstants.ACTIVE.equalsIgnoreCase(game.getStatus())
				&& game.getStartTime().isAfter(LocalDateTime.now());
	}

	/**
	 * Location required: either map pin (lat/lng) or arena (uses arena coordinates when pin omitted).
	 */
	private void resolveLocationForCreate(CreateGameRequest request, Game game) {
		Double lat = request.getLatitude();
		Double lng = request.getLongitude();
		Long arenaId = request.getArenaId();

		if (arenaId != null) {
			Arena arena = arenaRepository.findById(arenaId)
					.orElseThrow(() -> new ResourceNotFoundException("Arena not found"));
			game.setArenaId(arenaId);
			if (lat != null && lng != null) {
				if (arena.getLatitude() != null && arena.getLongitude() != null) {
					double km = round2(haversine(lat, lng, arena.getLatitude(), arena.getLongitude()));
					if (km > arenaLocationToleranceKm) {
						throw new BadRequestException(String.format(
								"Game location must be within %.1f km of the arena (distance: %.2f km)",
								arenaLocationToleranceKm, km));
					}
				}
			} else {
				lat = arena.getLatitude();
				lng = arena.getLongitude();
			}
		} else {
			game.setArenaId(null);
		}

		if (lat == null || lng == null) {
			throw new BadRequestException("Location is required (arena or map coordinates)");
		}

		game.setLatitude(lat);
		game.setLongitude(lng);
	}

	private String extractBearerToken(String token) {
		if (token == null || token.isBlank()) {
			throw new UnauthorizedException("Missing authorization token");
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
