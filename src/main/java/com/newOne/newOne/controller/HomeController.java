package com.newOne.newOne.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

	@GetMapping("/")
	public Map<String, String> home() {
		Map<String, String> info = new LinkedHashMap<>();
		info.put("app", "Airline Booking API");
		info.put("register", "POST /api/auth/register");
		info.put("login", "POST /api/auth/login");
		info.put("flights", "GET /api/flights (Bearer token required)");
		info.put("bookings", "POST /api/bookings and GET /api/bookings (Bearer token required)");
		info.put("baggage", "POST/GET/DELETE /api/baggage (Bearer token required)");
		info.put("checkIn", "POST /api/check-in/{bookingId} and GET /api/check-in/{bookingId}/boarding-pass");
		info.put("adminFlights", "GET/POST/PUT/DELETE /api/admin/flights (ADMIN only)");
		info.put("adminCheckIns", "GET /api/admin/check-ins (ADMIN only)");
		info.put("adminBaggage", "GET /api/admin/baggage/flight/{flightId} (ADMIN only)");
		info.put("adminLogin", "username: admin, password: admin123");
		return info;
	}
}
