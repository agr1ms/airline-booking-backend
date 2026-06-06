package com.newOne.newOne.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.newOne.newOne.service.FileService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/files")
public class FileController {

	private final FileService fileService;

	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping("/upload")
	public ResponseEntity<Map<String, Object>> uploadMultipart(@RequestParam("file") MultipartFile file) {
		Map<String, Object> response = new HashMap<>();
		try {
			String uploadedFilename = fileService.uploadMultipart(file);
			response.put("status", "success");
			response.put("message", "File uploaded successfully via multipart stream");
			response.put("filename", uploadedFilename);
			response.put("size", file.getSize());
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			response.put("status", "error");
			response.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(response);
		} catch (IOException e) {
			response.put("status", "error");
			response.put("message", "Upload failed: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	/**
	 * Raw Binary Stream Upload.
	 * Completely bypasses Spring's multipart resolver. Reads the request stream
	 * directly.
	 * Recommended for very large files (e.g., 100MB+).
	 * Header "X-File-Name" specifies the target filename.
	 */
	@PostMapping(value = "/upload-raw", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Map<String, Object>> uploadRawStream(
			HttpServletRequest request,
			@RequestHeader(value = "X-File-Name", required = false) String filename) {

		Map<String, Object> response = new HashMap<>();
		try {
			InputStream requestStream = request.getInputStream();
			String uploadedFilename = fileService.uploadRawStream(requestStream, filename);

			response.put("status", "success");
			response.put("message", "File uploaded successfully via raw binary stream");
			response.put("filename", uploadedFilename);
			return ResponseEntity.ok(response);
		} catch (SecurityException e) {
			response.put("status", "error");
			response.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
		} catch (IOException e) {
			response.put("status", "error");
			response.put("message", "Upload failed: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	/**
	 * Streaming File Download.
	 * Uses StreamingResponseBody to stream the file to the client in chunks (8KB by
	 * default).
	 * Ensures minimal memory utilization on the server.
	 */
	@GetMapping("/download/{filename}")
	public ResponseEntity<StreamingResponseBody> downloadFile(@PathVariable String filename) {
		try {
			File file = fileService.getFileForDownload(filename);

			// Detect content type
			String contentType = Files.probeContentType(file.toPath());
			if (contentType == null) {
				contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
			}

			long fileLength = file.length();

			StreamingResponseBody responseBody = outputStream -> {
				try (FileInputStream fileInputStream = new FileInputStream(file)) {
					byte[] buffer = new byte[8192];
					int bytesRead;
					while ((bytesRead = fileInputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, bytesRead);
					}
					outputStream.flush();
				}
			};

			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(contentType))
					.contentLength(fileLength)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
					.body(responseBody);

		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		} catch (SecurityException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
