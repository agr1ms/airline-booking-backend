package com.newOne.newOne.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BookingRequest(
		@NotNull Long flightId,
		@NotNull @Min(1) Integer seats) {
}
