#!/bin/bash

export OTEL_EXPORTER=otlp
export OTEL_OTLP_ENDPOINT=http://localhost:4317  # Change this to your actual OTLP endpoint
export OTEL_SERVICE_NAME=demo

./gradlew bootRun 
