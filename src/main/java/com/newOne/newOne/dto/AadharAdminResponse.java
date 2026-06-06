package com.newOne.newOne.dto;

public record AadharAdminResponse(
		Long userId,
		String username,
		String aadharNumber,
		String aadharFileName,
		boolean verified
) {}
