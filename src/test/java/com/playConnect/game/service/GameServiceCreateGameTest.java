package com.playConnect.game.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.playConnect.arena.entity.Arena;
import com.playConnect.arena.repository.ArenaRepository;
import com.playConnect.exception.BadRequestException;
import com.playConnect.game.dto.CreateGameRequest;
import com.playConnect.game.dto.CreateGameResponse;
import com.playConnect.game.entity.Game;
import com.playConnect.game.entity.GamePlayer;
import com.playConnect.game.repository.GamePlayerRepository;
import com.playConnect.game.repository.GameRepository;
import com.playConnect.security.securityConfig.JwtUtil;
import com.playConnect.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class GameServiceCreateGameTest {

	@Mock
	private GameRepository gameRepository;
	@Mock
	private JwtUtil jwtUtil;
	@Mock
	private GamePlayerRepository gamePlayerRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ArenaRepository arenaRepository;

	@InjectMocks
	private GameService gameService;

	@Test
	void createGame_pastStart_rejected() {
		when(jwtUtil.extractUserId("token")).thenReturn(1L);
		CreateGameRequest req = baseRequest();
		req.setStartTime(LocalDateTime.now().minusHours(1));
		assertThrows(BadRequestException.class, () -> gameService.createGame("Bearer token", req));
	}

	@Test
	void createGame_playersNotGreaterThanOne_rejected() {
		when(jwtUtil.extractUserId("token")).thenReturn(1L);
		CreateGameRequest req = baseRequest();
		req.setTotalPlayers(1);
		assertThrows(BadRequestException.class, () -> gameService.createGame("Bearer token", req));
	}

	@Test
	void createGame_missingLocation_rejected() {
		when(jwtUtil.extractUserId("token")).thenReturn(1L);
		CreateGameRequest req = baseRequest();
		req.setLatitude(null);
		req.setLongitude(null);
		req.setArenaId(null);
		BadRequestException ex = assertThrows(BadRequestException.class,
				() -> gameService.createGame("Bearer token", req));
		assertEquals("Location is required (arena or map coordinates)", ex.getMessage());
	}

	@Test
	void createGame_withArenaDropdown_usesArenaCoords() {
		when(jwtUtil.extractUserId("token")).thenReturn(5L);
		Arena arena = new Arena();
		arena.setId(10L);
		arena.setLatitude(18.5);
		arena.setLongitude(73.8);
		when(arenaRepository.findById(10L)).thenReturn(Optional.of(arena));
		when(gameRepository.save(any(Game.class))).thenAnswer(inv -> {
			Game g = inv.getArgument(0);
			g.setId(14L);
			return g;
		});

		CreateGameRequest req = baseRequest();
		req.setArenaId(10L);
		req.setLatitude(null);
		req.setLongitude(null);

		CreateGameResponse res = gameService.createGame("Bearer token", req);
		assertEquals("Game created", res.getMessage());
		assertEquals(14L, res.getGameId());
		verify(gamePlayerRepository).save(any(GamePlayer.class));
	}

	@Test
	void createGame_withMapPin_succeeds() {
		when(jwtUtil.extractUserId("token")).thenReturn(5L);
		when(gameRepository.save(any(Game.class))).thenAnswer(inv -> {
			Game g = inv.getArgument(0);
			g.setId(20L);
			return g;
		});

		CreateGameRequest req = baseRequest();
		req.setLatitude(19.07);
		req.setLongitude(72.87);
		req.setArenaId(null);

		CreateGameResponse res = gameService.createGame("Bearer token", req);
		assertEquals(20L, res.getGameId());
		verify(gamePlayerRepository).save(any(GamePlayer.class));
	}

	private static CreateGameRequest baseRequest() {
		CreateGameRequest req = new CreateGameRequest();
		req.setSport("football");
		req.setStartTime(LocalDateTime.now().plusDays(1));
		req.setTotalPlayers(4);
		req.setLatitude(19.0);
		req.setLongitude(73.0);
		return req;
	}
}
