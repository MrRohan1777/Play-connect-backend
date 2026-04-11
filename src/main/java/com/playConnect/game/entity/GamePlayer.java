package com.playConnect.game.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.playConnect.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "game_players", indexes = {
		@Index(name = "idx_game_players_game_id", columnList = "gameId"),
		@Index(name = "idx_game_players_user_id", columnList = "user_id"),
		@Index(name = "idx_game_players_game_status", columnList = "gameId,status")
})
public class GamePlayer {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "game_id", nullable = false)
	private Long gameId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private String status;

	private String removalReason;

	private Integer playersCount;

	@Column(name = "joined_at", nullable = false)
	private LocalDateTime joinedAt = LocalDateTime.now();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGameId() {
		return gameId;
	}

	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty("participationStatus")
	public String getParticipationStatus() {
		return status;
	}

	@JsonProperty("participationStatus")
	public void setParticipationStatus(String participationStatus) {
		this.status = participationStatus;
	}

	@JsonIgnore
	public String getStatusRaw() {
		return status;
	}

	public String getRemovalReason() {
		return removalReason;
	}

	public void setRemovalReason(String removalReason) {
		this.removalReason = removalReason;
	}

	public Integer getPlayersCount() {
		return playersCount;
	}

	public void setPlayersCount(Integer playersCount) {
		this.playersCount = playersCount;
	}

	public LocalDateTime getJoinedAt() {
		return joinedAt;
	}

	public void setJoinedAt(LocalDateTime joinedAt) {
		this.joinedAt = joinedAt;
	}
}
