package com.newOne.newOne.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.newOne.newOne.dto.AadharAdminResponse;
import com.newOne.newOne.dto.AadharResponse;
import com.newOne.newOne.entity.User;
import com.newOne.newOne.exception.ApiException;
import com.newOne.newOne.repository.UserRepository;

@Service
public class AadharService {

	private final UserRepository userRepository;
	private final String uploadsDir = "uploads/aadhar/";

	public AadharService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public AadharResponse uploadAadhar(MultipartFile file, String aadharNumber) {
		User user = getCurrentUser();

		if (aadharNumber == null || !aadharNumber.matches("^[0-9]{12}$")) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid Aadhar number. It must be exactly 12 digits.");
		}

		if (file == null || file.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Aadhar card file is required.");
		}

		String contentType = file.getContentType();
		if (contentType == null || (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Only image (JPEG, PNG) or PDF files are allowed.");
		}

		try {
			File directory = new File(uploadsDir);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			String originalFilename = file.getOriginalFilename();
			String extension = "";
			if (originalFilename != null && originalFilename.contains(".")) {
				extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			}

			String filename = user.getUsername() + "_aadhar" + extension;
			Path filepath = Paths.get(uploadsDir, filename);
			Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

			user.setAadharNumber(aadharNumber);
			user.setAadharFileName(filename);
			user.setAadharVerified(false); // initially unverified until admin approves
			userRepository.save(user);

			return new AadharResponse(user.getAadharNumber(), user.getAadharFileName(), user.isAadharVerified());
		} catch (IOException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store Aadhar card file: " + e.getMessage());
		}
	}

	public AadharResponse getAadharDetails() {
		User user = getCurrentUser();
		return new AadharResponse(user.getAadharNumber(), user.getAadharFileName(), user.isAadharVerified());
	}

	public Resource getAadharFile() {
		User user = getCurrentUser();
		if (user.getAadharFileName() == null) {
			throw new ApiException(HttpStatus.NOT_FOUND, "No Aadhar card uploaded yet.");
		}
		return getFileResource(user.getAadharFileName());
	}

	// Admin functionality
	public List<AadharAdminResponse> getAllAadharsForAdmin() {
		return userRepository.findAll().stream()
				.filter(user -> user.getAadharNumber() != null)
				.map(user -> new AadharAdminResponse(
						user.getId(),
						user.getUsername(),
						user.getAadharNumber(),
						user.getAadharFileName(),
						user.isAadharVerified()))
				.toList();
	}

	public AadharResponse verifyUserAadhar(Long userId, boolean verify) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
		if (user.getAadharNumber() == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "User has not uploaded an Aadhar card.");
		}
		user.setAadharVerified(verify);
		userRepository.save(user);
		return new AadharResponse(user.getAadharNumber(), user.getAadharFileName(), user.isAadharVerified());
	}

	public Resource getAadharFileForAdmin(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
		if (user.getAadharFileName() == null) {
			throw new ApiException(HttpStatus.NOT_FOUND, "User has not uploaded an Aadhar card yet.");
		}
		return getFileResource(user.getAadharFileName());
	}

	private Resource getFileResource(String filename) {
		try {
			Path filepath = Paths.get(uploadsDir, filename);
			Resource resource = new UrlResource(filepath.toUri());
			if (!resource.exists() || !resource.isReadable()) {
				throw new ApiException(HttpStatus.NOT_FOUND, "Aadhar card file not found on disk.");
			}
			return resource;
		} catch (MalformedURLException e) {
			throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading Aadhar file path: " + e.getMessage());
		}
	}

	private User getCurrentUser() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
	}
}
