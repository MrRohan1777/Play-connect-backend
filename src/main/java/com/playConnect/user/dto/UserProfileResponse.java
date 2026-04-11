package com.playConnect.user.dto;

import com.playConnect.user.entity.User;

/**
 * Public user card for profile / header (no password).
 */
public class UserProfileResponse {

	private Long id;
	private String name;
	private String email;
	/** Skill tier shown as "Level" on the client (BEGINNER, INTERMEDIATE, ADVANCED). */
	private String level;

	public static UserProfileResponse fromEntity(User user) {
		UserProfileResponse r = new UserProfileResponse();
		r.id = user.getId();
		r.name = user.getName();
		r.email = user.getEmail();
		r.level = user.getSkillLevel() != null ? user.getSkillLevel().name() : null;
		return r;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getLevel() {
		return level;
	}

	/** Same as {@link #getLevel()} — some clients read {@code skillLevel}. */
	public String getSkillLevel() {
		return level;
	}
}
