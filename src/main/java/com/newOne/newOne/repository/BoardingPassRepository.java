package com.newOne.newOne.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newOne.newOne.entity.BoardingPass;

public interface BoardingPassRepository extends JpaRepository<BoardingPass, Long> {

	Optional<BoardingPass> findByBookingId(Long bookingId);

	Optional<BoardingPass> findByBookingIdAndBookingPassengerUsername(Long bookingId, String username);

	boolean existsByBookingId(Long bookingId);

	List<BoardingPass> findByBookingPassengerUsernameOrderByCheckedInAtDesc(String username);

	List<BoardingPass> findByBookingFlightIdOrderByCheckedInAtAsc(Long flightId);

	List<BoardingPass> findAllByOrderByCheckedInAtDesc();
}
