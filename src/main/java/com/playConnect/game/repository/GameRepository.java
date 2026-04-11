package com.playConnect.game.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.playConnect.game.entity.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

	@Query("""
			    SELECT g
			    FROM Game g
			    WHERE g.status = 'ACTIVE'
			    AND g.startTime > CURRENT_TIMESTAMP
			""")
	List<Game> findUpcomingActiveGames();

	@Query("""
			    SELECT g
			    FROM Game g
			    WHERE g.status = 'ACTIVE'
			    AND g.arenaId = :arenaId
			    AND g.startTime > CURRENT_TIMESTAMP
			""")
	List<Game> findUpcomingActiveGamesByArenaId(Long arenaId);

	long countByCreatedBy(Long createdBy);

	@Query("""
			SELECT COUNT(g) FROM Game g
			WHERE g.winnerId = :userId
			AND g.startTime >= :since
			""")
	long countWinsForUserSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}
