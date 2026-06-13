package com.newOne.newOne.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.newOne.newOne.entity.Airport;

public interface AirportRepository extends JpaRepository<Airport, Long> {

	boolean existsByCode(String code);

	boolean existsByCodeAndIdNot(String code, Long id);
}
