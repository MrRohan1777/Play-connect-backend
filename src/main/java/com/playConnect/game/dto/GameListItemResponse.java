package com.playConnect.game.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.playConnect.game.entity.Game;

public class GameListItemResponse {
	private Long id;
	private Long arenaId;
	private String sport;
	private Double latitude;
	private Double longitude;
	private LocalDate date;
	private LocalTime time;
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
		this.date = game.getDate();
		this.time = game.getTime();
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

	public LocalDate getDate() {
		return date;
	}

	public LocalTime getTime() {
		return time;
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
