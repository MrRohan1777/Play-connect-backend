package com.playConnect.user.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateLocationRequest {

	@NotNull(message = "Latitude is required")
	private Double latitude;

	@NotNull(message = "Longitude is required")
	private Double longitude;

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
}
