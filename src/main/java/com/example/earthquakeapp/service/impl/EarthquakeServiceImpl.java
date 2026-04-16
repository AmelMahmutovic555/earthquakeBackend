package com.example.earthquakeapp.service.impl;

import com.example.earthquakeapp.model.Earthquake;
import com.example.earthquakeapp.repository.EarthquakeRepo;
import com.example.earthquakeapp.service.EarthquakeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class EarthquakeServiceImpl implements EarthquakeService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final EarthquakeRepo earthquakeRepo;

    public EarthquakeServiceImpl(WebClient webClient, ObjectMapper objectMapper, EarthquakeRepo earthquakeRepo) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.earthquakeRepo = earthquakeRepo;
    }

    @Override
    public List<Earthquake> getLatestEarthquakes() {
        try {
            String response = webClient.get()
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            JsonNode features = root.get("features");

            List<Earthquake> earthquakes = new ArrayList<>();

            if (features != null && features.isArray()) {
                for (JsonNode feature : features) {
                    JsonNode properties = feature.get("properties");
                    JsonNode geometry = feature.get("geometry");
                    JsonNode coordinates = geometry != null ? geometry.get("coordinates") : null;

                    Double magnitude = properties != null && properties.get("mag") != null && !properties.get("mag").isNull()
                            ? properties.get("mag").asDouble()
                            : null;

                    String magType = properties != null && properties.get("magType") != null && !properties.get("magType").isNull()
                            ? properties.get("magType").asText()
                            : null;

                    String place = properties != null && properties.get("place") != null && !properties.get("place").isNull()
                            ? properties.get("place").asText()
                            : null;

                    LocalTime time = properties != null && properties.get("time") != null && !properties.get("time").isNull()
                            ? Instant.ofEpochMilli(properties.get("time").asLong())
                            .atZone(ZoneId.systemDefault())
                            .toLocalTime()
                            : null;

                    String title = properties != null && properties.get("title") != null && !properties.get("title").isNull()
                            ? properties.get("title").asText()
                            : null;

                    Double longitude = coordinates != null && coordinates.isArray() && coordinates.size() > 0 && !coordinates.get(0).isNull()
                            ? coordinates.get(0).asDouble()
                            : null;

                    Double latitude = coordinates != null && coordinates.isArray() && coordinates.size() > 1 && !coordinates.get(1).isNull()
                            ? coordinates.get(1).asDouble()
                            : null;

                    Double depth = coordinates != null && coordinates.isArray() && coordinates.size() > 2 && !coordinates.get(2).isNull()
                            ? coordinates.get(2).asDouble()
                            : null;

                    Earthquake earthquake = new Earthquake(
                            magnitude,
                            magType,
                            place,
                            title,
                            time,
                            longitude,
                            latitude,
                            depth
                    );

                    earthquakes.add(earthquake);
                }
            }

            earthquakeRepo.deleteAll();
            earthquakeRepo.saveAll(earthquakes);
            return earthquakeRepo.findAll();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch earthquake data: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public List<Earthquake> deleteSpecificEarthquake(String place) {
        Earthquake earthquake = earthquakeRepo.findByPlace(place)
                .orElseThrow(() -> new RuntimeException("The place you provided doesn't have any earthquake records."));

        earthquakeRepo.delete(earthquake);

        return earthquakeRepo.findAll();
    }

    @Override
    public List<Earthquake> findByMagnitude() {
        List<Earthquake> earthquakes = earthquakeRepo.findEarthquakesWithMagnitudeGreaterThanEqualTwo();

        if (earthquakes.isEmpty()) {
            throw new RuntimeException("No earthquakes found with this magnitude.");
        }

        return earthquakes;
    }

    @Override
    public List<Earthquake> findByTime(Integer hour, Integer minute) {
        LocalTime start = LocalTime.of(hour, minute, 0);
        LocalTime end = LocalTime.of(hour, minute, 59);

        List<Earthquake> earthquakes = earthquakeRepo.findByTimeBetween(start, end);

        if (earthquakes.isEmpty()) {
            throw new RuntimeException("No earthquakes found at this time.");
        }

        return earthquakes;
    }
}