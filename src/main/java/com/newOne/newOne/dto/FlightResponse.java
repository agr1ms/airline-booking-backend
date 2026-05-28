package com.newOne.newOne.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FlightResponse(
		Long id,
		String flightNumber,
		String origin,
		String destination,
		LocalDateTime departureTime,
		LocalDateTime arrivalTime,
		int totalSeats,
		int availableSeats,
		BigDecimal price) {
}
