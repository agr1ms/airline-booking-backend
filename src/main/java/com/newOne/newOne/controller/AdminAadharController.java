package com.newOne.newOne.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newOne.newOne.dto.AadharAdminResponse;
import com.newOne.newOne.dto.AadharResponse;
import com.newOne.newOne.service.AadharService;

@RestController
@RequestMapping("/api/admin/users")
public class AdminAadharController {

	private final AadharService aadharService;

	public AdminAadharController(AadharService aadharService) {
		this.aadharService = aadharService;
	}

	@GetMapping("/aadhar")
	public List<AadharAdminResponse> getAllAadhars() {
		return aadharService.getAllAadharsForAdmin();
	}

	@PostMapping("/{userId}/aadhar/verify")
	public AadharResponse verifyAadhar(
			@PathVariable Long userId,
			@RequestParam("verify") boolean verify) {
		return aadharService.verifyUserAadhar(userId, verify);
	}

	@GetMapping("/{userId}/aadhar/file")
	public ResponseEntity<Resource> getAadharFile(@PathVariable Long userId) {
		Resource file = aadharService.getAadharFileForAdmin(userId);
		String contentType = "application/octet-stream";
		try {
			String probed = Files.probeContentType(Paths.get(file.getURI()));
			if (probed != null) {
				contentType = probed;
			}
		} catch (IOException ignored) {
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, contentType)
				.body(file);
	}
}
