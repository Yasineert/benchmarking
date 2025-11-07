package com.example.benchmark.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * Custom configuration for Spring Data REST. This class sets the query
 * parameter names for paging and limits the maximum page size to avoid
 * excessive payloads during benchmarks.
 */
@Configuration
public class RestRepositoryConfig implements RepositoryRestConfigurer {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.setPageParamName("page");
        config.setLimitParamName("size");
        config.setMaxPageSize(200);
    }
}