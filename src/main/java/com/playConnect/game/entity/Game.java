package com.playConnect.game.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long hostId;

    private String sport;

    private Double latitude;

    private Double longitude;

    private LocalDate date;

    private LocalTime time;

    private Integer totalPlayers;

    private Integer remainingSpots;

    private String contactNumber;

    private String email;
    
    private String status;
    
    private Integer cancelBeforeMinutes;
    
    private String hostCancelReason;
    
    @Transient
    private String distance;

    private LocalDateTime createdAt = LocalDateTime.now();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getHostId() {
		return hostId;
	}

	public void setHostId(Long hostId) {
		this.hostId = hostId;
	}

	public String getSport() {
		return sport;
	}

	public void setSport(String sport) {
		this.sport = sport;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	@JsonIgnore
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Expose status in JSON as `gameStatus` (not `status`) to avoid confusion with participation status.
	 */
	@JsonProperty("gameStatus")
	public String getGameStatus() {
		return status;
	}

	@JsonProperty("gameStatus")
	public void setGameStatus(String gameStatus) {
		this.status = gameStatus;
	}

	@JsonIgnore
	public String getStatusRaw() {
		return status;
	}

	public Integer getCancelBeforeMinutes() {
		return cancelBeforeMinutes;
	}

	public void setCancelBeforeMinutes(Integer cancelBeforeMinutes) {
		this.cancelBeforeMinutes = cancelBeforeMinutes;
	}

	public String getHostCancelReason() {
		return hostCancelReason;
	}

	public void setHostCancelReason(String hostCancelReason) {
		this.hostCancelReason = hostCancelReason;
	}
	
	
	
	

    
}