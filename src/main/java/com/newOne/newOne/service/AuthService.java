package com.newOne.newOne.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.newOne.newOne.dto.AuthRequest;
import com.newOne.newOne.dto.AuthResponse;
import com.newOne.newOne.entity.User;
import com.newOne.newOne.entity.UserRole;
import com.newOne.newOne.exception.ApiException;
import com.newOne.newOne.repository.UserRepository;
import com.newOne.newOne.security.JwtService;

import org.springframework.http.HttpStatus;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	public AuthResponse register(AuthRequest request) {
		if (userRepository.existsByUsername(request.username())) {
			throw new ApiException(HttpStatus.CONFLICT, "Username already taken");
		}
		User user = new User(request.username(), passwordEncoder.encode(request.password()), UserRole.USER);
		userRepository.save(user);
		String token = jwtService.generateToken(user.getUsername());
		return new AuthResponse(token, user.getUsername(), user.getRole().name());
	}

	public AuthResponse login(AuthRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.username(), request.password()));
		User user = userRepository.findByUsername(request.username())
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
		String token = jwtService.generateToken(request.username());
		return new AuthResponse(token, request.username(), user.getRole().name());
	}
}
