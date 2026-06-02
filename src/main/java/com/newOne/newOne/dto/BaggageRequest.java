package com.newOne.newOne.dto;

import com.newOne.newOne.entity.BaggageType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BaggageRequest(
		@NotNull BaggageType type,
		@NotNull @Min(1) Integer weightKg) {
}
