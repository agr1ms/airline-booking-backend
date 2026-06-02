package com.newOne.newOne.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.newOne.newOne.dto.BoardingPassResponse;
import com.newOne.newOne.service.CheckInService;

@RestController
@RequestMapping("/api/check-in")
public class CheckInController {

	private final CheckInService checkInService;

	public CheckInController(CheckInService checkInService) {
		this.checkInService = checkInService;
	}

	@GetMapping
	public List<BoardingPassResponse> myBoardingPasses() {
		return checkInService.myBoardingPasses();
	}

	@PostMapping("/{bookingId}")
	@ResponseStatus(HttpStatus.CREATED)
	public BoardingPassResponse checkIn(@PathVariable Long bookingId) {
		return checkInService.checkIn(bookingId);
	}

	@GetMapping("/{bookingId}/boarding-pass")
	public BoardingPassResponse boardingPass(@PathVariable Long bookingId) {
		return checkInService.getBoardingPass(bookingId);
	}
}
