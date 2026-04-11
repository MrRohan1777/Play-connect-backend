package com.playConnect.game.dto;

import java.time.LocalDateTime;

import com.playConnect.game.entity.Game;

public class GameListItemResponse {
	private Long id;
	private Long arenaId;
	private String sport;
	private Double latitude;
	private Double longitude;
	private LocalDateTime startTime;
	private Integer totalSlots;
	private Integer slotsLeft;
	private Double distanceKm;
	private Double fillRatio;
	private LocalDateTime createdAt;

	public GameListItemResponse(Game game, Double distanceKm, Integer slotsLeft, Double fillRatio) {
		this.id = game.getId();
		this.arenaId = game.getArenaId();
		this.sport = game.getSport();
		this.latitude = game.getLatitude();
		this.longitude = game.getLongitude();
		this.startTime = game.getStartTime();
		this.totalSlots = game.getTotalPlayers();
		this.slotsLeft = slotsLeft;
		this.distanceKm = distanceKm;
		this.fillRatio = fillRatio;
		this.createdAt = game.getCreatedAt();
	}

	public Long getId() {
		return id;
	}

	public String getSport() {
		return sport;
	}

	public Long getArenaId() {
		return arenaId;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public Integer getTotalSlots() {
		return totalSlots;
	}

	public Integer getSlotsLeft() {
		return slotsLeft;
	}

	public Double getDistanceKm() {
		return distanceKm;
	}

	public Double getFillRatio() {
		return fillRatio;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
