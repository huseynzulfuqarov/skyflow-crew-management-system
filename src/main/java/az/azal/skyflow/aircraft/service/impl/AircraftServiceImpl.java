package az.azal.skyflow.aircraft.service.impl;

import az.azal.skyflow.aircraft.dto.AircraftRequest;
import az.azal.skyflow.aircraft.dto.AircraftResponse;
import az.azal.skyflow.aircraft.mapper.AircraftMapper;
import az.azal.skyflow.aircraft.model.Aircraft;
import az.azal.skyflow.aircraft.model.AircraftStatus;
import az.azal.skyflow.aircraft.repository.AircraftRepository;
import az.azal.skyflow.aircraft.service.AircraftService;
import az.azal.skyflow.common.exception.custom.ResourceAlreadyExistsException;
import az.azal.skyflow.common.exception.custom.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AircraftServiceImpl implements AircraftService {

    private final AircraftRepository aircraftRepository;
    private final AircraftMapper aircraftMapper;

    @Override
    @Transactional(readOnly = true)
    public AircraftResponse getByRegistrationNumber(String registrationNumber) {
        Aircraft aircraft = aircraftRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft not found"));

        return aircraftMapper.toResponse(aircraft);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AircraftResponse> getAll() {

        return aircraftRepository.findAll()
                .stream()
                .map(aircraftMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AircraftResponse create(AircraftRequest request) {

        if (aircraftRepository.existsByRegistrationNumber(request.registrationNumber())) {
            throw new ResourceAlreadyExistsException("Aircraft already exists");
        }

        Aircraft aircraft = aircraftMapper.toEntity(request);
        aircraftRepository.save(aircraft);
        return aircraftMapper.toResponse(aircraft);
    }

    @Override
    @Transactional
    public AircraftResponse update(String registrationNumber, AircraftRequest request) {
        Aircraft aircraft = aircraftRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft not found"));

        aircraftMapper.updateEntity(request, aircraft);

        aircraftRepository.save(aircraft);

        return aircraftMapper.toResponse(aircraft);
    }

    @Override
    public AircraftResponse delete(String registrationNumber) {
        Aircraft aircraft = aircraftRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft not found"));

        aircraft.setStatus(AircraftStatus.RETIRED);
        aircraftRepository.save(aircraft);

        return aircraftMapper.toResponse(aircraft);
    }
}
