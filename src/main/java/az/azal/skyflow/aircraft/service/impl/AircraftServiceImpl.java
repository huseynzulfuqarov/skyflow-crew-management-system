package az.azal.skyflow.aircraft.service.impl;

import az.azal.skyflow.aircraft.dto.AircraftRequest;
import az.azal.skyflow.aircraft.dto.AircraftResponse;
import az.azal.skyflow.aircraft.mapper.AircraftMapper;
import az.azal.skyflow.aircraft.model.Aircraft;
import az.azal.skyflow.aircraft.model.AircraftStatus;
import az.azal.skyflow.aircraft.repository.AircraftRepository;
import az.azal.skyflow.aircraft.service.AircraftService;
import az.azal.skyflow.common.exception.custom.DuplicateResourceException;
import az.azal.skyflow.common.exception.custom.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AircraftServiceImpl implements AircraftService {

    private final AircraftRepository aircraftRepository;
    private final AircraftMapper aircraftMapper;

    @Override
    @Transactional(readOnly = true)
    public AircraftResponse getByRegistrationNumber(String registrationNumber) {
        Aircraft aircraft = aircraftRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> ResourceNotFoundException.byField("Aircraft", "registrationNumber", registrationNumber));

        return aircraftMapper.toResponse(aircraft);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AircraftResponse> getAll(Pageable pageable) {

        return aircraftRepository.findAll(pageable)
                .map(aircraftMapper::toResponse);
    }

    @Override
    @Transactional
    public AircraftResponse create(AircraftRequest request) {

        if (aircraftRepository.existsByRegistrationNumber(request.registrationNumber())) {
            throw DuplicateResourceException.byField("Aircraft", "registrationNumber", request.registrationNumber());
        }

        Aircraft aircraft = aircraftMapper.toEntity(request);
        aircraftRepository.save(aircraft);
        return aircraftMapper.toResponse(aircraft);
    }

    @Override
    @Transactional
    public AircraftResponse update(String registrationNumber, AircraftRequest request) {
        Aircraft aircraft = aircraftRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> ResourceNotFoundException.byField("Aircraft", "registrationNumber", registrationNumber));

        aircraftMapper.updateEntity(request, aircraft);

        aircraftRepository.save(aircraft);

        return aircraftMapper.toResponse(aircraft);
    }

    @Override
    @Transactional
    public void delete(String registrationNumber) {
        Aircraft aircraft = aircraftRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> ResourceNotFoundException.byField("Aircraft", "registrationNumber", registrationNumber));

        aircraft.setStatus(AircraftStatus.RETIRED);
        aircraftRepository.save(aircraft);
    }
}
