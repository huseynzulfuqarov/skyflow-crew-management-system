package az.azal.skyflow.crew.service;

import az.azal.skyflow.crew.dto.CrewRequest;
import az.azal.skyflow.crew.dto.CrewResponse;
import az.azal.skyflow.crew.model.CrewMember;
import az.azal.skyflow.flight.model.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CrewService {
	CrewResponse getCrewByEmployeeId(String employeeId);

	Page<CrewResponse> getAll(Pageable pageable);

	CrewResponse create(CrewRequest request);

	CrewResponse update(String employeeId,CrewRequest request);

	void delete(String employeeId);

	void recordFlightCompletion(CrewMember crewMember, Flight flight);
}
