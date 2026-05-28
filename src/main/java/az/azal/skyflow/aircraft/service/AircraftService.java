package az.azal.skyflow.aircraft.service;

import az.azal.skyflow.aircraft.dto.AircraftRequest;
import az.azal.skyflow.aircraft.dto.AircraftResponse;

import java.util.List;

public interface AircraftService {

    AircraftResponse getByRegistrationNumber(String registrationNumber);

    List<AircraftResponse> getAll();

    AircraftResponse create(AircraftRequest request);

    AircraftResponse update(String registrationNumber, AircraftRequest request);

    AircraftResponse delete(String registrationNumber);
}
