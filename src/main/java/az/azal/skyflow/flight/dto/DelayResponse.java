package az.azal.skyflow.flight.dto;

import az.azal.skyflow.flight.model.DelayReason;

import java.time.LocalDateTime;
import java.util.UUID;

public record DelayResponse(
		UUID id,
		UUID flightId,
		DelayReason reason,
		Integer delayMinutes,
		boolean isHighRisk,
		LocalDateTime createdAt
) {
}
