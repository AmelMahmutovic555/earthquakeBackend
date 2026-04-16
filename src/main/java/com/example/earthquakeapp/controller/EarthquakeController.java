package com.example.earthquakeapp.controller;

import com.example.earthquakeapp.model.Earthquake;
import com.example.earthquakeapp.service.EarthquakeService;
import com.example.earthquakeapp.service.impl.EarthquakeServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/")
public class EarthquakeController {
    private final EarthquakeServiceImpl earthquakeService;

    public EarthquakeController(EarthquakeServiceImpl earthquakeService) {
        this.earthquakeService = earthquakeService;
    }

    @GetMapping
    public ResponseEntity<List<Earthquake>> getData(){
        return ResponseEntity.ok(earthquakeService.getLatestEarthquakes());
    }

    @GetMapping("/byMagnitude")
    public ResponseEntity<?> findByMagnitude(){
        try {
        return ResponseEntity.ok(earthquakeService.findByMagnitude());
    } catch (RuntimeException e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }
    }

    @GetMapping("/byTime/{hour}/{minute}")
    public ResponseEntity<?> findByTime(@PathVariable Integer hour, @PathVariable Integer minute){
        try {
            return ResponseEntity.ok(earthquakeService.findByTime(hour, minute));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{place}")
    public ResponseEntity<?> deleteData(@PathVariable String place){
        try {
            return ResponseEntity.ok(earthquakeService.deleteSpecificEarthquake(place));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}
