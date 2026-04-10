package com.playConnect.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playConnect.Response.ApiResponse;
import com.playConnect.user.dto.RegisterRequest;
import com.playConnect.user.dto.UpdateLocationRequest;
import com.playConnect.user.service.UserService;
import com.playConnect.utilities.AppConstants;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<Long>> register(@Valid @RequestBody RegisterRequest request) {
		Long id =  userService.register(request);
		
		ApiResponse<Long> response = new ApiResponse<>();
		response.setMessage("User Successfully Register...!");
		response.setData(id);
		response.setStatus(AppConstants.SUCCESS);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PutMapping("/updateLocation")
	public ResponseEntity<ApiResponse<Object>> updateLocation(
	        @RequestHeader("Authorization") String token,
	        @Valid @RequestBody UpdateLocationRequest request) {
		System.out.println("token == "+token);
	    userService.updateLocation(token, request);

	    ApiResponse<Object> response = new ApiResponse<>();
	    response.setStatus(AppConstants.SUCCESS);
	    response.setMessage("Location updated successfully");
	    response.setData(null);

	    return ResponseEntity.ok(response);
	}
}
