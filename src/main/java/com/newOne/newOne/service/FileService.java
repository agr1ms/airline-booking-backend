package com.newOne.newOne.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

@Service
public class FileService {

	@Value("${app.upload.dir}")
	private String uploadDir;

	@PostConstruct
	public void init() {
		try {
			Files.createDirectories(Paths.get(uploadDir));
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize upload folder: " + uploadDir, e);
		}
	}

	/**
	 * Upload file using standard Spring MultipartFile.
	 * Spring Tomcat parses the request and spools the file onto disk.
	 * We then read it in a streaming fashion and copy it to our destination folder.
	 */
	public String uploadMultipart(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("File cannot be empty");
		}
		
		String filename = file.getOriginalFilename();
		if (filename == null || filename.isBlank()) {
			filename = "upload_" + System.currentTimeMillis();
		}
		
		Path destination = Paths.get(uploadDir).resolve(filename).normalize();
		
		// Ensure file remains in the directory
		if (!destination.getParent().equals(Paths.get(uploadDir).normalize())) {
			throw new SecurityException("Cannot store file outside directory");
		}

		try (InputStream in = file.getInputStream()) {
			Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
		}
		return filename;
	}

	/**
	 * Upload file using raw stream directly from the HTTP request.
	 * This bypasses multipart parsing entirely, using minimal memory and disk overhead.
	 */
	public String uploadRawStream(InputStream inputStream, String filename) throws IOException {
		if (filename == null || filename.isBlank()) {
			filename = "raw_upload_" + System.currentTimeMillis();
		}
		
		Path destination = Paths.get(uploadDir).resolve(filename).normalize();
		
		// Ensure file remains in the directory
		if (!destination.getParent().equals(Paths.get(uploadDir).normalize())) {
			throw new SecurityException("Cannot store file outside directory");
		}

		// Stream the data in 8KB chunks
		try (OutputStream out = Files.newOutputStream(destination)) {
			byte[] buffer = new byte[8192];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		}
		return filename;
	}

	/**
	 * Get the file reference for downloading.
	 */
	public File getFileForDownload(String filename) {
		Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
		
		// Ensure path traversal protection
		if (!filePath.getParent().equals(Paths.get(uploadDir).normalize())) {
			throw new SecurityException("Cannot download file outside directory");
		}
		
		File file = filePath.toFile();
		if (!file.exists() || !file.isFile()) {
			throw new IllegalArgumentException("File not found: " + filename);
		}
		return file;
	}
}
