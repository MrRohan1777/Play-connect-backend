package com.playConnect.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playConnect.Response.ApiResponse;
import com.playConnect.security.dto.LoginRequest;
import com.playConnect.security.dto.LoginResponse;
import com.playConnect.security.securityConfig.UserSecurityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

	@Autowired
	private UserSecurityService userService;
	
	@PostMapping("/login")
	public  ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
		LoginResponse loginResponse =  userService.login(request);
	    
	    ApiResponse<LoginResponse> response = new ApiResponse<>();
		response.setMessage("Login Successful...!");
		response.setData(loginResponse);
		response.setStatus("Success".toUpperCase());
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
