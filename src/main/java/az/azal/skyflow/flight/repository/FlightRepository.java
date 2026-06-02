package az.azal.skyflow.flight.repository;

import az.azal.skyflow.flight.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FlightRepository extends JpaRepository<Flight, UUID> {
	Optional<Flight> findByFlightNumber(String flightNumber);

	boolean existsByFlightNumber(String flightNumber);
}
