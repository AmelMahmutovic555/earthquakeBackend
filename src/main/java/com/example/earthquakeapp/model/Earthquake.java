package com.example.earthquakeapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalTime;

@Entity
@Data
public class Earthquake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double magnitude;


    private String magType;
    private String place;
    private String title;
    private LocalTime time;

    private Double longitude;
    private Double latitude;
    private Double depth;


    public Earthquake(){}

    public Earthquake(Double magnitude, String magType, String place, String title, LocalTime time, Double longitude, Double latitude, Double depth) {
        this.magnitude = magnitude;
        this.magType = magType;
        this.place = place;
        this.title = title;
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
        this.depth = depth;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(Double magnitude) {
        this.magnitude = magnitude;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getMagType() {
        return magType;
    }

    public void setMagType(String magType) {
        this.magType = magType;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

}
