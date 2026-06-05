package az.azal.skyflow.crew.service;

import az.azal.skyflow.crew.dto.CrewResponse;
import az.azal.skyflow.crew.model.CrewStatus;

import java.util.UUID;

public interface CrewStatusService {

	CrewResponse updateCrewStatus(UUID crewId, CrewStatus newStatus, String changedBy);

}
