package com.playConnect.game.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateGameRequest {

	@NotBlank(message = "sport is required")
	private String sport;

	private Long arenaId;

	private Double latitude;

	private Double longitude;

	@NotNull(message = "startTime is required")
	@Future(message = "Cannot create past game")
	private LocalDateTime startTime;

	@NotNull(message = "totalPlayers is required")
	@Min(value = 2, message = "totalPlayers must be greater than 1")
	private Integer totalPlayers;

	public String getSport() {
		return sport;
	}

	public void setSport(String sport) {
		this.sport = sport;
	}

	public Long getArenaId() {
		return arenaId;
	}

	public void setArenaId(Long arenaId) {
		this.arenaId = arenaId;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public Integer getTotalPlayers() {
		return totalPlayers;
	}

	public void setTotalPlayers(Integer totalPlayers) {
		this.totalPlayers = totalPlayers;
	}
}
