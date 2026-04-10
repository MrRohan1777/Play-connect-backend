package com.playConnect.game.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Game_Participation")
public class GameParticipation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Long gameId;
	
	private Long leaderId;

	private String leaderEmail;

	private String leaderPhone;

	private Integer playersCount;

	private String status;
	
	private String removalReason;

	private LocalDateTime createdAt = LocalDateTime.now();

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

	public String getLeaderEmail() {
		return leaderEmail;
	}

	public void setLeaderEmail(String leaderEmail) {
		this.leaderEmail = leaderEmail;
	}

	public String getLeaderPhone() {
		return leaderPhone;
	}

	public void setLeaderPhone(String leaderPhone) {
		this.leaderPhone = leaderPhone;
	}

	public Integer getPlayersCount() {
		return playersCount;
	}

	public void setPlayersCount(Integer playersCount) {
		this.playersCount = playersCount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Expose participation status in JSON as `participationStatus` (not `status`).
	 */
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Long getLeaderId() {
		return leaderId;
	}

	public void setLeaderId(Long leaderId) {
		this.leaderId = leaderId;
	}

	public String getRemovalReason() {
		return removalReason;
	}

	public void setRemovalReason(String removalReason) {
		this.removalReason = removalReason;
	}
	
	
	
	
}
