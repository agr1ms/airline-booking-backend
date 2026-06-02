package com.newOne.newOne.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newOne.newOne.entity.Baggage;
import com.newOne.newOne.entity.BaggageType;

public interface BaggageRepository extends JpaRepository<Baggage, Long> {

	List<Baggage> findByBookingIdOrderByAddedAtAsc(Long bookingId);

	List<Baggage> findByBookingPassengerUsernameOrderByAddedAtDesc(String username);

	List<Baggage> findByBookingFlightIdOrderByAddedAtAsc(Long flightId);

	Optional<Baggage> findByIdAndBookingPassengerUsername(Long id, String username);

	long countByBookingIdAndType(Long bookingId, BaggageType type);
}
