# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build and run all tests
mvn clean verify

# Build with Docker image
mvn clean verify -P docker

# Run a specific test class
mvn test -Dtest=ApplicationTest

# Run a specific test method
mvn test -Dtest=ApplicationTest#getTime

# Run the application (after build)
export APP_SAMPLE_CONFIG=ValueFromShell
java -cp "target/sample-jooq-*-dist/lib/*" \
  -Dapp.http.port=8080 \
  -Dapp.jdbc.url=jdbc:h2:./databases/task-db \
  com.github.phoswald.sample.Application
```

## Architecture

This is a sample Java web app demonstrating jOOQ, Spark Java, and layered architecture.

**Stack:** Java 25, Spark Java (HTTP), jOOQ (SQL), H2 (database), Thymeleaf (templates), JAXB/Yasson (XML/JSON), JUnit 5 + REST-Assured (tests).

**Layers (all under `src/main/java/com/github/phoswald/sample/`):**

- `Application.java` — entry point; registers all Spark routes and wires serialization
- `ApplicationModule.java` — manual service locator / factory (no DI framework); creates database connections and service instances
- `*/TaskResource.java` / `*/SampleResource.java` — REST handlers (JSON/XML responses)
- `*/TaskController.java` / `*/SampleController.java` — HTML page handlers (Thymeleaf rendering)
- `*/TaskRepository.java` — data access via jOOQ DSL; all SQL lives here
- `*/TaskEntity.java` — domain/DTO objects
- `utils/ConfigProvider.java` — reads config from system properties then env vars

**jOOQ code generation** runs at `generate-sources` phase via Maven plugin (config: `src/main/resources/jooq.xml`). Generated classes land in `target/generated-sources/jooq/`.

**Database:** H2 file-based in production, H2 in-memory for tests. Schema defined in `src/main/resources/schema.sql` and applied automatically. Tests construct a fresh in-memory URL per test including `INIT=RUNSCRIPT FROM 'src/main/resources/schema.sql'`.

**Configuration priority:** system properties (`-Dapp.http.port`) > env vars (`APP_HTTP_PORT`) > hardcoded defaults. Key properties: `app.http.port`, `app.jdbc.url`, `app.jdbc.username`, `app.jdbc.password`, `app.sample.config`.

**Test pattern:** `ApplicationTest` overrides `ApplicationModule` via an inner `TestModule` to inject test-specific config. `TaskRepositoryTest` tests the repository directly against an in-memory H2 instance.

**URL structure:**
- `GET /app/rest/...` — REST endpoints (JSON/XML)
- `GET /app/pages/...` — HTML pages (Thymeleaf)
