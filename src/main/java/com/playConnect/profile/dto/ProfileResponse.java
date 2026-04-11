package com.playConnect.profile.dto;

import java.time.LocalDate;

public class ProfileResponse {

	private String name;
	private String email;
	private String level;
	private String city;
	private LocalDate joinedDate;
	private String profileImage;

	private int score;
	private int gamesJoined;
	private int gamesHosted;
	private int winsThisWeek;
	private int playersMet;

	private ProfileNextGameResponse nextGame;

	public ProfileResponse() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public LocalDate getJoinedDate() {
		return joinedDate;
	}

	public void setJoinedDate(LocalDate joinedDate) {
		this.joinedDate = joinedDate;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getGamesJoined() {
		return gamesJoined;
	}

	public void setGamesJoined(int gamesJoined) {
		this.gamesJoined = gamesJoined;
	}

	public int getGamesHosted() {
		return gamesHosted;
	}

	public void setGamesHosted(int gamesHosted) {
		this.gamesHosted = gamesHosted;
	}

	public int getWinsThisWeek() {
		return winsThisWeek;
	}

	public void setWinsThisWeek(int winsThisWeek) {
		this.winsThisWeek = winsThisWeek;
	}

	public int getPlayersMet() {
		return playersMet;
	}

	public void setPlayersMet(int playersMet) {
		this.playersMet = playersMet;
	}

	public ProfileNextGameResponse getNextGame() {
		return nextGame;
	}

	public void setNextGame(ProfileNextGameResponse nextGame) {
		this.nextGame = nextGame;
	}
}
