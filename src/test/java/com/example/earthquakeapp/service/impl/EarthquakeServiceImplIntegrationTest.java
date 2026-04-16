package com.example.earthquakeapp.service.impl;

import com.example.earthquakeapp.model.Earthquake;
import com.example.earthquakeapp.repository.EarthquakeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class EarthquakeServiceImplIntegrationTest {

    @Autowired
    private EarthquakeServiceImpl earthquakeService;

    @Autowired
    private EarthquakeRepo earthquakeRepo;

    @MockitoBean
    private WebClient webClient;

    @MockitoBean
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @MockitoBean
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        earthquakeRepo.deleteAll();
    }

    @Test
    void getLatestEarthquakes_shouldFetchAndStoreEarthquakesInDatabase() {
        String jsonResponse = """
                {
                  "features": [
                    {
                      "properties": {
                        "mag": 2.7,
                        "magType": "ml",
                        "place": "Nevada",
                        "time": 1713181810000,
                        "title": "M 2.7 - Nevada"
                      },
                      "geometry": {
                        "coordinates": [-116.421, 33.945, 7.1]
                      }
                    },
                    {
                      "properties": {
                        "mag": 3.1,
                        "magType": "mb",
                        "place": "Alaska",
                        "time": 1713181870000,
                        "title": "M 3.1 - Alaska"
                      },
                      "geometry": {
                        "coordinates": [-149.900, 61.218, 10.5]
                      }
                    }
                  ]
                }
                """;

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(jsonResponse));

        List<Earthquake> result = earthquakeService.getLatestEarthquakes();

        assertNotNull(result);
        assertEquals(2, result.size());

        List<Earthquake> saved = earthquakeRepo.findAll();
        assertEquals(2, saved.size());

        Earthquake nevada = saved.stream()
                .filter(e -> "Nevada".equals(e.getPlace()))
                .findFirst()
                .orElseThrow();

        assertEquals(2.7, nevada.getMagnitude());
        assertEquals("ml", nevada.getMagType());
        assertEquals(-116.421, nevada.getLongitude());
        assertEquals(33.945, nevada.getLatitude());
        assertEquals(7.1, nevada.getDepth());
    }

    @Test
    void deleteSpecificEarthquake_shouldDeleteEarthquakeFromDatabase() {
        Earthquake earthquake = new Earthquake(
                2.9,
                "ml",
                "Japan",
                "M 2.9 - Japan",
                LocalTime.of(10, 15, 20),
                139.6917,
                35.6895,
                12.4
        );

        earthquakeRepo.save(earthquake);

        assertTrue(earthquakeRepo.findByPlace("Japan").isPresent());

        earthquakeService.deleteSpecificEarthquake("Japan");

        assertTrue(earthquakeRepo.findByPlace("Japan").isEmpty());
    }
    @Test
    void findByMagnitude_shouldReturnEarthquakesWithMagnitudeGreaterThanEqualTwo() {
        earthquakeRepo.save(new Earthquake(
                2.5,
                "md",
                "Chile",
                "M 2.5 - Chile",
                LocalTime.of(11, 20, 10),
                -70.6693,
                -33.4489,
                15.0
        ));

        earthquakeRepo.save(new Earthquake(
                3.4,
                "ml",
                "Peru",
                "M 3.4 - Peru",
                LocalTime.of(11, 20, 50),
                -77.0428,
                -12.0464,
                22.0
        ));

        List<Earthquake> result = earthquakeService.findByMagnitude();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(e -> e.getMagnitude() >= 2.0));
    }

    @Test
    void findByMagnitude_shouldThrowWhenNoMatchingEarthquakesExist() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                earthquakeService::findByMagnitude);

        assertEquals("No earthquakes found with this magnitude.", ex.getMessage());
    }

    @Test
    void findByTime_shouldReturnEarthquakesWithinSameMinute() {
        earthquakeRepo.save(new Earthquake(
                2.5,
                "md",
                "Turkey",
                "M 2.5 - Turkey",
                LocalTime.of(14, 30, 10),
                32.8597,
                39.9334,
                8.0
        ));

        earthquakeRepo.save(new Earthquake(
                3.0,
                "ml",
                "Greece",
                "M 3.0 - Greece",
                LocalTime.of(14, 30, 45),
                23.7275,
                37.9838,
                10.0
        ));

        earthquakeRepo.save(new Earthquake(
                4.0,
                "mb",
                "Italy",
                "M 4.0 - Italy",
                LocalTime.of(14, 31, 10),
                12.4964,
                41.9028,
                14.0
        ));

        List<Earthquake> result = earthquakeService.findByTime(14, 30);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(e ->
                e.getTime().getHour() == 14 && e.getTime().getMinute() == 30
        ));
    }

    @Test
    void findByTime_shouldThrowWhenNoEarthquakesExistAtThatTime() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> earthquakeService.findByTime(14, 30));

        assertEquals("No earthquakes found at this time.", ex.getMessage());
    }
}