package az.azal.skyflow.crew.service.impl;

import az.azal.skyflow.common.exception.custom.ResourceNotFoundException;
import az.azal.skyflow.crew.dto.CrewResponse;
import az.azal.skyflow.crew.mapper.CrewMapper;
import az.azal.skyflow.crew.model.CrewMember;
import az.azal.skyflow.crew.model.CrewStatus;
import az.azal.skyflow.crew.repository.CrewMemberRepository;
import az.azal.skyflow.crew.service.CrewStatusService;
import az.azal.skyflow.crew.service.FlightCrewAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CrewStatusServiceImpl implements CrewStatusService {


	private final CrewMemberRepository crewMemberRepository;
	private final FlightCrewAssignmentService flightCrewAssignmentService;
	private final CrewMapper crewMapper;

	@Override
	@Transactional
	public CrewResponse updateCrewStatus(UUID crewId, CrewStatus newStatus, String changedBy) {

		// "changedBy" field will be used Audit in the future,
		// when the security is written. For now, it is not used.

		CrewMember crew = crewMemberRepository.findById(crewId)
				.orElseThrow(() -> ResourceNotFoundException.byId("CrewMember", crewId));

		crew.setStatus(newStatus);
		crewMemberRepository.save(crew);

		if(newStatus == CrewStatus.SICK
				|| newStatus == CrewStatus.ON_LEAVE
				|| newStatus == CrewStatus.INACTIVE) {

		flightCrewAssignmentService.handleCrewUnavailability(crew);
		}
		return crewMapper.toResponse(crew);
	}
}
