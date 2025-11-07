package com.example.benchmark.config;

import com.example.benchmark.web.CategoryResource;
import com.example.benchmark.web.ItemResource;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

/**
 * Registers JAX‑RS resources with Jersey. The {@code @ApplicationPath} sets
 * the base URI for all endpoints ("/") so that paths defined in resources
 * map directly to the root of the service. Additional providers (e.g. Jackson)
 * are auto‑discovered via classpath scanning.
 */
@Configuration
@ApplicationPath("/")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        // Explicitly register resource classes; Jersey will not pick up Spring
        // managed beans automatically otherwise.
        register(CategoryResource.class);
        register(ItemResource.class);
        // Enable Jackson for JSON serialization/deserialization
        packages("org.glassfish.jersey.jackson");
    }
}