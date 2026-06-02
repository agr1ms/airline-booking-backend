package com.newOne.newOne.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.newOne.newOne.entity.BaggageType;

public record BaggageResponse(
		Long baggageId,
		Long bookingId,
		String ticketNumber,
		String flightNumber,
		BaggageType type,
		int weightKg,
		BigDecimal fee,
		String tagNumber,
		LocalDateTime addedAt) {
}
