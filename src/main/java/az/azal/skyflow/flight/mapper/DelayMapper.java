package az.azal.skyflow.flight.mapper;

import az.azal.skyflow.flight.dto.DelayResponse;
import az.azal.skyflow.flight.model.FlightDelay;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DelayMapper {

	@Mapping(source = "flight.id", target = "flightId")
	@Mapping(source = "delayReason", target = "reason")
	DelayResponse toResponse(FlightDelay flightDelay);
}
