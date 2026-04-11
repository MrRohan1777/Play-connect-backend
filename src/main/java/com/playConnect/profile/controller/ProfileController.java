package com.playConnect.profile.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playConnect.Response.ApiResponse;
import com.playConnect.profile.dto.ProfileResponse;
import com.playConnect.profile.service.ProfileService;
import com.playConnect.utilities.AppConstants;

@RestController
@RequestMapping("/profile")
public class ProfileController {

	@Autowired
	private ProfileService profileService;

	@GetMapping
	public ResponseEntity<ApiResponse<ProfileResponse>> getProfile() {
		Long userId = 1L; // MVP temp user id until auth integration
		ProfileResponse profile = profileService.getProfile(userId);
		ApiResponse<ProfileResponse> response = new ApiResponse<>();
		response.setMessage("Profile fetched successfully");
		response.setData(profile);
		response.setStatus(AppConstants.SUCCESS);
		return ResponseEntity.ok(response);
	}
}
