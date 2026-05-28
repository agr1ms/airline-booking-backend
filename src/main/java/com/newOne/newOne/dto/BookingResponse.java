package com.newOne.newOne.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.newOne.newOne.entity.BookingStatus;

public record BookingResponse(
		Long bookingId,
		String ticketNumber,
		BookingStatus status,
		Integer seatsBooked,
		BigDecimal totalPrice,
		LocalDateTime bookedAt,
		String passengerUsername,
		FlightResponse flight) {
}
