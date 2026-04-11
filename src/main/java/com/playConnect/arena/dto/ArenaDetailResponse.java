package com.playConnect.arena.dto;

import com.playConnect.arena.entity.Arena;

public class ArenaDetailResponse {
	private Long id;
	private String name;
	private String address;
	private Double latitude;
	private Double longitude;
	private String imageUrl;

	public ArenaDetailResponse(Arena arena) {
		this.id = arena.getId();
		this.name = arena.getName();
		this.address = arena.getAddress();
		this.latitude = arena.getLatitude();
		this.longitude = arena.getLongitude();
		this.imageUrl = arena.getImageUrl();
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

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public String getImageUrl() {
		return imageUrl;
	}
}
