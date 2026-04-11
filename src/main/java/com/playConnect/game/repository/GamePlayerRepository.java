package com.playConnect.game.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.playConnect.game.entity.GamePlayer;

@Repository
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {

	Optional<GamePlayer> findByGameIdAndUser_IdAndStatus(Long gameId, Long userId, String status);

	boolean existsByGameIdAndUser_IdAndStatus(Long gameId, Long userId, String status);

	long countByGameIdAndStatus(Long gameId, String status);

	long countByUser_IdAndStatus(Long userId, String status);

	@Query("SELECT gp.gameId, COUNT(gp.id) FROM GamePlayer gp WHERE gp.gameId IN :gameIds AND gp.status = :status GROUP BY gp.gameId")
	List<Object[]> countJoinedByGameIds(@Param("gameIds") List<Long> gameIds, @Param("status") String status);

	/**
	 * Co-participants met: sums {@code COALESCE(playersCount, 1)} per other row in the same games,
	 * counting both JOINED and LEFT so brief sessions still count. Multi-seat rows (playersCount &gt; 1)
	 * contribute that many toward the total.
	 */
	@Query("""
			SELECT COALESCE(SUM(COALESCE(gp2.playersCount, 1)), 0)
			FROM GamePlayer gp1
			JOIN GamePlayer gp2 ON gp1.gameId = gp2.gameId
			WHERE gp1.user.id = :userId
			AND gp2.user.id <> :userId
			AND gp1.status IN :metStatuses
			AND gp2.status IN :metStatuses
			""")
	long sumPlayersMet(@Param("userId") Long userId, @Param("metStatuses") Collection<String> metStatuses);
}
