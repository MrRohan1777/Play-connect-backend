package com.playConnect.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.playConnect.exception.EmailAlreadyExistsException;
import com.playConnect.exception.RegistrationFailedException;
import com.playConnect.exception.ResourceNotFoundException;
import com.playConnect.mapper.UserMapper;
import com.playConnect.security.securityConfig.JwtUtil;
import com.playConnect.user.dto.RegisterRequest;
import com.playConnect.user.dto.UpdateLocationRequest;
import com.playConnect.user.dto.UserProfileResponse;
import com.playConnect.user.entity.User;
import com.playConnect.user.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository playerRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtUtil jwtUtil;

	public Long register(RegisterRequest request) {

		if (playerRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("Email already registered");
		}

		User user = UserMapper.INSTANCE.dtoToEntity(request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setLatitude(0.0);
		user.setLongitude(0.0);
		playerRepository.save(user);

		if (user.getId() == null) {
			throw new RegistrationFailedException("User registration failed");
		}

		return user.getId();
	}

	public UserProfileResponse getUserProfile(Long userId) {
		User user = playerRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		return UserProfileResponse.fromEntity(user);
	}

	public void updateLocation(String token, UpdateLocationRequest request) {

		String cleanToken = token.replace("Bearer ", "");
		Long userId = jwtUtil.extractUserId(cleanToken);
		System.out.println("ID === "+userId);
		User user = playerRepository.findById(userId)
		        .orElseThrow(() -> 
		            new ResourceNotFoundException("User not found"));

		user.setLatitude(request.getLatitude());
		user.setLongitude(request.getLongitude());

		playerRepository.save(user);
	}

}
