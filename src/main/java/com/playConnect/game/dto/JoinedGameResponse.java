package com.playConnect.game.dto;

import com.playConnect.game.entity.Game;

public class JoinedGameResponse {

    private Game game;
    private Long participationId;
    private String participationStatus;
    private String reason;
    private Integer playersCount;

    public JoinedGameResponse(Game game,
                              Long participationId,
                              String participationStatus,
                              String reason,
                              Integer playersCount) {
        this.game = game;
        this.participationId = participationId;
        this.participationStatus = participationStatus;
        this.reason = reason;
        this.playersCount = playersCount;
    }

    public Game getGame() {
        return game;
    }

    public Long getParticipationId() {
        return participationId;
    }

    public String getParticipationStatus() {
        return participationStatus;
    }

    public String getReason() {
        return reason;
    }

    public Integer getPlayersCount() {
        return playersCount;
    }
}
