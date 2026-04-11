package com.playConnect.game.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.playConnect.game.entity.Game;
import com.playConnect.game.entity.GamePlayer;
import com.playConnect.game.repository.GamePlayerRepository;
import com.playConnect.game.repository.GameRepository;
import com.playConnect.security.securityConfig.JwtUtil;
import com.playConnect.user.repository.UserRepository;
import com.playConnect.utilities.AppConstants;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

	@Mock
	private GameRepository gameRepository;

	@Mock
	private GamePlayerRepository gamePlayerRepository;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private GameService gameService;

	private Game game;

	@BeforeEach
	void setup() {
		game = new Game();
		game.setId(1L);
		game.setCreatedBy(100L);
		game.setSport("football");
		game.setLatitude(19.07);
		game.setLongitude(72.87);
		game.setStartTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
		game.setTotalPlayers(2);
		game.setStatus(AppConstants.ACTIVE);
	}

	@Test
	void joinUntilFull_lastJoinFails() {
		when(jwtUtil.extractUserId("token")).thenReturn(200L);
		when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
		when(gamePlayerRepository.existsByGameIdAndUser_IdAndStatus(1L, 200L, AppConstants.JOINED))
				.thenReturn(false);
		when(gamePlayerRepository.countByGameIdAndStatus(1L, AppConstants.JOINED)).thenReturn(2L);

		RuntimeException ex = assertThrows(RuntimeException.class, () -> gameService.joinGame(1L, "Bearer token"));
		assertEquals("Game is full", ex.getMessage());
		verify(gamePlayerRepository, never()).save(any(GamePlayer.class));
	}

	@Test
	void sameUserJoinsTwice_blocked() {
		when(jwtUtil.extractUserId("token")).thenReturn(200L);
		when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
		when(gamePlayerRepository.existsByGameIdAndUser_IdAndStatus(1L, 200L, AppConstants.JOINED))
				.thenReturn(true);

		RuntimeException ex = assertThrows(RuntimeException.class, () -> gameService.joinGame(1L, "Bearer token"));
		assertEquals("Cannot join the same game twice", ex.getMessage());
		verify(gamePlayerRepository, never()).countByGameIdAndStatus(any(Long.class), any(String.class));
	}

	@Test
	void pastGame_notJoinable() {
		game.setStartTime(LocalDateTime.now().minusDays(1));
		when(jwtUtil.extractUserId("token")).thenReturn(200L);
		when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

		RuntimeException ex = assertThrows(RuntimeException.class, () -> gameService.joinGame(1L, "Bearer token"));
		assertEquals("Cannot join past games", ex.getMessage());
		verify(gamePlayerRepository, never()).existsByGameIdAndUser_IdAndStatus(any(Long.class), any(Long.class),
				eq(AppConstants.JOINED));
	}
}
