# Monitoring Spring Boot Applications: Logs and Traces with OpenObserve

Modern applications are distributed, fast-moving, and complex. As systems scale, understanding what's happening inside your services becomes critical. That's where application monitoring comes in — specifically **logs** and **traces**, which help you pinpoint issues, track user flows, and debug production problems faster.

In this guide, you'll learn how to monitor a Spring Boot application using **OpenObserve**, an open-source observability platform that supports logs, metrics, and traces — all in one place. We'll also use the **OpenTelemetry Collector** to standardize and send our telemetry data to OpenObserve.

By the end, you'll have a fully working setup to view real-time logs and distributed traces from your Spring Boot service.

---

## What You'll Build

Here's what we’re setting up:

- A sample Spring Boot application
- Logs and traces instrumented via **OpenTelemetry**
- An **OpenTelemetry Collector** to receive, process, and export telemetry
- An **OpenObserve Cloud** workspace to analyze logs and traces

### **Architecture Overview:**


![](/assets/architecture.png)

| Component             | Role                                                                 |
|----------------------|----------------------------------------------------------------------|
| **Spring Boot App**   | Emits logs (via `logback-spring.xml`) and traces (via OTel SDK)     |
| **OTel Collector**    | Receives telemetry, optionally processes it, and forwards to backend |
| **OpenObserve**       | Visualizes logs and traces; enables filtering, analysis, and search  |
---

## 1. Setting Up OpenObserve Cloud

Before we dive into the code, let’s get OpenObserve Cloud set up.

### 🔧 Step-by-Step:

1. **Create an account** at [https://cloud.openobserve.ai](https://cloud.openobserve.ai)  
   > You can use the free tier for testing purposes.
   > You can host OpenObserve locally as well, read the [documentation](https://openobserve.ai/docs/quickstart/).
2. **Testing Log Ingestion to OpenObserve**
    - Go to `DataSources` section , under `Custom` tab, you will find the Curl command for ingestion.

    ![](/assets/testing_openobserve.png)

    - Copy the curl command and run it in your local terminal window. Once executed, go to the OpenObserve UI , `Logs` section, select the stream specified in Curl command , usually `default` and run the query. If you are able to see a log entry, great job! You have successfully ingested data to OpenObserve using Curl command.

    ![](/assets/viewing_logs.png)

3. **Fetching your API credentials**

- Go to `DataSources` section and get credentials in expected format. For instance, in this case we are using Opentelemtry for traces collection.

    ![](/assets/finding_openobserve_creds.png)
   - These will be used later by the OpenTelemetry Collector or log forwarders to push telemetry data to OpenObserve.
   - You’ll typically need:
     - `Ingest Token`
     - `OTLP endpoint` (usually something like `https://api.openobserve.ai/api/default`)

4. **Explore the UI briefly**
   - Familiarize yourself with the Logs, Traces, and Dashboard tabs — you’ll return to them after setup.

> ✅ Once done, you're ready to configure the OpenTelemetry Collector and start sending telemetry data.

---

## 2. Configuring the OpenTelemetry Collector

To decouple our application from the observability backend and give us more control, we'll route all telemetry through the **OpenTelemetry Collector**. This collector acts as a telemetry pipeline — it receives logs and traces, processes them (batching, filtering, etc.), and then exports them to OpenObserve.

### 🔧 Step-by-Step Setup

#### Step 1: Download the Otel Collector (Contrib Build)

Run the following command to download the latest contrib binary (v0.116.0 as of writing):

```bash
wget https://github.com/open-telemetry/opentelemetry-collector-releases/releases/download/v0.116.0/otelcol-contrib_0.116.0_linux_amd64.tar.gz
```

This will download the tarball for Linux.


#### Step 2: Extract the Archive

Create a folder and extract the tarball:

```bash
mkdir otelcol-contrib
tar xvzf otelcol-contrib_0.116.0_linux_amd64.tar.gz -C otelcol-contrib
```

This creates the `otelcol-contrib` directory with the collector binary and supporting files.

---
#### Step 3: Create the Collector Configuration

Inside the `otelcol-contrib` folder, create a file named `config.yaml` with the following content:

```yaml
receivers:
  otlp:
    protocols:
      grpc:
      http:

exporters:
  otlphttp/openobserve:
    endpoint: "<openobserve_api_http_endpoint>"
    headers:
      Authorization: "<auth_token>"

processors:
  batch:

service:
  pipelines:
    traces:
      receivers: [otlp]
      processors: [batch]
      exporters: [otlphttp/openobserve]
    logs:
      receivers: [otlp]
      processors: [batch]
      exporters: [otlphttp/openobserve]
```

🔍 Replace:
- `<openobserve_api__http_endpoint>` with your actual OpenObserve http endpoint 
- `<auth-token>` with your API token from OpenObserver
> Note: You copied these credentials in previous section

#### Step 4: Run the Collector

From inside the `otelcol-contrib` directory, run:

```bash
./otelcol-contrib --config config.yaml
```

You should see logs indicating it’s listening on:
- Port `4317` (gRPC)
- Port `4318` (HTTP)

![](/assets/otel-collector.png)


✅ The OpenTelemetry Collector is now ready to receive telemetry data from your Spring Boot app and forward it to OpenObserve!

📌 Tip: The `batch` processor is optional but recommended. It buffers telemetry for better performance and network efficiency.


## 3. Configuring Spring Boot to Send Logs and Traces to OpenTelemetry Collector

With the OpenTelemetry Collector running locally, the next step is to configure your Spring Boot application to emit logs and traces — both of which will be routed to OpenObserve via the Collector.

The project you're working with already includes all the necessary components, and it will be available on GitHub here:  
👉 [GitHub Repository: Monitoring Spring Boot with OpenObserve](https://github.com/simranquirky/springboot-openobserve-demo)

## 🧩 About the Application

This is a simple Spring Boot REST API that exposes a `/hello` endpoint. When accessed, it logs a message, triggers nested service logic, and emits distributed traces. The app is instrumented using the OpenTelemetry Java SDK — with both automatic (`@WithSpan`) and manual span creation — to demonstrate how real-time logs and traces can be collected and visualized using OpenObserve.

### 🧩 Project Structure Highlights

Your codebase contains the following relevant files:

| File                     | Purpose                                      |
|--------------------------|----------------------------------------------|
| `HelloController.java`   | REST controller emitting logs and spans      |
| `HelloService.java`      | Service layer with manual span instrumentation |
| `logback-spring.xml`     | Sends structured logs via the OTel Logback Appender |
| `application.properties` | OTLP exporter configuration for OTel SDK     |
| `run.sh`                 | Bootstraps the app with required env vars    |

---

### ⚙️ Configuration Details

#### `application.properties`

Located [here](./src/main/resources/application.properties), this config enables OTLP export for traces, logs, and metrics:

```properties
otel.traces.exporter=otlp
otel.metrics.exporter=otlp
otel.logs.exporter=otlp
otel.exporter.otlp.endpoint=http://localhost:4317
```

> This points telemetry to your locally running OpenTelemetry Collector.

---

#### `logback-spring.xml`

Your logs are sent using the `OpenTelemetryAppender`:

```xml
<configuration>
  <appender name="OTEL" class="io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender" />
  <root level="INFO">
    <appender-ref ref="OTEL"/>
  </root>
</configuration>
```

✅ This enables automatic log export to OpenTelemetry.

---

#### Instrumented Code Examples

Your REST controller uses the `@WithSpan` annotation to generate spans automatically:

```java
@GetMapping("/hello")
@WithSpan
public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    logger.info("Hello endpoint called with name: {}", name);
    doSomeProcessing(name);
    return String.format("Hello, %s!", name);
}
```

And your service includes **manual span creation** using the OpenTelemetry API:

```java
Span span = tracer.spanBuilder("get-greeting").startSpan();
...
span.setAttribute("name", name);
...
span.end();
```

These spans are exported to the collector and visualized in OpenObserve.

---

### ▶️ Running the Application

Use the provided `run.sh` script to start the application with correct environment variables:

```bash
./run.sh
```

This script sets:
- OTLP endpoint
- Service name
- Launches the Spring Boot app

---

Next, hit the endpoint `/hello` to generate logs and traces

![](/assets/Making_curl_requests.png)

✅ Once the app is running and the `/hello` endpoint is hit, logs and spans will be sent through the OTel Collector and appear in your OpenObserve dashboard.


## 4. Viewing and Analyzing Logs and Traces in OpenObserve

With the Spring Boot app sending telemetry through the OpenTelemetry Collector, it’s time to explore how to view and analyze your data in OpenObserve.

---

### 🔍 Logs 

1. Go to the **Logs** tab in OpenObserve.
2. Select stream to see logs corresponding to the stream, in this case `default`. Click on any log entry to see the detailed view

![](/assets/logs.png)

3. Filter logs based on service name.

![](/assets/service_filter.png)

The number of entries remains the same on running the query as all the entries are from demo service. To see the changes filter logs based on message body.

![](/assets/filter_on_message.png)



> ✅ You can search by field (e.g., `host_name=<host_name>`) or log level (e.g., `severity=INFO`), thanks to structured logging.

---

### 🧵 Traces Dashboard

1. Go to the **Traces** tab.
2. Select a recent trace — e.g., one triggered by `/hello`.

![](/assets/traces.png)

3. Inspect the spans:
   - Root span: `hello` endpoint
   - Nested span: `doSomeProcessing`

![](/assets/span.png)

> 🔎 Look at custom attributes like `name=OpenObserve` and timing data between spans.
---

### 📌 Tips

- Use time range filters to narrow down your investigation
- Correlate logs and traces for deeper debugging
- Use OpenObserve’s “Explore” view to write custom queries

---

This gives you a unified, developer-friendly way to observe and debug your Spring Boot app — without jumping between multiple tools.
