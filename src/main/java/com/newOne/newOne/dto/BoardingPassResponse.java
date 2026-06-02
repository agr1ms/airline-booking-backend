package com.newOne.newOne.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BoardingPassResponse(
		Long boardingPassId,
		String boardingPassNumber,
		Long bookingId,
		String ticketNumber,
		String passengerUsername,
		String flightNumber,
		String origin,
		String destination,
		LocalDateTime departureTime,
		List<String> assignedSeats,
		String gate,
		LocalDateTime boardingTime,
		LocalDateTime checkedInAt) {
}
