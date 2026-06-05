package az.azal.skyflow.crew.service.impl;

import az.azal.skyflow.common.exception.custom.BusinessRuleViolationException;
import az.azal.skyflow.common.exception.custom.ResourceNotFoundException;
import az.azal.skyflow.crew.dto.CrewAssignmentRequest;
import az.azal.skyflow.crew.dto.CrewAssignmentResponse;
import az.azal.skyflow.crew.mapper.CrewMapper;
import az.azal.skyflow.crew.model.AssignmentStatus;
import az.azal.skyflow.crew.model.CrewMember;
import az.azal.skyflow.crew.model.CrewStatus;
import az.azal.skyflow.crew.model.FlightCrewAssignment;
import az.azal.skyflow.crew.repository.CrewMemberRepository;
import az.azal.skyflow.crew.repository.FlightCrewAssignmentRepository;
import az.azal.skyflow.crew.service.FlightCrewAssignmentService;
import az.azal.skyflow.flight.model.Flight;
import az.azal.skyflow.flight.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlightCrewAssignmentServiceImpl implements FlightCrewAssignmentService {

	private final FlightRepository flightRepository;
	private final CrewMemberRepository crewMemberRepository;
	private final FlightCrewAssignmentRepository assignmentRepository;
	private final CrewMapper crewMapper;

	@Override
	@Transactional
	public CrewAssignmentResponse assignCrewToFlights(UUID flightId, CrewAssignmentRequest request) {
		// 1
		Flight flight = flightRepository.findById(flightId)
				.orElseThrow(() -> ResourceNotFoundException.byId("Flight", flightId));
		// 2
		CrewMember crewMember = crewMemberRepository.findById(request.crewMemberId())
				.orElseThrow(() -> ResourceNotFoundException.byId("CrewMember", request.crewMemberId()));
		// 3
		if (crewMember.getStatus() != CrewStatus.AVAILABLE) {
			throw BusinessRuleViolationException.crewNotAvailable(crewMember.getId().toString(),crewMember.getStatus().toString());
		}
		// 4
		boolean alreadyAssigned = assignmentRepository.existsByFlightAndCrewMember(flight, crewMember);
		if (alreadyAssigned) {
			throw BusinessRuleViolationException.alreadyAssignedToFlight(flight.getFlightNumber(), crewMember.getId().toString());
		}
		// 5
		checkTimeConflict(crewMember, flight);
		// 6
		checkRestPeriod(crewMember, flight);
		// 7
		FlightCrewAssignment assignment = new FlightCrewAssignment();
		assignment.setCrewMember(crewMember);
		assignment.setFlight(flight);
		assignment.setRoleOnFlight(request.roleOnFlight());
		assignment.setAssignmentStatus(AssignmentStatus.ASSIGNED);
		assignment.setAssignedAt(LocalDateTime.now());
		assignmentRepository.save(assignment);

		return crewMapper.toAssignmentResponse(assignment);
	}

	@Override
	@Transactional
	public List<FlightCrewAssignment> handleCrewUnavailability(CrewMember crewMember) {

		LocalDateTime now = LocalDateTime.now();

		List<FlightCrewAssignment> futureAssignments =
				assignmentRepository.findFutureAssignmentsByCrewMember(crewMember, now);

		if(futureAssignments.isEmpty()){
			return futureAssignments;
		}

		assignmentRepository.updateFutureAssignmentStatuses(crewMember, AssignmentStatus.REMOVED, now);

		return futureAssignments;
	}

	private void checkTimeConflict(CrewMember crewMember, Flight newFlight) {

		boolean hasConflict = assignmentRepository.hasTimeConflicts(
						crewMember,
						newFlight.getDepartureTime(),
						newFlight.getArrivalTime());

			if (hasConflict) {
				throw BusinessRuleViolationException.timeConflict(crewMember.getId().toString(), newFlight.getFlightNumber());
			}

	}

	private void checkRestPeriod(CrewMember crewMember, Flight newFlight) {

		LocalDateTime windowEnd = newFlight.getArrivalTime();
		LocalDateTime windowStart = windowEnd.minusHours(24);

		long totalFlightMinutes = assignmentRepository.sumFlightMinutesInWindow(crewMember.getId(), windowStart, windowEnd);

		long newFLightDuration = Duration.between(newFlight.getDepartureTime(), newFlight.getArrivalTime()).toMinutes();

		totalFlightMinutes += newFLightDuration;

		if (totalFlightMinutes >= 12 * 60) {
			throw BusinessRuleViolationException.cumulativeHoursViolation(
					crewMember.getEmployeeId(),
					totalFlightMinutes / 60
			);
		}
		if(crewMember.getLastFlightEnd() == null){
			return;
		}

		int requiredRestHours = (totalFlightMinutes > 10 * 60) ? 12 : 8;
		LocalDateTime earliestAllowedDeparture = crewMember.getLastFlightEnd().plusHours(requiredRestHours);

		if (newFlight.getDepartureTime().isBefore(earliestAllowedDeparture)) {
			throw BusinessRuleViolationException.restPeriodViolation(
					crewMember.getEmployeeId(),
					String.valueOf(earliestAllowedDeparture)
			);
		}

	}
}
