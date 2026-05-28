package com.newOne.newOne.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newOne.newOne.entity.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {

	boolean existsByFlightNumber(String flightNumber);

	boolean existsByFlightNumberAndIdNot(String flightNumber, Long id);

	List<Flight> findAllByOrderByDepartureTimeAsc();

	List<Flight> findByDepartureTimeAfterOrderByDepartureTimeAsc(LocalDateTime after);

	List<Flight> findByOriginIgnoreCaseAndDestinationIgnoreCaseAndDepartureTimeAfterOrderByDepartureTimeAsc(
			String origin,
			String destination,
			LocalDateTime after);
}
