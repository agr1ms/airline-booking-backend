package com.newOne.newOne.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.newOne.newOne.dto.BaggageRequest;
import com.newOne.newOne.dto.BaggageResponse;
import com.newOne.newOne.service.BaggageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/baggage")
public class BaggageController {

	private final BaggageService baggageService;

	public BaggageController(BaggageService baggageService) {
		this.baggageService = baggageService;
	}

	@GetMapping
	public List<BaggageResponse> myBaggage() {
		return baggageService.myBaggage();
	}

	@GetMapping("/booking/{bookingId}")
	public List<BaggageResponse> listForBooking(@PathVariable Long bookingId) {
		return baggageService.listForBooking(bookingId);
	}

	@PostMapping("/booking/{bookingId}")
	@ResponseStatus(HttpStatus.CREATED)
	public BaggageResponse add(@PathVariable Long bookingId, @Valid @RequestBody BaggageRequest request) {
		return baggageService.addBaggage(bookingId, request);
	}

	@DeleteMapping("/{baggageId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remove(@PathVariable Long baggageId) {
		baggageService.removeBaggage(baggageId);
	}
}
