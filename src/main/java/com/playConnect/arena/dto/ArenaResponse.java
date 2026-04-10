package com.playConnect.arena.dto;

import com.playConnect.arena.entity.Arena;

public class ArenaResponse {
	private Long id;
	private String name;
	private String address;
	private String imageUrl;
	private double distance;

	public ArenaResponse(Arena arena, double distance) {
		this.id = arena.getId();
		this.name = arena.getName();
		this.address = arena.getAddress();
		this.imageUrl = arena.getImageUrl();
		this.distance = distance;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public double getDistance() {
		return distance;
	}
}
