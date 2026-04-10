package com.playConnect.game.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.playConnect.game.entity.GameParticipation;

@Repository
public interface GameParticipationRepository extends JpaRepository<GameParticipation, Long> {

    Optional<GameParticipation> findByGameIdAndLeaderEmailAndStatus(Long gameId, String leaderEmail, String status);
    boolean existsByLeaderIdAndStatus(Long leaderId, String status);
    List<GameParticipation> findByLeaderId(Long leaderId);
    
}