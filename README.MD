# ViableOne Room Reservation Service

A lightweight REST service for reserving rooms and discovering availability within a given time interval. It exposes two
endpoints:

- POST /api/v1/room/book — book a specific room for a time interval
- GET /api/v1/room/available — find any available room for a time interval

The service uses ISO-8601 timestamps (Instant), e.g., 2025-09-15T09:00:00Z, and responds with appropriate HTTP status
codes for success, conflicts, and validation errors.

## Features

- Room booking with conflict detection
- Find an available room for a requested interval

## Technical features

- Logs HTTP requests and responses for better observability using Logbook
- Provides health monitoring and metrics via Spring Boot Actuator endpoints (/actuator/health)

## Tech Stack

- Language: Java 21
- Framework: Spring MVC (Spring Boot)
- Build tool: Gradle (Kotlin DSL)
- Testing: JUnit 5, Spring Boot Test, MockMvc
- Logging: Logback

## Requirements

- JDK 21+
- Gradle Wrapper (provided)

## Running the Application

## Run with Docker

- Build the image:
    - `docker build -t room-reservation-svc:local .`
- Run the container:
    - `docker run --rm -p 8080:8080 room-reservation-svc:local`
- Optional environment variables:
    - Set active profile: `docker run -e SPRING_PROFILES_ACTIVE=prod -p 8080:8080 room-reservation-svc:local`
    - Pass JVM options (memory, GC, debug, etc.): `docker run -e JAVA_OPTS="-Xms256m -Xmx512m" -p 8080:8080 room-reservation-svc:local`

Notes:
- The Dockerfile uses a multi-stage build to produce a small runtime image with JRE 21.
- The application listens on port 8080 by default.

## Run with Gradle

```bash
./gradlew bootRun
```

Alternatively, if you have already built the JAR file, you can run it directly with a specific profile:

```bash
java -jar build/libs/viableone-room-reservation-svc-0.0.1-SNAPSHOT.jar
```

## Running Tests with Gradle

- Run the full test suite:
    - macOS/Linux: `./gradlew test`
    - Windows: `gradlew.bat test`

- Run a single test class (acceptance test only):
    - Using the fully qualified class name:
        - macOS/Linux: `./gradlew test --tests "com.efor.task.viableone.reservation.controller.RoomReservationControllerAcceptanceTest"`
        - Windows: `gradlew.bat test --tests "com.efor.task.viableone.reservation.controller.RoomReservationControllerAcceptanceTest"`

## API Documentation

Swagger UI is available at: http://localhost:8080/swagger-ui.html

API documentation in OpenAPI format: http://localhost:8080/v3/api-docs

## API Endpoints

[Acceptance tests using curl](AcceptanceTests.MD)

### Book a Room

Attempts to create a reservation for the given room and time interval.  
**Time semantics:** `start` is inclusive; `end` is exclusive. All times are UTC (RFC 3339).

```bash
curl -X POST "http://localhost:8080/api/v1/room/book"   -H "accept: application/json"   -H "content-type: application/json"   -d '{
        "roomId": "R-101",
        "reservationStart": "2025-09-20T08:00:00Z",
        "reservationEnd":   "2025-09-20T10:00:00Z"
      }'
```

**201 Created — Example Response**

```json
{
  "roomId": "R-101",
  "reservationStart": "2025-09-20T08:00:00Z",
  "reservationEnd": "2025-09-20T10:00:00Z"
}
```

**409 Conflict — Example Response** (interval overlaps an existing reservation)

```json
{
  "roomId": "R-101",
  "reservationStart": "2025-09-20T08:00:00Z",
  "reservationEnd": "2025-09-20T10:00:00Z"
}
```

**Possible status codes**

- `201 Created` — Reservation successfully created.
- `409 Conflict` — Conflicting reservation exists (body echoes interval).
- `400 Bad Request` — Malformed JSON or parameters.
- `422 Unprocessable Entity` — Invalid interval (e.g., end ≤ start) or business rule violation.

---

### Find Available Room

Finds any room that is fully available in the given interval (no reservation is created).

```bash
curl -X GET "http://localhost:8080/api/v1/room/available?start=2025-09-20T08:00:00Z&end=2025-09-20T10:00:00Z"   -H "accept: application/json"
```

**200 OK — Example Response**

```json
{
  "roomId": "R-205"
}
```

**204 No Content** — No room is available for the entire interval (no body).

**Possible status codes**

- `200 OK` — Available room found (body contains `roomId`).
- `204 No Content` — No room available for the entire interval.
- `400 Bad Request` — Missing/invalid query parameters.
- `422 Unprocessable Entity` — Invalid interval (e.g., end ≤ start).

---

## Notes

- **Time format:** Use RFC 3339/ISO-8601 instants (`YYYY-MM-DDTHH:mm:ssZ`).
- **Interval rules:** Start is inclusive; end is exclusive; both in UTC.
- **Validation:** Business validations and interval checks may return `400`.
- **Content types:** Requests and responses are JSON (`application/json`).

## Configuration

The application supports multiple Spring profiles:

- `default`: Default configuration

Default configuration file: (`src/main/resources/application.yaml`).

### HTTP Request Logging

The application uses Logbook to log HTTP requests and responses, providing enhanced visibility for API interactions.
This is especially useful for debugging external service calls and API usage.

#### Logbook Configuration

Logbook is configured in the application's logback.xml file. Here's an example configuration:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <include resource="org/springframework/boot/logging/logback/console-appender.xml"/>
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>

    <logger name="org.zalando.logbook" level="TRACE" additivity="false">
        <appender-ref ref="CONSOLE"/>
    </logger>
</configuration>
```

In your application.yml, you can further customize Logbook behavior:

```yaml
logbook:
  format:
    style: http
  exclude:
    - /actuator/**
  filter:
    enabled: true
  obfuscate:
    headers:
      - Authorization
      - X-Secret
  write:
    level: INFO
  predicate:
    exclude:
      - path: "/v3/api-docs"
      - path: "/v3/api-docs/**"
      - path: "/swagger-ui/**"
```

## TODOs

- Rounding of the input time
    - The smallest selected unit is minutes -> if the input time is not a whole minute, it would be appropriate to
      select a rounding strategy. Currently, only lower units (seconds, milliseconds, etc.) are truncated.
- Allow configuration of some hard-coded values in the product code
- Room name validation (regex?)
- Possible separation of integration testing (future DB) and unit testing using service mocking
- Optimization for a large number of rooms
- Contract first approach (creating an OpenAPI specification)

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.