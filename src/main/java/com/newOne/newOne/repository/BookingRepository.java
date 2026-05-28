package com.newOne.newOne.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newOne.newOne.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	boolean existsByFlightId(Long flightId);

	List<Booking> findByPassengerUsernameOrderByCreatedAtDesc(String username);

	Optional<Booking> findByIdAndPassengerUsername(Long id, String username);
}
