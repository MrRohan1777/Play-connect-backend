package com.playConnect.profile.dto;

public class ProfileResponse {
	private String name;
	private int score;
	private int winsThisWeek;
	private int playersMet;
	private int gamesJoined;
	private int gamesHosted;

	public ProfileResponse(String name, int score, int winsThisWeek, int playersMet, int gamesJoined, int gamesHosted) {
		this.name = name;
		this.score = score;
		this.winsThisWeek = winsThisWeek;
		this.playersMet = playersMet;
		this.gamesJoined = gamesJoined;
		this.gamesHosted = gamesHosted;
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	public int getWinsThisWeek() {
		return winsThisWeek;
	}

	public int getPlayersMet() {
		return playersMet;
	}

	public int getGamesJoined() {
		return gamesJoined;
	}

	public int getGamesHosted() {
		return gamesHosted;
	}
}
