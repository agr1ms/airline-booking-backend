package com.newOne.newOne.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/")
	public Map<String, String> home() {
		return Map.of(
				"app", "Airline Booking API",
				"register", "POST /api/auth/register",
				"login", "POST /api/auth/login",
				"flights", "GET /api/flights (Bearer token required)",
				"bookings", "POST /api/bookings and GET /api/bookings (Bearer token required)",
				"admin", "GET/POST/PUT/DELETE /api/admin/flights (ADMIN role only)",
				"adminLogin", "username: admin, password: admin123");
	}
}
