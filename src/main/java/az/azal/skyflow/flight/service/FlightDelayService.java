package az.azal.skyflow.flight.service;

import az.azal.skyflow.flight.dto.DelayRequest;
import az.azal.skyflow.flight.dto.DelayResponse;

import java.util.UUID;

public interface FlightDelayService {

	DelayResponse delayFlight(UUID flightId, DelayRequest request, String delayedBy);

}
