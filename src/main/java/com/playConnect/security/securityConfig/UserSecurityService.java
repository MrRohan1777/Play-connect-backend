package com.playConnect.security.securityConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.playConnect.exception.InvalidUsernamePasswordException;
import com.playConnect.security.dto.LoginRequest;
import com.playConnect.security.dto.LoginResponse;
import com.playConnect.user.entity.User;
import com.playConnect.user.repository.UserRepository;

@Service
public class UserSecurityService {

	    @Autowired
	    private UserRepository userRepository;
	    @Autowired
	    private PasswordEncoder passwordEncoder;
	    @Autowired
	    private JwtUtil jwtUtil;

	    public LoginResponse login(LoginRequest request) {

	        User user = userRepository.findByEmail(request.getEmail())
	                .orElseThrow(() -> new InvalidUsernamePasswordException("Invalid email or password"));

	        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
	            throw new InvalidUsernamePasswordException("Invalid email or password");
	        }

	        System.out.println("Id -- "+user.getId());
	        String token = jwtUtil.generateToken(user.getEmail(),user.getId());

	        return new LoginResponse("SUCCESS", token, user.getId());
	    }
}
