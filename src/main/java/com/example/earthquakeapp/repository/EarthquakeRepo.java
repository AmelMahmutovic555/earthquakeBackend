package com.example.earthquakeapp.repository;

import com.example.earthquakeapp.model.Earthquake;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EarthquakeRepo extends JpaRepository<Earthquake, Long> {
    @Query("SELECT e FROM Earthquake e WHERE e.magnitude > 2")
    List<Earthquake> findEarthquakesWithMagnitudeGreaterThanEqualTwo();
    List<Earthquake> findByTimeBetween(LocalTime start, LocalTime end);
    Optional<Earthquake> findByPlace(String place);
    long deleteByPlace(String place);
}
