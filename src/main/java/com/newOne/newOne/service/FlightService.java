package com.newOne.newOne.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newOne.newOne.dto.FlightRequest;
import com.newOne.newOne.dto.FlightResponse;
import com.newOne.newOne.entity.Flight;
import com.newOne.newOne.exception.ApiException;
import com.newOne.newOne.repository.BookingRepository;
import com.newOne.newOne.repository.FlightRepository;

@Service
public class FlightService {

	private final FlightRepository flightRepository;
	private final BookingRepository bookingRepository;

	public FlightService(FlightRepository flightRepository, BookingRepository bookingRepository) {
		this.flightRepository = flightRepository;
		this.bookingRepository = bookingRepository;
	}

	public List<FlightResponse> listAllForAdmin() {
		return flightRepository.findAllByOrderByDepartureTimeAsc().stream().map(this::toResponse).toList();
	}

	@Transactional
	public FlightResponse create(FlightRequest request) {
		validateTimes(request);
		if (flightRepository.existsByFlightNumber(request.flightNumber())) {
			throw new ApiException(HttpStatus.CONFLICT, "Flight number already exists");
		}
		Flight flight = mapRequest(new Flight(), request);
		flight.setAvailableSeats(request.totalSeats());
		return toResponse(flightRepository.save(flight));
	}

	@Transactional
	public FlightResponse update(Long id, FlightRequest request) {
		validateTimes(request);
		Flight flight = flightRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Flight not found"));
		if (flightRepository.existsByFlightNumberAndIdNot(request.flightNumber(), id)) {
			throw new ApiException(HttpStatus.CONFLICT, "Flight number already exists");
		}

		int bookedSeats = flight.getTotalSeats() - flight.getAvailableSeats();
		if (request.totalSeats() < bookedSeats) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Total seats cannot be less than already booked seats");
		}

		mapRequest(flight, request);
		flight.setAvailableSeats(request.totalSeats() - bookedSeats);
		return toResponse(flightRepository.save(flight));
	}

	@Transactional
	public void delete(Long id) {
		Flight flight = flightRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Flight not found"));
		if (bookingRepository.existsByFlightId(id)) {
			throw new ApiException(HttpStatus.CONFLICT, "Cannot delete flight with existing bookings");
		}
		flightRepository.delete(flight);
	}

	public List<FlightResponse> listFlights(String origin, String destination) {
		LocalDateTime now = LocalDateTime.now();
		if (origin != null && !origin.isBlank() && destination != null && !destination.isBlank()) {
			return flightRepository
					.findByOriginIgnoreCaseAndDestinationIgnoreCaseAndDepartureTimeAfterOrderByDepartureTimeAsc(
							origin.trim(),
							destination.trim(),
							now)
					.stream()
					.map(this::toResponse)
					.toList();
		}
		return flightRepository.findByDepartureTimeAfterOrderByDepartureTimeAsc(now).stream().map(this::toResponse).toList();
	}

	public FlightResponse getById(Long id) {
		Flight flight = flightRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Flight not found"));
		return toResponse(flight);
	}

	private Flight mapRequest(Flight flight, FlightRequest request) {
		flight.setFlightNumber(request.flightNumber().trim().toUpperCase());
		flight.setOrigin(request.origin().trim().toUpperCase());
		flight.setDestination(request.destination().trim().toUpperCase());
		flight.setDepartureTime(request.departureTime());
		flight.setArrivalTime(request.arrivalTime());
		flight.setTotalSeats(request.totalSeats());
		flight.setPrice(request.price());
		return flight;
	}

	private void validateTimes(FlightRequest request) {
		if (!request.arrivalTime().isAfter(request.departureTime())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Arrival time must be after departure time");
		}
	}

	FlightResponse toResponse(Flight flight) {
		return new FlightResponse(
				flight.getId(),
				flight.getFlightNumber(),
				flight.getOrigin(),
				flight.getDestination(),
				flight.getDepartureTime(),
				flight.getArrivalTime(),
				flight.getTotalSeats(),
				flight.getAvailableSeats(),
				flight.getPrice());
	}
}
