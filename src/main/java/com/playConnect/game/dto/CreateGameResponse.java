package com.playConnect.game.dto;

public class CreateGameResponse {

	private String message;
	private Long gameId;

	public CreateGameResponse(String message, Long gameId) {
		this.message = message;
		this.gameId = gameId;
	}

	public String getMessage() {
		return message;
	}

	public Long getGameId() {
		return gameId;
	}
}
