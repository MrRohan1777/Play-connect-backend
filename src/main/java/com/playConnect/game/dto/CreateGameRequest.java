package com.playConnect.game.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class CreateGameRequest {

    private String sport;
    
    private Long arenaId;

    private Double latitude;

    private Double longitude;

    private LocalDate date;

    private LocalTime time;

    private Integer totalPlayers;

    private Integer remainingSpots;

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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public Integer getTotalPlayers() {
		return totalPlayers;
	}

	public void setTotalPlayers(Integer totalPlayers) {
		this.totalPlayers = totalPlayers;
	}

	public Integer getRemainingSpots() {
		return remainingSpots;
	}

	public void setRemainingSpots(Integer remainingSpots) {
		this.remainingSpots = remainingSpots;
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