package com.newOne.newOne.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AirportRequest(
		@NotBlank @Size(min = 3, max = 3, message = "Airport code must be exactly 3 characters") String code,
		@NotBlank @Size(max = 100) String name,
		@NotBlank @Size(max = 100) String city,
		@NotBlank @Size(max = 100) String country) {
}
