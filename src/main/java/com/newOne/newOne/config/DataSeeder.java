package com.newOne.newOne.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.newOne.newOne.entity.Flight;
import com.newOne.newOne.entity.User;
import com.newOne.newOne.entity.UserRole;
import com.newOne.newOne.repository.FlightRepository;
import com.newOne.newOne.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

	private final FlightRepository flightRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public DataSeeder(
			FlightRepository flightRepository,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		this.flightRepository = flightRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		seedAdminUser();
		if (flightRepository.count() > 0) {
			return;
		}
		seed("AI101", "DELHI", "MUMBAI", 6, 2, 180, "5600.00");
		seed("AI202", "MUMBAI", "BENGALURU", 8, 2, 160, "4900.00");
		seed("AI303", "DELHI", "BENGALURU", 10, 2, 190, "6100.00");
		seed("AI404", "KOLKATA", "DELHI", 12, 2, 170, "5300.00");
	}

	private void seedAdminUser() {
		if (!userRepository.existsByUsername("admin")) {
			userRepository.save(new User("admin", passwordEncoder.encode("admin123"), UserRole.ADMIN));
		}
	}

	private void seed(
			String number,
			String origin,
			String destination,
			int depHoursFromNow,
			int durationHours,
			int seats,
			String price) {
		Flight flight = new Flight();
		flight.setFlightNumber(number);
		flight.setOrigin(origin);
		flight.setDestination(destination);
		flight.setDepartureTime(LocalDateTime.now().plusHours(depHoursFromNow));
		flight.setArrivalTime(LocalDateTime.now().plusHours(depHoursFromNow + durationHours));
		flight.setTotalSeats(seats);
		flight.setAvailableSeats(seats);
		flight.setPrice(new BigDecimal(price));
		flightRepository.save(flight);
	}
}
