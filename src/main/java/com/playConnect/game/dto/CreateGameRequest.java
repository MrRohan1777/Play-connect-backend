package com.playConnect.game.dto;

import java.time.LocalDateTime;

public class CreateGameRequest {

	private String sport;

	private Long arenaId;

	private Double latitude;

	private Double longitude;

	private LocalDateTime startTime;

	private Integer totalPlayers;

	private String contactNumber;

	private String email;

	private Integer cancelBeforeMinutes;

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

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getCancelBeforeMinutes() {
		return cancelBeforeMinutes;
	}

	public void setCancelBeforeMinutes(Integer cancelBeforeMinutes) {
		this.cancelBeforeMinutes = cancelBeforeMinutes;
	}
}
