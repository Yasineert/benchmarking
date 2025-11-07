package com.example.benchmark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Variant C entry point. Uses Spring MVC controllers annotated with
 * {@code @RestController} for endpoints. Domain model and services are shared
 * with other variants.
 */
@SpringBootApplication
public class ServiceCApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCApplication.class, args);
    }
}