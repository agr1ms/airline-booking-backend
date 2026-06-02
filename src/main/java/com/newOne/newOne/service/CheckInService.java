package com.newOne.newOne.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newOne.newOne.dto.BoardingPassResponse;
import com.newOne.newOne.entity.BoardingPass;
import com.newOne.newOne.entity.Booking;
import com.newOne.newOne.entity.BookingStatus;
import com.newOne.newOne.entity.Flight;
import com.newOne.newOne.exception.ApiException;
import com.newOne.newOne.repository.BoardingPassRepository;
import com.newOne.newOne.repository.BookingRepository;

@Service
public class CheckInService {

	private static final String[] SEAT_COLUMNS = { "A", "B", "C", "D", "E", "F" };

	private final BoardingPassRepository boardingPassRepository;
	private final BookingRepository bookingRepository;
	private final int opensHoursBefore;
	private final int closesHoursBefore;

	public CheckInService(
			BoardingPassRepository boardingPassRepository,
			BookingRepository bookingRepository,
			@Value("${app.checkin.opens-hours-before:24}") int opensHoursBefore,
			@Value("${app.checkin.closes-hours-before:1}") int closesHoursBefore) {
		this.boardingPassRepository = boardingPassRepository;
		this.bookingRepository = bookingRepository;
		this.opensHoursBefore = opensHoursBefore;
		this.closesHoursBefore = closesHoursBefore;
	}

	public List<BoardingPassResponse> myBoardingPasses() {
		return boardingPassRepository.findByBookingPassengerUsernameOrderByCheckedInAtDesc(currentUsername())
				.stream()
				.map(this::toResponse)
				.toList();
	}

	public BoardingPassResponse getBoardingPass(Long bookingId) {
		BoardingPass pass = boardingPassRepository
				.findByBookingIdAndBookingPassengerUsername(bookingId, currentUsername())
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Boarding pass not found"));
		return toResponse(pass);
	}

	@Transactional
	public BoardingPassResponse checkIn(Long bookingId) {
		Booking booking = bookingRepository.findByIdAndPassengerUsername(bookingId, currentUsername())
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking not found"));

		if (booking.getStatus() != BookingStatus.CONFIRMED) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Only confirmed bookings can be checked in");
		}
		if (boardingPassRepository.existsByBookingId(bookingId)) {
			throw new ApiException(HttpStatus.CONFLICT, "Already checked in for this booking");
		}

		Flight flight = booking.getFlight();
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime checkInOpens = flight.getDepartureTime().minusHours(opensHoursBefore);
		LocalDateTime checkInCloses = flight.getDepartureTime().minusHours(closesHoursBefore);

		if (now.isBefore(checkInOpens)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Check-in opens " + opensHoursBefore + " hours before departure");
		}
		if (now.isAfter(checkInCloses)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Check-in closed " + closesHoursBefore + " hour(s) before departure");
		}

		List<String> assignedSeats = assignSeats(flight, booking.getSeatsBooked());

		BoardingPass pass = new BoardingPass();
		pass.setBooking(booking);
		pass.setBoardingPassNumber(generateBoardingPassNumber());
		pass.setAssignedSeats(String.join(",", assignedSeats));
		pass.setGate(resolveGate(flight));
		pass.setBoardingTime(flight.getDepartureTime().minusMinutes(30));

		return toResponse(boardingPassRepository.save(pass));
	}

	public List<BoardingPassResponse> listAllForAdmin() {
		return boardingPassRepository.findAllByOrderByCheckedInAtDesc().stream().map(this::toResponse).toList();
	}

	public List<BoardingPassResponse> listByFlightForAdmin(Long flightId) {
		return boardingPassRepository.findByBookingFlightIdOrderByCheckedInAtAsc(flightId).stream()
				.map(this::toResponse)
				.toList();
	}

	private List<String> assignSeats(Flight flight, int seatsNeeded) {
		Set<String> takenSeats = boardingPassRepository.findByBookingFlightIdOrderByCheckedInAtAsc(flight.getId())
				.stream()
				.flatMap(pass -> Arrays.stream(pass.getAssignedSeats().split(",")))
				.map(String::trim)
				.collect(Collectors.toSet());

		List<String> assigned = new ArrayList<>();
		int row = 1;
		while (assigned.size() < seatsNeeded) {
			for (String column : SEAT_COLUMNS) {
				if (assigned.size() >= seatsNeeded) {
					break;
				}
				String seat = row + column;
				if (!takenSeats.contains(seat)) {
					assigned.add(seat);
					takenSeats.add(seat);
				}
			}
			row++;
			if ((row - 1) * SEAT_COLUMNS.length > flight.getTotalSeats()) {
				throw new ApiException(HttpStatus.CONFLICT, "No seats available for check-in");
			}
		}
		return assigned;
	}

	private String resolveGate(Flight flight) {
		int gateNumber = (flight.getId().intValue() % 20) + 1;
		return "Gate " + gateNumber;
	}

	private BoardingPassResponse toResponse(BoardingPass pass) {
		Booking booking = pass.getBooking();
		Flight flight = booking.getFlight();
		List<String> seats = Arrays.stream(pass.getAssignedSeats().split(",")).map(String::trim).toList();
		return new BoardingPassResponse(
				pass.getId(),
				pass.getBoardingPassNumber(),
				booking.getId(),
				booking.getTicketNumber(),
				booking.getPassenger().getUsername(),
				flight.getFlightNumber(),
				flight.getOrigin(),
				flight.getDestination(),
				flight.getDepartureTime(),
				seats,
				pass.getGate(),
				pass.getBoardingTime(),
				pass.getCheckedInAt());
	}

	private String currentUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private String generateBoardingPassNumber() {
		return "BP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}
}
