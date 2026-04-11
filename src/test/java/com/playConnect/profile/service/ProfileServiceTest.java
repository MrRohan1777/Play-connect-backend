package com.playConnect.profile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.playConnect.arena.repository.ArenaRepository;
import com.playConnect.game.repository.GamePlayerRepository;
import com.playConnect.game.repository.GameRepository;
import com.playConnect.profile.dto.ProfileResponse;
import com.playConnect.user.entity.User;
import com.playConnect.user.enums.SkillLevel;
import com.playConnect.user.repository.UserRepository;
import com.playConnect.utilities.AppConstants;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private GameRepository gameRepository;

	@Mock
	private GamePlayerRepository gamePlayerRepository;

	@Mock
	private ArenaRepository arenaRepository;

	@InjectMocks
	private ProfileService profileService;

	@Test
	void getProfile_scoreUsesJoinedHostedAndWins() {
		User user = new User();
		user.setId(10L);
		user.setName("Rohan");
		user.setEmail("r@e.com");
		user.setSkillLevel(SkillLevel.INTERMEDIATE);
		user.setCity("Kolhapur");
		user.setCreatedAt(LocalDateTime.of(2026, 4, 1, 12, 0));

		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gamePlayerRepository.countByUser_IdAndStatus(10L, AppConstants.JOINED)).thenReturn(20L);
		when(gameRepository.countByCreatedBy(10L)).thenReturn(5L);
		when(gamePlayerRepository.countDistinctPlayersMet(10L, AppConstants.JOINED)).thenReturn(242L);
		when(gameRepository.countWinsForUserSince(eq(10L), any())).thenReturn(12L);
		when(gameRepository.findUpcomingInvolvingUser(eq(10L), any(Pageable.class)))
				.thenReturn(Collections.emptyList());

		ProfileResponse r = profileService.getProfile(10L);

		// gamesJoined*2 + gamesHosted*5 + winsThisWeek*10
		assertEquals(20 * 2 + 5 * 5 + 12 * 10, r.getScore());
		assertEquals(20, r.getGamesJoined());
		assertEquals(5, r.getGamesHosted());
		assertEquals(12, r.getWinsThisWeek());
		assertEquals(242, r.getPlayersMet());
		assertEquals("Intermediate", r.getLevel());
		assertNull(r.getNextGame());
	}
}
