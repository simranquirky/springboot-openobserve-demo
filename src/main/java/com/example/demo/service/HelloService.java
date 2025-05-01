package com.example.demo.service;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloService.class);
    
    private final Tracer tracer;

    @Autowired
    public HelloService(Tracer tracer) {
        this.tracer = tracer;
    }

    public String getGreeting(String name) {
        Span span = tracer.spanBuilder("get-greeting").startSpan();
        try (var scope = span.makeCurrent()) {
            logger.info("Generating greeting for: {}", name);
            span.setAttribute("name", name);
        
            
            // Simulate some processing time
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error("Sleep interrupted", e);
            }
            
            return "Hello, " + name + "!";
        } finally {
            span.end();
        }
    }
}
