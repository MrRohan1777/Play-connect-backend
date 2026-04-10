package com.playConnect.game.dto;

import com.playConnect.game.entity.Game;

public class MyGameItemResponse {

    private Game game;
    private boolean isHost;
    private String participationStatus;
    private String reason;
    private Long userId;
    private Long participationId;

    public MyGameItemResponse(Game game,
                              boolean isHost,
                              String participationStatus,
                              String reason,
                              Long userId,
                              Long participationId) {
        this.game = game;
        this.isHost = isHost;
        this.participationStatus = participationStatus;
        this.reason = reason;
        this.userId = userId;
        this.participationId = participationId;
    }

    public Game getGame() {
        return game;
    }

    public boolean isHost() {
        return isHost;
    }

    public String getParticipationStatus() {
        return participationStatus;
    }

    public String getReason() {
        return reason;
    }

	public Long getUserId() {
		return userId;
	}

	public Long getParticipationId() {
		return participationId;
	}
}
