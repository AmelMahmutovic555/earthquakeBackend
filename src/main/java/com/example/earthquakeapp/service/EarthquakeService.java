package com.example.earthquakeapp.service;

import com.example.earthquakeapp.model.Earthquake;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface EarthquakeService {
    List<Earthquake> getLatestEarthquakes();
    List<Earthquake> deleteSpecificEarthquake(String place);
    List<Earthquake> findByMagnitude();
    List<Earthquake> findByTime(Integer hour, Integer minute);
}
