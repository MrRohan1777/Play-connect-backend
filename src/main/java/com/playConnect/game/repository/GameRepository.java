package com.playConnect.game.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.playConnect.game.entity.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

	@Query("""
			    SELECT g
			    FROM Game g
			    WHERE g.status = 'ACTIVE'
			    AND (g.date > CURRENT_DATE OR (g.date = CURRENT_DATE AND g.time > CURRENT_TIME))
			""")
	List<Game> findUpcomingActiveGames();

	@Query("""
			    SELECT g
			    FROM Game g
			    WHERE g.status = 'ACTIVE'
			    AND g.arenaId = :arenaId
			    AND (g.date > CURRENT_DATE OR (g.date = CURRENT_DATE AND g.time > CURRENT_TIME))
			""")
	List<Game> findUpcomingActiveGamesByArenaId(Long arenaId);

}
