package az.azal.skyflow.crew.service.impl;

import az.azal.skyflow.common.exception.custom.ResourceNotFoundException;
import az.azal.skyflow.crew.dto.CrewResponse;
import az.azal.skyflow.crew.event.CrewStatusChangedEvent;
import az.azal.skyflow.crew.mapper.CrewMapper;
import az.azal.skyflow.crew.model.CrewMember;
import az.azal.skyflow.crew.model.CrewStatus;
import az.azal.skyflow.crew.repository.CrewMemberRepository;
import az.azal.skyflow.crew.service.CrewStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CrewStatusServiceImpl implements CrewStatusService {

	private final CrewMemberRepository crewMemberRepository;
	private final CrewMapper crewMapper;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	@Transactional
	public CrewResponse updateCrewStatus(UUID crewId, CrewStatus newStatus, String changedBy) {

		// "changedBy" field will be used Audit in the future,
		// when the security is written. For now, it is not used.

		CrewMember crew = crewMemberRepository.findById(crewId)
				.orElseThrow(() -> ResourceNotFoundException.byId("CrewMember", crewId));

		CrewStatus oldStatus = crew.getStatus();
		crew.setStatus(newStatus);
		crewMemberRepository.save(crew);

		eventPublisher.publishEvent(new CrewStatusChangedEvent(
				crew.getId(),
				crew.getEmployeeId(),
				oldStatus,
				newStatus,
				changedBy
		));

		return crewMapper.toResponse(crew);
	}
}