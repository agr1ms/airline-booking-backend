package com.newOne.newOne.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.newOne.newOne.dto.AirportRequest;
import com.newOne.newOne.dto.AirportResponse;
import com.newOne.newOne.entity.Airport;
import com.newOne.newOne.exception.ApiException;
import com.newOne.newOne.repository.AirportRepository;

@Service
public class AirportService {

	private final AirportRepository airportRepository;

	public AirportService(AirportRepository airportRepository) {
		this.airportRepository = airportRepository;
	}

	public List<AirportResponse> getAll() {
		return airportRepository.findAll().stream()
				.map(this::toResponse)
				.toList();
	}

	public AirportResponse getById(Long id) {
		Airport airport = airportRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Airport not found"));
		return toResponse(airport);
	}

	@Transactional
	public AirportResponse create(AirportRequest request) {
		String code = request.code().trim().toUpperCase();
		if (airportRepository.existsByCode(code)) {
			throw new ApiException(HttpStatus.CONFLICT, "Airport with code " + code + " already exists");
		}
		Airport airport = new Airport();
		mapRequest(airport, request);
		return toResponse(airportRepository.save(airport));
	}

	@Transactional
	public AirportResponse update(Long id, AirportRequest request) {
		Airport airport = airportRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Airport not found"));

		String code = request.code().trim().toUpperCase();
		if (airportRepository.existsByCodeAndIdNot(code, id)) {
			throw new ApiException(HttpStatus.CONFLICT, "Airport with code " + code + " already exists");
		}

		mapRequest(airport, request);
		return toResponse(airportRepository.save(airport));
	}

	@Transactional
	public void delete(Long id) {
		Airport airport = airportRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Airport not found"));
		airportRepository.delete(airport);
	}

	private void mapRequest(Airport airport, AirportRequest request) {
		airport.setCode(request.code().trim().toUpperCase());
		airport.setName(request.name().trim());
		airport.setCity(request.city().trim());
		airport.setCountry(request.country().trim());
	}

	private AirportResponse toResponse(Airport airport) {
		return new AirportResponse(
				airport.getId(),
				airport.getCode(),
				airport.getName(),
				airport.getCity(),
				airport.getCountry());
	}
}
