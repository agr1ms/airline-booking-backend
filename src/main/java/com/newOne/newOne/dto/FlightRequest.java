package com.newOne.newOne.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FlightRequest(
		@NotBlank @Size(max = 20) String flightNumber,
		@NotBlank @Size(max = 80) String origin,
		@NotBlank @Size(max = 80) String destination,
		@NotNull LocalDateTime departureTime,
		@NotNull LocalDateTime arrivalTime,
		@NotNull @Min(1) Integer totalSeats,
		@NotNull @DecimalMin("0.01") BigDecimal price) {
}
