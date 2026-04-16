package com.example.earthquakeapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .baseUrl("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson")
                .build();
    }
}
