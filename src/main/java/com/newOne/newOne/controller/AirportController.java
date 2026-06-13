package com.newOne.newOne.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newOne.newOne.dto.AirportResponse;
import com.newOne.newOne.service.AirportService;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

	private final AirportService airportService;

	public AirportController(AirportService airportService) {
		this.airportService = airportService;
	}

	@GetMapping
	public List<AirportResponse> listAll() {
		return airportService.getAll();
	}

	@GetMapping("/{id}")
	public AirportResponse get(@PathVariable Long id) {
		return airportService.getById(id);
	}
}
