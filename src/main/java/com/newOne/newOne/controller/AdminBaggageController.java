package com.newOne.newOne.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newOne.newOne.dto.BaggageResponse;
import com.newOne.newOne.service.BaggageService;

@RestController
@RequestMapping("/api/admin/baggage")
public class AdminBaggageController {

	private final BaggageService baggageService;

	public AdminBaggageController(BaggageService baggageService) {
		this.baggageService = baggageService;
	}

	@GetMapping("/flight/{flightId}")
	public List<BaggageResponse> listByFlight(@PathVariable Long flightId) {
		return baggageService.listByFlightForAdmin(flightId);
	}
}
