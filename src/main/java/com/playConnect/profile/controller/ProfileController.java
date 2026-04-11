package com.playConnect.profile.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playConnect.exception.UnauthorizedException;
import com.playConnect.profile.dto.ProfileResponse;
import com.playConnect.profile.dto.UpdateProfileRequest;
import com.playConnect.profile.service.ProfileService;
import com.playConnect.security.securityConfig.JwtUserPrincipal;

@RestController
@RequestMapping("/profile")
public class ProfileController {

	@Autowired
	private ProfileService profileService;

	@GetMapping
	public ProfileResponse getProfile(Authentication authentication) {
		Long userId = resolveUserId(authentication);
		return profileService.getProfile(userId);
	}

	@PutMapping
	public ProfileResponse updateProfile(
			Authentication authentication,
			@RequestBody(required = false) UpdateProfileRequest request) {
		Long userId = resolveUserId(authentication);
		return profileService.updateProfile(userId, request);
	}

	private static Long resolveUserId(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new UnauthorizedException("Not authenticated");
		}
		Object principal = authentication.getPrincipal();
		if (principal instanceof JwtUserPrincipal jwt) {
			return jwt.getUserId();
		}
		throw new UnauthorizedException("Invalid authentication");
	}
}
