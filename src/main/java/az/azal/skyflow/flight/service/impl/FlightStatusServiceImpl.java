package az.azal.skyflow.flight.service.impl;

import az.azal.skyflow.flight.model.Flight;
import az.azal.skyflow.flight.model.FlightStatus;
import az.azal.skyflow.flight.model.FlightStatusHistory;
import az.azal.skyflow.flight.repository.FlightStatusHistoryRepository;
import az.azal.skyflow.flight.service.FlightStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FlightStatusServiceImpl implements FlightStatusService {

	private final FlightStatusHistoryRepository flightStatusHistoryRepository;

	public void changeFlightStatus(Flight flight, FlightStatus flightStatus, String delayedBy) {

		FlightStatusHistory history = new FlightStatusHistory();

		history.setFlight(flight);
		history.setOldStatus(flight.getStatus());

		flight.setStatus(flightStatus);

		history.setNewStatus(flightStatus);
		history.setChangeReason("Flight delayed by " + delayedBy);
		history.setChangedBy(delayedBy);
		history.setChangeTime(LocalDateTime.now());

		flightStatusHistoryRepository.save(history);
	}
}
