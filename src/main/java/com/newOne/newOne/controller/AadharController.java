package com.newOne.newOne.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.newOne.newOne.dto.AadharResponse;
import com.newOne.newOne.service.AadharService;

@RestController
@RequestMapping("/api/users/aadhar")
public class AadharController {

	private final AadharService aadharService;

	public AadharController(AadharService aadharService) {
		this.aadharService = aadharService;
	}

	@PostMapping("/upload")
	@ResponseStatus(HttpStatus.OK)
	public AadharResponse upload(
			@RequestParam("file") MultipartFile file,
			@RequestParam("aadharNumber") String aadharNumber) {
		return aadharService.uploadAadhar(file, aadharNumber);
	}

	@GetMapping("/status")
	public AadharResponse getStatus() {
		return aadharService.getAadharDetails();
	}

	@GetMapping("/file")
	public ResponseEntity<Resource> getFile() {
		Resource file = aadharService.getAadharFile();
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
