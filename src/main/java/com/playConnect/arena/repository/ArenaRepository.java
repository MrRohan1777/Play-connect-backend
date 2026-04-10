package com.playConnect.arena.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.playConnect.arena.entity.Arena;

@Repository
public interface ArenaRepository extends JpaRepository<Arena, Long> {
}
