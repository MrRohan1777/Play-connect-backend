package com.playConnect.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playConnect.user.dto.UserProfileResponse;
import com.playConnect.user.service.UserService;

/**
 * Paths used by the web app (e.g. {@code GET /users/{id}} for profile). Response body is the user
 * object directly so clients can use {@code response.data.name} (axios) without unwrapping.
 */
@RestController
@RequestMapping("/users")
public class UsersController {

	@Autowired
	private UserService userService;

	@GetMapping("/{id}")
	public ResponseEntity<UserProfileResponse> getUser(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getUserProfile(id));
	}
}
