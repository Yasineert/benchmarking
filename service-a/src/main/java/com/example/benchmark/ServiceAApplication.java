package com.example.benchmark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Jersey based variant (A). This class simply bootstraps
 * Spring Boot which in turn configures Jersey and JPA. All resources are
 * registered via {@link com.example.benchmark.config.JerseyConfig}.
 */
@SpringBootApplication
public class ServiceAApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceAApplication.class, args);
    }
}