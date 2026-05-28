package az.azal.skyflow.flight.service;

import az.azal.skyflow.flight.dto.FlightRequest;
import az.azal.skyflow.flight.dto.FlightResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface FlightService {
	FlightResponse getByFlightNumber(String flightNumber);

	List<FlightResponse> getAll();

	FlightResponse create(FlightRequest request);

	FlightResponse update(String flightNumber, @Valid FlightRequest request);

	FlightResponse delete(String flightNumber);
}
