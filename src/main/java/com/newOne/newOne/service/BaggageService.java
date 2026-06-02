package com.newOne.newOne.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newOne.newOne.dto.BaggageRequest;
import com.newOne.newOne.dto.BaggageResponse;
import com.newOne.newOne.entity.Baggage;
import com.newOne.newOne.entity.BaggageType;
import com.newOne.newOne.entity.Booking;
import com.newOne.newOne.entity.BookingStatus;
import com.newOne.newOne.exception.ApiException;
import com.newOne.newOne.repository.BaggageRepository;
import com.newOne.newOne.repository.BoardingPassRepository;
import com.newOne.newOne.repository.BookingRepository;

@Service
public class BaggageService {

	private final BaggageRepository baggageRepository;
	private final BookingRepository bookingRepository;
	private final BoardingPassRepository boardingPassRepository;
	private final int cabinMaxKg;
	private final int checkedMaxKg;
	private final int maxCheckedBags;
	private final BigDecimal checkedFeePerKg;

	public BaggageService(
			BaggageRepository baggageRepository,
			BookingRepository bookingRepository,
			BoardingPassRepository boardingPassRepository,
			@Value("${app.baggage.cabin-max-kg:7}") int cabinMaxKg,
			@Value("${app.baggage.checked-max-kg:23}") int checkedMaxKg,
			@Value("${app.baggage.max-checked-bags:3}") int maxCheckedBags,
			@Value("${app.baggage.checked-fee-per-kg:50}") BigDecimal checkedFeePerKg) {
		this.baggageRepository = baggageRepository;
		this.bookingRepository = bookingRepository;
		this.boardingPassRepository = boardingPassRepository;
		this.cabinMaxKg = cabinMaxKg;
		this.checkedMaxKg = checkedMaxKg;
		this.maxCheckedBags = maxCheckedBags;
		this.checkedFeePerKg = checkedFeePerKg;
	}

	public List<BaggageResponse> myBaggage() {
		return baggageRepository.findByBookingPassengerUsernameOrderByAddedAtDesc(currentUsername())
				.stream()
				.map(this::toResponse)
				.toList();
	}

	public List<BaggageResponse> listForBooking(Long bookingId) {
		Booking booking = bookingRepository.findByIdAndPassengerUsername(bookingId, currentUsername())
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking not found"));
		return baggageRepository.findByBookingIdOrderByAddedAtAsc(booking.getId()).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public BaggageResponse addBaggage(Long bookingId, BaggageRequest request) {
		Booking booking = bookingRepository.findByIdAndPassengerUsername(bookingId, currentUsername())
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking not found"));
		validateBookingEligible(booking);

		if (request.type() == BaggageType.CABIN) {
			validateCabinBaggage(booking.getId(), request.weightKg());
		}
		else {
			validateCheckedBaggage(booking.getId(), request.weightKg());
		}

		Baggage baggage = new Baggage();
		baggage.setBooking(booking);
		baggage.setType(request.type());
		baggage.setWeightKg(request.weightKg());
		baggage.setFee(calculateFee(request.type(), request.weightKg()));
		baggage.setTagNumber(generateTagNumber());

		return toResponse(baggageRepository.save(baggage));
	}

	@Transactional
	public void removeBaggage(Long baggageId) {
		Baggage baggage = baggageRepository.findByIdAndBookingPassengerUsername(baggageId, currentUsername())
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Baggage not found"));
		validateBookingEligible(baggage.getBooking());
		baggageRepository.delete(baggage);
	}

	public List<BaggageResponse> listByFlightForAdmin(Long flightId) {
		return baggageRepository.findByBookingFlightIdOrderByAddedAtAsc(flightId).stream()
				.map(this::toResponse)
				.toList();
	}

	private void validateBookingEligible(Booking booking) {
		if (booking.getStatus() != BookingStatus.CONFIRMED) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Baggage can only be managed for confirmed bookings");
		}
		if (boardingPassRepository.existsByBookingId(booking.getId())) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot change baggage after check-in");
		}
	}

	private void validateCabinBaggage(Long bookingId, int weightKg) {
		if (baggageRepository.countByBookingIdAndType(bookingId, BaggageType.CABIN) >= 1) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Only one cabin bag allowed per booking");
		}
		if (weightKg > cabinMaxKg) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Cabin bag cannot exceed " + cabinMaxKg + " kg");
		}
	}

	private void validateCheckedBaggage(Long bookingId, int weightKg) {
		if (baggageRepository.countByBookingIdAndType(bookingId, BaggageType.CHECKED) >= maxCheckedBags) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Maximum " + maxCheckedBags + " checked bags allowed");
		}
		if (weightKg > checkedMaxKg) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Each checked bag cannot exceed " + checkedMaxKg + " kg");
		}
	}

	private BigDecimal calculateFee(BaggageType type, int weightKg) {
		if (type == BaggageType.CABIN) {
			return BigDecimal.ZERO;
		}
		return checkedFeePerKg.multiply(BigDecimal.valueOf(weightKg));
	}

	private BaggageResponse toResponse(Baggage baggage) {
		Booking booking = baggage.getBooking();
		return new BaggageResponse(
				baggage.getId(),
				booking.getId(),
				booking.getTicketNumber(),
				booking.getFlight().getFlightNumber(),
				baggage.getType(),
				baggage.getWeightKg(),
				baggage.getFee(),
				baggage.getTagNumber(),
				baggage.getAddedAt());
	}

	private String currentUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private String generateTagNumber() {
		return "BAG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}
}
