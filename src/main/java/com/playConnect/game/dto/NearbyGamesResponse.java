package com.playConnect.game.dto;

import java.util.List;

import com.playConnect.game.entity.Game;

public class NearbyGamesResponse {

    private String message;
    private List<Game> games;

    public NearbyGamesResponse(String message, List<Game> games) {
        this.message = message;
        this.games = games;
    }

    public String getMessage() {
        return message;
    }

    public List<Game> getGames() {
        return games;
    }
}
