package com.playConnect.game.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.playConnect.game.dto.CreateGameRequest;
import com.playConnect.game.dto.JoinedGameResponse;
import com.playConnect.game.dto.MyGameItemResponse;
import com.playConnect.game.dto.MyGameResponse;
import com.playConnect.game.dto.NearbyGamesResponse;
import com.playConnect.game.dto.ParticipateGameRequest;
import com.playConnect.game.dto.RemovePlayerRequest;
import com.playConnect.game.entity.Game;
import com.playConnect.game.entity.GameParticipation;
import com.playConnect.game.repository.GameParticipationRepository;
import com.playConnect.game.repository.GameRepository;
import com.playConnect.mapper.UserMapper;
import com.playConnect.security.securityConfig.JwtUtil;
import com.playConnect.utilities.AppConstants;

import jakarta.transaction.Transactional;

@Service
public class GameService {

	/** Active games first, then by scheduled date and time. */
	private static final Comparator<Game> ACTIVE_FIRST_THEN_SCHEDULE = Comparator
			.comparing((Game g) -> !AppConstants.ACTIVE.equalsIgnoreCase(g.getStatus()))
			.thenComparing(Game::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
			.thenComparing(Game::getTime, Comparator.nullsLast(Comparator.naturalOrder()));

	@Autowired
	private GameRepository gameRepository;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private GameParticipationRepository gameParticipationRepository;

	public Game hostGame(String token, CreateGameRequest request) {

		if (request.getRemainingSpots() > request.getTotalPlayers()) {
			throw new RuntimeException("Remaining spots cannot be greater than total players");
		}

		if (request.getRemainingSpots() < 0) {
			throw new RuntimeException("Remaining spots cannot be negative");
		}
		String cleanToken = token.replace("Bearer ", "");
		Long userId = jwtUtil.extractUserId(cleanToken);
		Game game = UserMapper.INSTANCE.gameDtoToEntity(request);
		game.setHostId(userId);
		game.setStatus(AppConstants.ACTIVE);

		return gameRepository.save(game);
	}

	public NearbyGamesResponse getNearbyGames(String sport, double lat, double lng, double radius) {

		List<Object[]> results = gameRepository.findNearbyGames(sport, lat, lng, radius);

		List<Game> games = results.stream().map(row -> {
			Game game = (Game) row[0];
			double distance = ((Number) row[1]).doubleValue();
			distance = Math.round(distance * 100.0) / 100.0;
			String distanceString = String.valueOf(distance) + AppConstants.KILOMETER;
			game.setDistance(distanceString);
			return game;
		}).collect(Collectors.toList());

		if (games.isEmpty()) {
			return new NearbyGamesResponse("No games found within " + radius + " km. Try increasing radius.", games);
		}

		return new NearbyGamesResponse("" + AppConstants.GAME_FOUND, games);

	}

	@Transactional
	public Long joinGame(Long gameId, ParticipateGameRequest request, String token) {

		Long userId = jwtUtil.extractUserId(token);

		Game game = gameRepository.findById(gameId).orElseThrow(() -> new RuntimeException("Game not found"));

		if (!AppConstants.ACTIVE.equalsIgnoreCase(game.getStatus())) {
			throw new RuntimeException("Game is not active");
		}

		if (request.getPlayersCount() <= 0) {
			throw new RuntimeException("Players count must be greater than zero");
		}

		if (request.getPlayersCount() > game.getRemainingSpots()) {
			throw new RuntimeException("Not enough spots available");
		}

		if (gameParticipationRepository.existsByLeaderIdAndStatus(userId, AppConstants.JOINED)) {
			throw new RuntimeException("You are already participating in another game");
		}

		boolean alreadyJoined = gameParticipationRepository
				.findByGameIdAndLeaderEmailAndStatus(gameId, request.getLeaderEmail(), AppConstants.JOINED).isPresent();

		if (alreadyJoined) {
			throw new RuntimeException("You already joined this game");
		}

		// update remaining spots
		game.setRemainingSpots(game.getRemainingSpots() - request.getPlayersCount());

		gameRepository.save(game);

		// save join record
		GameParticipation join = new GameParticipation();

		join.setGameId(gameId);
		join.setLeaderEmail(request.getLeaderEmail());
		join.setLeaderPhone(request.getLeaderPhone());
		join.setPlayersCount(request.getPlayersCount());
		join.setStatus(AppConstants.JOINED);
		join.setLeaderId(userId);

		GameParticipation saved = gameParticipationRepository.save(join);
		// here call async method for notification Host

		return saved.getId();
	}

	@Transactional
	public String cancelGame(Long gameId, String token, RemovePlayerRequest request) {

		Long userId = jwtUtil.extractUserId(token);
		Game game = gameRepository.findById(gameId).orElseThrow(() -> new RuntimeException("Game not found"));

		if (!game.getHostId().equals(userId)) {
			throw new RuntimeException("Only host can cancel this game");
		}

		if (!AppConstants.ACTIVE.equals(game.getStatus())) {
			throw new RuntimeException("Game already cancelled or completed");
		}

		game.setStatus(AppConstants.CANCELLED_HOST);
		game.setHostCancelReason(request.getReason());

		gameRepository.save(game);
		// here call async method for notification All playes

		return "Game cancelled successfully";
	}

	@Transactional
	public String leaveGame(Long participationId, String token, RemovePlayerRequest request) {

		Long userId = jwtUtil.extractUserId(token);
		GameParticipation participation = gameParticipationRepository.findById(participationId)
				.orElseThrow(() -> new RuntimeException("Participation not found"));

		if (!participation.getLeaderId().equals(userId)) {
			throw new RuntimeException("You are not allowed to leave this game");
		}

		if (!AppConstants.JOINED.equals(participation.getStatus())) {
			throw new RuntimeException("Participation already cancelled");
		}

		Game game = gameRepository.findById(participation.getGameId())
				.orElseThrow(() -> new RuntimeException("Game not found"));

		// cancel window validation
		LocalDateTime gameStartTime = LocalDateTime.of(game.getDate(), game.getTime());

		LocalDateTime cancelDeadline = gameStartTime.minusMinutes(game.getCancelBeforeMinutes());

		if (LocalDateTime.now().isAfter(cancelDeadline)) {
			throw new RuntimeException("Cancellation window closed");
		}

		// update participation
		participation.setStatus(AppConstants.CANCELLED_BY_PLAYER);
		participation.setRemovalReason(request.getReason());

		// restore spots
		game.setRemainingSpots(game.getRemainingSpots() + participation.getPlayersCount());

		gameParticipationRepository.save(participation);
		gameRepository.save(game);
		// here call async method for notification to host for cancelation.

		return "You have successfully left the game";
	}

	@Transactional
	public String removePlayer(Long gameId, Long participationId, String token, RemovePlayerRequest request) {

		Long userId = jwtUtil.extractUserId(token);

		Game game = gameRepository.findById(gameId).orElseThrow(() -> new RuntimeException("Game not found"));

		if (!game.getHostId().equals(userId)) {
			throw new RuntimeException("Only host can remove players");
		}

		GameParticipation participation = gameParticipationRepository.findById(participationId)
				.orElseThrow(() -> new RuntimeException("Participation not found"));

		if (!participation.getGameId().equals(gameId)) {
			throw new RuntimeException("Participation does not belong to this game");
		}

		if (!AppConstants.JOINED.equals(participation.getStatus())) {
			throw new RuntimeException("Player already removed or cancelled");
		}

		participation.setStatus(AppConstants.REMOVED_BY_HOST);
		System.out.println("Reason : " + request.getReason());
		participation.setRemovalReason(request.getReason());

		game.setRemainingSpots(game.getRemainingSpots() + participation.getPlayersCount());

		gameParticipationRepository.save(participation);
		gameRepository.save(game);
		// here call async method for notification to host for kiking out.

		return "Player removed successfully";
	}

	private void sortMyGameItemsActiveFirst(List<MyGameItemResponse> list) {
		list.sort(Comparator.comparing(MyGameItemResponse::getGame, ACTIVE_FIRST_THEN_SCHEDULE));
	}

	private void sortJoinedGamesActiveFirst(List<JoinedGameResponse> list) {
		list.sort(Comparator.comparing(JoinedGameResponse::getGame, ACTIVE_FIRST_THEN_SCHEDULE));
	}

	public List<MyGameItemResponse> getHostedGamesByUserId(Long userId) {
		List<MyGameItemResponse> list = new ArrayList<>();
		for (Game game : gameRepository.findByHostId(userId)) {
			list.add(new MyGameItemResponse(
					game,
					true,
					null,
					game.getHostCancelReason(),
					userId,
					null));
		}
		sortMyGameItemsActiveFirst(list);
		return list;
	}

	public List<JoinedGameResponse> getJoinedGamesByUserId(Long userId) {
		List<GameParticipation> participations = gameParticipationRepository.findByLeaderId(userId);
		List<JoinedGameResponse> response = new ArrayList<>();
		for (GameParticipation p : participations) {
			Game game = gameRepository.findById(p.getGameId()).orElse(null);
			if (game == null) {
				continue;
			}
			response.add(new JoinedGameResponse(
					game,
					p.getId(),
					p.getStatus(),
					p.getRemovalReason(),
					p.getPlayersCount()));
		}
		sortJoinedGamesActiveFirst(response);
		return response;
	}

	public MyGameResponse getMyGames(String token) {

		Long userId = jwtUtil.extractUserId(token);
		
	    List<MyGameItemResponse> responseList = new ArrayList<>(getHostedGamesByUserId(userId));

	    //  Participated games
	    List<GameParticipation> participations =
	    		gameParticipationRepository.findByLeaderId(userId);

	    for (GameParticipation participation : participations) {

	        Game game = gameRepository.findById(participation.getGameId())
	                .orElse(null);

	        if (game == null) continue;

	        MyGameItemResponse item = new MyGameItemResponse(
	                game,
	                false,
	                participation.getStatus(),
	                participation.getRemovalReason(),
	                userId,
	                participation.getId()
	        );

	        responseList.add(item);
	    }
	    sortMyGameItemsActiveFirst(responseList);
	    // NOTE : If user is participatnt then only have removal reason if user is Host  
	    return new MyGameResponse(responseList);
	}
	
	public List<JoinedGameResponse> getJoinedGames(String token) {
		Long userId = jwtUtil.extractUserId(token);
		return getJoinedGamesByUserId(userId);
	}
}
