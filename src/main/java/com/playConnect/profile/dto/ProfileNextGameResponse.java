package com.playConnect.profile.dto;

import java.time.LocalDate;

public class ProfileNextGameResponse {

	private String sport;
	private LocalDate date;
	private String time;
	private String arenaName;

	public ProfileNextGameResponse() {
	}

	public ProfileNextGameResponse(String sport, LocalDate date, String time, String arenaName) {
		this.sport = sport;
		this.date = date;
		this.time = time;
		this.arenaName = arenaName;
	}

	public String getSport() {
		return sport;
	}

	public void setSport(String sport) {
		this.sport = sport;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getArenaName() {
		return arenaName;
	}

	public void setArenaName(String arenaName) {
		this.arenaName = arenaName;
	}
}
