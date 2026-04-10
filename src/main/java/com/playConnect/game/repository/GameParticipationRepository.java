package com.playConnect.game.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.playConnect.game.entity.GameParticipation;

@Repository
public interface GameParticipationRepository extends JpaRepository<GameParticipation, Long> {

    Optional<GameParticipation> findByGameIdAndLeaderIdAndStatus(Long gameId, Long leaderId, String status);
    boolean existsByGameIdAndLeaderIdAndStatus(Long gameId, Long leaderId, String status);
    long countByGameIdAndStatus(Long gameId, String status);

    @Query("SELECT gp.gameId, COUNT(gp.id) FROM GameParticipation gp WHERE gp.gameId IN :gameIds AND gp.status = :status GROUP BY gp.gameId")
    List<Object[]> countJoinedByGameIds(@Param("gameIds") List<Long> gameIds, @Param("status") String status);
}