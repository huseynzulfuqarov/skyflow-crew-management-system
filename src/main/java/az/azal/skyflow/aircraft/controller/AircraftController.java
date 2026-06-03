package az.azal.skyflow.aircraft.controller;

import az.azal.skyflow.aircraft.dto.AircraftRequest;
import az.azal.skyflow.aircraft.dto.AircraftResponse;
import az.azal.skyflow.aircraft.service.AircraftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/aircraft")
@RequiredArgsConstructor
public class AircraftController {

    private final AircraftService aircraftService;

    @GetMapping("/{registrationNumber}")
    public ResponseEntity<AircraftResponse> getByRegistrationNumber(@PathVariable String registrationNumber){
        return ResponseEntity.ok(aircraftService.getByRegistrationNumber(registrationNumber));
    }

    @GetMapping()
    public ResponseEntity<Page<AircraftResponse>> getAll(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable){
        return ResponseEntity.ok(aircraftService.getAll(pageable));
    }

    @PostMapping()
    public ResponseEntity<AircraftResponse> create(@Valid @RequestBody AircraftRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(aircraftService.create(request));
    }

    @PutMapping("/{registrationNumber}")
    public ResponseEntity<AircraftResponse> update(@PathVariable String registrationNumber, @Valid @RequestBody AircraftRequest request){
        return ResponseEntity.ok(aircraftService.update(registrationNumber, request));
    }

    @DeleteMapping("/{registrationNumber}")
    public ResponseEntity<Void> delete(@PathVariable String registrationNumber){
        aircraftService.delete(registrationNumber);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}