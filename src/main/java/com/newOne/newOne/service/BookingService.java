package com.newOne.newOne.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newOne.newOne.dto.BookingRequest;
import com.newOne.newOne.dto.BookingResponse;
import com.newOne.newOne.dto.FlightResponse;
import com.newOne.newOne.entity.Booking;
import com.newOne.newOne.entity.BookingStatus;
import com.newOne.newOne.entity.Flight;
import com.newOne.newOne.entity.User;
import com.newOne.newOne.exception.ApiException;
import com.newOne.newOne.repository.BoardingPassRepository;
import com.newOne.newOne.repository.BookingRepository;
import com.newOne.newOne.repository.FlightRepository;
import com.newOne.newOne.repository.UserRepository;

@Service
public class BookingService {

	private final BookingRepository bookingRepository;
	private final FlightRepository flightRepository;
	private final UserRepository userRepository;
	private final FlightService flightService;
	private final BoardingPassRepository boardingPassRepository;

	public BookingService(
			BookingRepository bookingRepository,
			FlightRepository flightRepository,
			UserRepository userRepository,
			FlightService flightService,
			BoardingPassRepository boardingPassRepository) {
		this.bookingRepository = bookingRepository;
		this.flightRepository = flightRepository;
		this.userRepository = userRepository;
		this.flightService = flightService;
		this.boardingPassRepository = boardingPassRepository;
	}

	public List<BookingResponse> myBookings() {
		return bookingRepository.findByPassengerUsernameOrderByCreatedAtDesc(currentUsername())
				.stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public BookingResponse createBooking(BookingRequest request) {
		User passenger = userRepository.findByUsername(currentUsername())
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));

		if (!passenger.isAadharVerified()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Passenger is not Aadhar verified. Aadhar verification is mandatory to book tickets.");
		}

		Flight flight = flightRepository.findById(request.flightId())
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Flight not found"));

		if (flight.getDepartureTime().isBefore(LocalDateTime.now())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot book already departed flights");
		}
		if (flight.getAvailableSeats() < request.seats()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Not enough available seats");
		}

		flight.setAvailableSeats(flight.getAvailableSeats() - request.seats());
		flightRepository.save(flight);

		Booking booking = new Booking();
		booking.setPassenger(passenger);
		booking.setFlight(flight);
		booking.setSeatsBooked(request.seats());
		booking.setTotalPrice(flight.getPrice().multiply(BigDecimal.valueOf(request.seats())));
		booking.setTicketNumber(generateTicketNumber());
		booking.setStatus(BookingStatus.CONFIRMED);

		return toResponse(bookingRepository.save(booking));
	}

	@Transactional
	public BookingResponse cancelBooking(Long bookingId) {
		Booking booking = bookingRepository.findByIdAndPassengerUsername(bookingId, currentUsername())
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking not found"));
		if (booking.getStatus() == BookingStatus.CANCELLED) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Booking already cancelled");
		}
		if (boardingPassRepository.existsByBookingId(bookingId)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot cancel after check-in");
		}
		if (booking.getFlight().getDepartureTime().isBefore(LocalDateTime.now())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot cancel after departure");
		}

		booking.setStatus(BookingStatus.CANCELLED);
		Flight flight = booking.getFlight();
		flight.setAvailableSeats(flight.getAvailableSeats() + booking.getSeatsBooked());
		flightRepository.save(flight);
		return toResponse(bookingRepository.save(booking));
	}

	private BookingResponse toResponse(Booking booking) {
		FlightResponse flight = flightService.toResponse(booking.getFlight());
		var boardingPass = boardingPassRepository.findByBookingId(booking.getId());
		return new BookingResponse(
				booking.getId(),
				booking.getTicketNumber(),
				booking.getStatus(),
				booking.getSeatsBooked(),
				booking.getTotalPrice(),
				booking.getCreatedAt(),
				booking.getPassenger().getUsername(),
				boardingPass.isPresent(),
				boardingPass.map(bp -> bp.getBoardingPassNumber()).orElse(null),
				flight);
	}

	private String currentUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private String generateTicketNumber() {
		return "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}
}
