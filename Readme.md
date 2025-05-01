# 🚀 Monitoring Spring Boot Applications with OpenTelemetry & OpenObserve

This repository demonstrates how to monitor a Spring Boot application using OpenTelemetry and visualize logs and traces in [OpenObserve](https://openobserve.ai/). It includes:

- Distributed tracing using OpenTelemetry SDK
- Log export using the OpenTelemetry Logback appender
- OpenTelemetry Collector to route data to OpenObserve
- A RESTful Spring Boot app with both auto and manual instrumentation

---

## 📦 Project Structure

```bash
.
├── src/main/java/com/example/demo/
│   ├── DemoApplication.java           # Main Spring Boot app
│   ├── controller/HelloController.java  # @WithSpan annotation + logs
│   └── service/HelloService.java        # Manual spans using Tracer
├── src/main/resources/
│   ├── application.properties          # OTLP config
│   └── logback-spring.xml              # OTel logback appender
├── run.sh                              # Script to run the app
├── config.yaml                         # OpenTelemetry Collector config
```

---

## ⚙️ Prerequisites

- Java 17+
- Gradle
- OpenTelemetry Collector (local setup)
- OpenObserve Cloud (Free tier)

---

## 🚀 Getting Started

### 1. Clone the Repo

```bash
git clone https://github.com/simranquirky/springboot-openobserve-demo.git
cd springboot-openobserve-demo
```

---

### 2. Set Up OpenTelemetry Collector

Download and run the OpenTelemetry Collector locally:

```bash
wget https://github.com/open-telemetry/opentelemetry-collector-releases/releases/download/v0.116.0/otelcol-contrib_0.116.0_linux_amd64.tar.gz
mkdir otelcol-contrib
tar xvzf otelcol-contrib_0.116.0_linux_amd64.tar.gz -C otelcol-contrib
```

Copy the provided `config.yaml` into the `otelcol-contrib` folder and start the collector:

```bash
cd otelcol-contrib
./otelcol-contrib --config ../config.yaml
```

---

### 3. Configure OpenObserve

1. Sign up at [OpenObserve Cloud](https://cloud.openobserve.ai)
2. Get your OTLP ingest token and workspace endpoint
3. Update `config.yaml` with your token and endpoint

---

### 4. Run the Application

```bash
chmod +x run.sh
./run.sh
```

Access the app:  
📍 [http://localhost:8080/hello?name=OpenObserve](http://localhost:8080/hello?name=OpenObserve)

---

## 🔎 Observability Features

### ✅ Logs
- Collected using the OpenTelemetry Logback appender
- Exported via OTLP to OpenObserve
- View log fields, levels, and messages in the dashboard

### ✅ Traces
- Generated using `@WithSpan` and manual instrumentation
- Spans contain metadata like function name, custom attributes
- View service map and trace details in OpenObserve

---

## 📸 Screenshots

> Add screenshots of:
- Logs dashboard with structured entries
- Traces timeline showing spans for `/hello`
- OpenTelemetry Collector terminal output

---

## 🧠 Learn More

- [OpenTelemetry Java SDK](https://opentelemetry.io/docs/instrumentation/java/)
- [OpenTelemetry Collector](https://opentelemetry.io/docs/collector/)
- [OpenObserve Documentation](https://openobserve.ai/docs/)

---


## 💡 Author

Made with ❤️ by [Simran Kumari](https://github.com/simranquirky)
```

