package az.azal.skyflow.flight.service.impl;

import az.azal.skyflow.aircraft.model.Aircraft;
import az.azal.skyflow.aircraft.model.AircraftStatus;
import az.azal.skyflow.aircraft.repository.AircraftRepository;
import az.azal.skyflow.common.exception.custom.BusinessRuleViolationException;
import az.azal.skyflow.common.exception.custom.DuplicateResourceException;
import az.azal.skyflow.common.exception.custom.ResourceNotFoundException;
import az.azal.skyflow.flight.dto.FlightRequest;
import az.azal.skyflow.flight.dto.FlightResponse;
import az.azal.skyflow.flight.mapper.FlightMapper;
import az.azal.skyflow.flight.model.Flight;
import az.azal.skyflow.flight.model.FlightStatus;
import az.azal.skyflow.flight.repository.FlightRepository;
import az.azal.skyflow.flight.service.FlightStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FlightService Unit Tests")
class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AircraftRepository aircraftRepository;

    @Mock
    private FlightMapper flightMapper;

    @Mock
    private FlightStatusService flightStatusService;

    @InjectMocks
    private FlightServiceImpl flightService;

    @Captor
    private ArgumentCaptor<Flight> flightCaptor;

    private FlightRequest flightRequest;
    private Flight flight;
    private FlightResponse flightResponse;
    private Aircraft aircraft;
    private UUID flightId;
    private UUID aircraftId;

    @BeforeEach
    void setUp() {
        flightId = UUID.randomUUID();
        aircraftId = UUID.randomUUID();

        LocalDateTime departure = LocalDateTime.of(2026, 7, 15, 10, 0);
        LocalDateTime arrival = departure.plusHours(3);

        flightRequest = new FlightRequest("J2101", "GYD", "IST", departure, arrival, null, aircraftId);

        aircraft = new Aircraft();
        aircraft.setId(aircraftId);
        aircraft.setRegistrationNumber("4K-AZ01");
        aircraft.setStatus(AircraftStatus.ACTIVE);

        flight = new Flight();
        flight.setId(flightId);
        flight.setFlightNumber("J2101");
        flight.setDepartureAirport("GYD");
        flight.setDestinationAirport("IST");
        flight.setDepartureTime(departure);
        flight.setArrivalTime(arrival);
        flight.setStatus(FlightStatus.SCHEDULED);
        flight.setAircraft(aircraft);

        flightResponse = new FlightResponse(
                flightId, "J2101", aircraftId, "GYD", "IST",
                FlightStatus.SCHEDULED, departure, arrival, null, null, null, null);
    }

    @Nested
    @DisplayName("Create Operations")
    class CreateTests {

        @Test
        @DisplayName("Valid request → successfully created, status becomes SCHEDULED")
        void create_whenValidRequest_shouldCreateFlightWithScheduledStatus() {

            when(flightRepository.existsByFlightNumber("J2101")).thenReturn(false);
            when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.of(aircraft));
            when(flightMapper.toEntity(flightRequest)).thenReturn(flight);
            when(flightMapper.toResponse(flight)).thenReturn(flightResponse);

            FlightResponse result = flightService.create(flightRequest);

            assertThat(result).isNotNull();
            assertThat(result.flightNumber()).isEqualTo("J2101");
            assertThat(result.status()).isEqualTo(FlightStatus.SCHEDULED);

            verify(flightRepository).save(flightCaptor.capture());

            Flight captured = flightCaptor.getValue();

            assertThat(captured.getStatus()).isEqualTo(FlightStatus.SCHEDULED);
            assertThat(captured.getAircraft()).isEqualTo(aircraft);
        }

        @Test
        @DisplayName("Duplicate flightNumber → DuplicateResourceException")
        void create_whenDuplicateFlightNumber_shouldThrowDuplicateException() {
            when(flightRepository.existsByFlightNumber("J2101")).thenReturn(true);

            assertThatThrownBy(() -> flightService.create(flightRequest))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("J2101");

            verify(aircraftRepository, never()).findById(any());
            verify(flightRepository, never()).save(any());
        }

        @Test
        @DisplayName("When aircraft is not found → ResourceNotFoundException")
        void create_whenAircraftNotFound_shouldThrowNotFoundException() {
            when(flightRepository.existsByFlightNumber("J2101")).thenReturn(false);
            when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> flightService.create(flightRequest))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(flightRepository, never()).save(any());
        }

        @Test
        @DisplayName("When aircraft is in MAINTENANCE status → BusinessRuleViolationException")
        void create_whenAircraftNotActive_shouldThrowBusinessRuleViolation() {

            when(flightRepository.existsByFlightNumber("J2101")).thenReturn(false);
            when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.of(aircraft));

            aircraft.setStatus(AircraftStatus.MAINTENANCE);

            assertThatThrownBy(() -> flightService.create(flightRequest))
                    .isInstanceOf(BusinessRuleViolationException.class);

            verify(flightRepository, never()).save(any());
        }

        @Test
        @DisplayName("When aircraft is in RETIRED status → BusinessRuleViolationException")
        void create_whenAircraftRetired_shouldThrowBusinessRuleViolation() {
            aircraft.setStatus(AircraftStatus.RETIRED);

            when(flightRepository.existsByFlightNumber("J2101")).thenReturn(false);
            when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.of(aircraft));

            assertThatThrownBy(() -> flightService.create(flightRequest))
                    .isInstanceOf(BusinessRuleViolationException.class);

            verify(flightRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Operations")
    class GetTests {

        @Test
        @DisplayName("Existing flightNumber → FlightResponse is returned")
        void getByFlightNumber_whenExists_shouldReturnResponse() {
            when(flightRepository.findByFlightNumber("J2101"))
                    .thenReturn(Optional.of(flight));
            when(flightMapper.toResponse(flight)).thenReturn(flightResponse);

            FlightResponse result = flightService.getByFlightNumber("J2101");

            assertThat(result).isNotNull();
            assertThat(result.flightNumber()).isEqualTo("J2101");
        }

        @Test
        @DisplayName("Non-existing flightNumber → ResourceNotFoundException")
        void getByFlightNumber_whenNotExists_shouldThrowNotFoundException() {
            when(flightRepository.findByFlightNumber("J2999"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> flightService.getByFlightNumber("J2999"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("J2999");
        }

        @Test
        @DisplayName("getAll → works with Pageable")
        void getAll_shouldReturnPageOfResponses() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Flight> flightPage = new PageImpl<>(List.of(flight));

            when(flightRepository.findAll(pageable)).thenReturn(flightPage);
            when(flightMapper.toResponse(flight)).thenReturn(flightResponse);

            Page<FlightResponse> result = flightService.getAll(pageable);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).flightNumber()).isEqualTo("J2101");
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateTests {

        @Test
        @DisplayName("Update in SCHEDULED status → successful")
        void update_whenScheduledStatus_shouldSucceed() {

            FlightResponse updatedResponse = new FlightResponse(
                    flightId, "J2101", aircraftId, "GYD", "AYT",
                    FlightStatus.SCHEDULED, flight.getDepartureTime(), flight.getArrivalTime(),
                    null, null, null, null);

            when(flightRepository.findByFlightNumber("J2101")).thenReturn(Optional.of(flight));
            when(flightRepository.save(flight)).thenReturn(flight);
            when(flightMapper.toResponse(flight)).thenReturn(updatedResponse);

            flight.setStatus(FlightStatus.SCHEDULED);

            FlightResponse result = flightService.update("J2101", flightRequest);

            assertThat(result).isNotNull();

            verify(flightMapper).updateEntity(flightRequest, flight);
            verify(flightRepository).save(flight);
        }

        @Test
        @DisplayName("Update in ARRIVED status → BusinessRuleViolationException")
        void update_whenArrivedStatus_shouldThrowException() {

            when(flightRepository.findByFlightNumber("J2101")).thenReturn(Optional.of(flight));

            flight.setStatus(FlightStatus.ARRIVED);

            assertThatThrownBy(() -> flightService.update("J2101", flightRequest))
                    .isInstanceOf(BusinessRuleViolationException.class);

            verify(flightRepository, never()).save(any());
        }

        @Test
        @DisplayName("Update in CANCELLED status → BusinessRuleViolationException")
        void update_whenCancelledStatus_shouldThrowException() {
            when(flightRepository.findByFlightNumber("J2101")).thenReturn(Optional.of(flight));

            flight.setStatus(FlightStatus.CANCELLED);

            assertThatThrownBy(() -> flightService.update("J2101", flightRequest))
                    .isInstanceOf(BusinessRuleViolationException.class);

            verify(flightRepository, never()).save(any());
        }

        @Test
        @DisplayName("Update non-existing flight → ResourceNotFoundException")
        void update_whenNotExists_shouldThrowNotFoundException() {
            when(flightRepository.findByFlightNumber("J2999")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> flightService.update("J2999", flightRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteTests {

        @Test
        @DisplayName("Existing flight delete → status becomes CANCELLED (via FlightStatusService)")
        void delete_whenExists_shouldCancelFlight() {
            flight.setStatus(FlightStatus.SCHEDULED);

            when(flightRepository.findByFlightNumber("J2101")).thenReturn(Optional.of(flight));

            flightService.delete("J2101");

            verify(flightStatusService).changeFlightStatus(
                    eq(flight), eq(FlightStatus.CANCELLED), eq("SYSTEM"), eq("Flight deleted"));

            verify(flightRepository).save(flight);
        }

        @Test
        @DisplayName("Delete non-existing flight → ResourceNotFoundException")
        void delete_whenNotExists_shouldThrowNotFoundException() {
            when(flightRepository.findByFlightNumber("J2999")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> flightService.delete("J2999"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("ChangeStatus Operations")
    class ChangeStatusTests {

        @Test
        @DisplayName("Valid status change → FlightStatusService is called")
        void changeStatus_whenFlightExists_shouldDelegateToStatusService() {
            when(flightRepository.findByFlightNumber("J2101")).thenReturn(Optional.of(flight));
            when(flightMapper.toResponse(flight)).thenReturn(flightResponse);

            FlightResponse result = flightService.changeStatus("J2101", FlightStatus.BOARDING, "Test reason");

            verify(flightStatusService).changeFlightStatus(
                    eq(flight), eq(FlightStatus.BOARDING), eq("SYSTEM"), eq("Test reason"));
            verify(flightRepository).save(flight);

            assertThat(result).isNotNull();
        }
    }
}