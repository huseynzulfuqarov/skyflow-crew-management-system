package az.azal.skyflow.flight.service.impl;

import az.azal.skyflow.aircraft.model.Aircraft;
import az.azal.skyflow.aircraft.repository.AircraftRepository;
import az.azal.skyflow.common.exception.custom.DuplicateResourceException;
import az.azal.skyflow.common.exception.custom.ResourceNotFoundException;
import az.azal.skyflow.flight.dto.FlightRequest;
import az.azal.skyflow.flight.dto.FlightResponse;
import az.azal.skyflow.flight.mapper.FlightMapper;
import az.azal.skyflow.flight.model.Flight;
import az.azal.skyflow.flight.model.FlightStatus;
import az.azal.skyflow.flight.repository.FlightRepository;
import az.azal.skyflow.flight.service.FlightService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

	private final FlightRepository repository;
	private final AircraftRepository aircraftRepository;
	private final FlightMapper flightMapper;

	@Override
	@Transactional(readOnly = true)
	public FlightResponse getByFlightNumber(String flightNumber) {
		return repository.findByFlightNumber(flightNumber)
				.map(flightMapper::toResponse)
				.orElseThrow(() -> ResourceNotFoundException.byField("Flight", "flightNumber", flightNumber));
	}

	@Override
	@Transactional(readOnly = true)
	public List<FlightResponse> getAll() {
		return repository.findAll()
				.stream()
				.map(flightMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional
	public FlightResponse create(FlightRequest request) {

		if(repository.existsByFlightNumber(request.flightNumber())){
			throw DuplicateResourceException.byField("Flight", "flightNumber", request.flightNumber());
		}

		Aircraft aircraft = aircraftRepository.findById(request.aircraftId())
				.orElseThrow(() -> ResourceNotFoundException.byId("Aircraft", request.aircraftId()));

		Flight flight = flightMapper.toEntity(request);
		flight.setAircraft(aircraft);
		flight.setStatus(FlightStatus.SCHEDULED);

		repository.save(flight);
		return flightMapper.toResponse(flight);
	}

	@Override
	@Transactional
	public FlightResponse update(String flightNumber, FlightRequest request) {
		Flight flight = repository.findByFlightNumber(flightNumber)
				.orElseThrow(() -> ResourceNotFoundException.byField("Flight", "flightNumber", flightNumber));

		flightMapper.updateEntity(request, flight);

		repository.save(flight);

		return flightMapper.toResponse(flight);
	}

	@Override
	@Transactional
	public FlightResponse delete(String flightNumber) {
		Flight flight = repository.findByFlightNumber(flightNumber)
				.orElseThrow(() -> ResourceNotFoundException.byField("Flight", "flightNumber", flightNumber));

		flight.setStatus(FlightStatus.CANCELLED);
		repository.save(flight);
		return flightMapper.toResponse(flight);
	}
}
