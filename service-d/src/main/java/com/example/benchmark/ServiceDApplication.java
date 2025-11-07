package com.example.benchmark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Variant D entry point. Uses Spring Data REST to automatically expose CRUD
 * endpoints for repositories. Additional custom controllers may be added to
 * override default behavior.
 */
@SpringBootApplication
public class ServiceDApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceDApplication.class, args);
    }
}