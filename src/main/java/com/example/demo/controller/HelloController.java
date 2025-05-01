package com.example.demo.controller;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/hello")
    @WithSpan // Creates a new span for this method
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        logger.info("Hello endpoint called with name: {}", name);
        doSomeProcessing(name);
        if ("error".equalsIgnoreCase(name)) {
            logger.error("Simulated exception triggered for name: {}", name);
            throw new RuntimeException("Simulated failure for demonstration");
        }
        return String.format("Hello, %s!", name);
    }

    @WithSpan // Creates a nested span for this method
    private void doSomeProcessing(String name) {
        logger.info("Processing data for: {}", name);
        // Simulate some processing time
       
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

