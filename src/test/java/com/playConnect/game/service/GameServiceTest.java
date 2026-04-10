package com.playConnect.game.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.playConnect.game.entity.Game;
import com.playConnect.game.entity.GameParticipation;
import com.playConnect.game.repository.GameParticipationRepository;
import com.playConnect.game.repository.GameRepository;
import com.playConnect.security.securityConfig.JwtUtil;
import com.playConnect.utilities.AppConstants;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

	@Mock
	private GameRepository gameRepository;

	@Mock
	private GameParticipationRepository gameParticipationRepository;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private GameService gameService;

	private Game game;

	@BeforeEach
	void setup() {
		game = new Game();
		game.setId(1L);
		game.setHostId(100L);
		game.setSport("football");
		game.setLatitude(19.07);
		game.setLongitude(72.87);
		game.setDate(LocalDate.now().plusDays(1));
		game.setTime(LocalTime.of(10, 0));
		game.setTotalPlayers(2);
		game.setStatus(AppConstants.ACTIVE);
	}

	@Test
	void joinUntilFull_lastJoinFails() {
		when(jwtUtil.extractUserId("token")).thenReturn(200L);
		when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
		when(gameParticipationRepository.existsByGameIdAndLeaderIdAndStatus(1L, 200L, AppConstants.JOINED))
				.thenReturn(false);
		when(gameParticipationRepository.countByGameIdAndStatus(1L, AppConstants.JOINED)).thenReturn(2L);

		RuntimeException ex = assertThrows(RuntimeException.class, () -> gameService.joinGame(1L, "Bearer token"));
		assertEquals("Game is full", ex.getMessage());
		verify(gameParticipationRepository, never()).save(any(GameParticipation.class));
	}

	@Test
	void sameUserJoinsTwice_blocked() {
		when(jwtUtil.extractUserId("token")).thenReturn(200L);
		when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
		when(gameParticipationRepository.existsByGameIdAndLeaderIdAndStatus(1L, 200L, AppConstants.JOINED))
				.thenReturn(true);

		RuntimeException ex = assertThrows(RuntimeException.class, () -> gameService.joinGame(1L, "Bearer token"));
		assertEquals("Cannot join the same game twice", ex.getMessage());
		verify(gameParticipationRepository, never()).countByGameIdAndStatus(any(Long.class), any(String.class));
	}

	@Test
	void pastGame_notJoinable() {
		game.setDate(LocalDate.now().minusDays(1));
		when(jwtUtil.extractUserId("token")).thenReturn(200L);
		when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

		RuntimeException ex = assertThrows(RuntimeException.class, () -> gameService.joinGame(1L, "Bearer token"));
		assertEquals("Cannot join past games", ex.getMessage());
		verify(gameParticipationRepository, never()).existsByGameIdAndLeaderIdAndStatus(any(Long.class), any(Long.class),
				eq(AppConstants.JOINED));
	}
}
