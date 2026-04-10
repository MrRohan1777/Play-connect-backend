package com.playConnect.game.dto;

import java.util.List;

public class GameListResponse {
	private List<GameListItemResponse> games;

	public GameListResponse(List<GameListItemResponse> games) {
		this.games = games;
	}

	public List<GameListItemResponse> getGames() {
		return games;
	}
}
