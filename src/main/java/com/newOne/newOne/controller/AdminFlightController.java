package com.newOne.newOne.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.newOne.newOne.dto.FlightRequest;
import com.newOne.newOne.dto.FlightResponse;
import com.newOne.newOne.service.FlightService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/flights")
public class AdminFlightController {

	private final FlightService flightService;

	public AdminFlightController(FlightService flightService) {
		this.flightService = flightService;
	}

	@GetMapping
	public List<FlightResponse> listAll() {
		return flightService.listAllForAdmin();
	}

	@GetMapping("/{id}")
	public FlightResponse get(@PathVariable Long id) {
		return flightService.getById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FlightResponse create(@Valid @RequestBody FlightRequest request) {
		return flightService.create(request);
	}

	@PutMapping("/{id}")
	public FlightResponse update(@PathVariable Long id, @Valid @RequestBody FlightRequest request) {
		return flightService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		flightService.delete(id);
	}
}
