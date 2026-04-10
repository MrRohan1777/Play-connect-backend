package com.playConnect.game.dto;

import java.util.List;

public class MyGameResponse {
	
	private List<MyGameItemResponse> games;

	public MyGameResponse(List<MyGameItemResponse> games) {
		this.games = games;
	}

	public List<MyGameItemResponse> getGames() {
		return games;
	}
}
