package com.playConnect.security.securityConfig;

import java.io.Serializable;

/**
 * Minimal principal stored in {@link org.springframework.security.core.context.SecurityContext}
 * after JWT validation for profile routes.
 */
public class JwtUserPrincipal implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Long userId;
	private final String email;

	public JwtUserPrincipal(Long userId, String email) {
		this.userId = userId;
		this.email = email;
	}

	public Long getUserId() {
		return userId;
	}

	public String getEmail() {
		return email;
	}
}
