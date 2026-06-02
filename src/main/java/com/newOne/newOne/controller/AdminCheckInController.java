package com.newOne.newOne.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newOne.newOne.dto.BoardingPassResponse;
import com.newOne.newOne.service.CheckInService;

@RestController
@RequestMapping("/api/admin/check-ins")
public class AdminCheckInController {

	private final CheckInService checkInService;

	public AdminCheckInController(CheckInService checkInService) {
		this.checkInService = checkInService;
	}

	@GetMapping
	public List<BoardingPassResponse> listAll() {
		return checkInService.listAllForAdmin();
	}

	@GetMapping("/flight/{flightId}")
	public List<BoardingPassResponse> listByFlight(@PathVariable Long flightId) {
		return checkInService.listByFlightForAdmin(flightId);
	}
}
