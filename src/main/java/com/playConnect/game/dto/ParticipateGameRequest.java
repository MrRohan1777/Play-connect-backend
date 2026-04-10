package com.playConnect.game.dto;

public class ParticipateGameRequest {

	private Integer playersCount;
    private String leaderEmail;
    private String leaderPhone;
	public Integer getPlayersCount() {
		return playersCount;
	}
	public void setPlayersCount(Integer playersCount) {
		this.playersCount = playersCount;
	}
	public String getLeaderEmail() {
		return leaderEmail;
	}
	public void setLeaderEmail(String leaderEmail) {
		this.leaderEmail = leaderEmail;
	}
	public String getLeaderPhone() {
		return leaderPhone;
	}
	public void setLeaderPhone(String leaderPhone) {
		this.leaderPhone = leaderPhone;
	}
    
    
}
