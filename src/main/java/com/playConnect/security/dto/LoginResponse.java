package com.playConnect.security.dto;

public class LoginResponse {

    private String status;
    private Long userId;
    private String token;
    
    
	public LoginResponse(String status, String token, Long userId) {
		super();
		this.status = status;
		this.token = token;
		this.userId = userId;
	}
	
	public LoginResponse() {
		super();
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
    
    
}