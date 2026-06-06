package com.newOne.newOne.dto;

public record AadharResponse(
		String aadharNumber,
		String aadharFileName,
		boolean verified
) {}
