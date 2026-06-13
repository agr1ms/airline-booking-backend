package com.newOne.newOne.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.newOne.newOne.dto.AirportRequest;
import com.newOne.newOne.dto.AirportResponse;
import com.newOne.newOne.service.AirportService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/airports")
public class AdminAirportController {

	private final AirportService airportService;

	public AdminAirportController(AirportService airportService) {
		this.airportService = airportService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public AirportResponse create(@Valid @RequestBody AirportRequest request) {
		return airportService.create(request);
	}

	@PutMapping("/{id}")
	public AirportResponse update(@PathVariable Long id, @Valid @RequestBody AirportRequest request) {
		return airportService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		airportService.delete(id);
	}
}
