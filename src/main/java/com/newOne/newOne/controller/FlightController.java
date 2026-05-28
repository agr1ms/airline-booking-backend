package com.newOne.newOne.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newOne.newOne.dto.FlightResponse;
import com.newOne.newOne.service.FlightService;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

	private final FlightService flightService;

	public FlightController(FlightService flightService) {
		this.flightService = flightService;
	}

	@GetMapping
	public List<FlightResponse> list(
			@RequestParam(required = false) String origin,
			@RequestParam(required = false) String destination) {
		return flightService.listFlights(origin, destination);
	}

	@GetMapping("/{id}")
	public FlightResponse get(@PathVariable Long id) {
		return flightService.getById(id);
	}
}
