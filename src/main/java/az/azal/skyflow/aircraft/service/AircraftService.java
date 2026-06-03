package az.azal.skyflow.aircraft.service;

import az.azal.skyflow.aircraft.dto.AircraftRequest;
import az.azal.skyflow.aircraft.dto.AircraftResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AircraftService {

    AircraftResponse getByRegistrationNumber(String registrationNumber);

    Page<AircraftResponse> getAll(Pageable pageable);

    AircraftResponse create(AircraftRequest request);

    AircraftResponse update(String registrationNumber, AircraftRequest request);

    void delete(String registrationNumber);
}
