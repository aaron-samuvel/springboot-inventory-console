# Spring Boot Inventory Console

[![Build WAR](https://github.com/aaron-samuvel/springboot-inventory-console/actions/workflows/build.yml/badge.svg)](https://github.com/aaron-samuvel/springboot-inventory-console/actions/workflows/build.yml)

A Java portfolio project demonstrating Spring Boot, Spring MVC, JSP, JSTL, tag libraries, XML deployment descriptors, Apache Tomcat configuration, PostgreSQL, and production-oriented operations.

## Stack

- Java 21 and Spring Boot 4.1.2
- Spring MVC: server-rendered inventory console plus a JSON API
- JSP + JSTL 3 and Spring's JSP form tag library
- Executable WAR for embedded Tomcat and deployment to external Apache Tomcat 11
- PostgreSQL with Flyway migrations, indexes, bounded HikariCP pool, and JDBC-backed sessions
- Spring Security, Bean Validation, Actuator health probes, Prometheus metrics, structured logs
- Docker image runs as a non-root user

## API

The JSON API is versioned under `/api/v1/products` and requires the configured admin account. `GET /api/v1/products?q=bear&page=0` returns a bounded `Slice`; `POST /api/v1/products` creates a product after validation. CSRF protection remains enabled for mutations. The browser console uses Spring Security's form login and includes the CSRF token on its write form.

Example product body:

```json
{
  "sku": "BEAR-100",
  "name": "Industrial Bearing",
  "category": "Bearings",
  "price": 42.50,
  "quantity": 20
}
```

## Official references

- [Spring Boot system requirements](https://docs.spring.io/spring-boot/system-requirements.html)
- [Spring Boot servlet and JSP guidance](https://docs.spring.io/spring-boot/reference/web/servlet.html)
- [Spring Boot Actuator health probes](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html)
- [Spring Boot metrics](https://docs.spring.io/spring-boot/reference/actuator/metrics.html)
- [Apache Tomcat 11 configuration reference](https://tomcat.apache.org/tomcat-11.0-doc/config/)
- [Jakarta Pages 4.0](https://jakarta.ee/specifications/pages/4.0/)
- [Jakarta Tags 3.0](https://jakarta.ee/specifications/tags/3.0/)
- [Spring Boot Testcontainers support](https://docs.spring.io/spring-boot/reference/testing/testcontainers.html)
- [Testcontainers PostgreSQL module](https://java.testcontainers.org/modules/databases/postgres/)

## Local run

Install Java 21, Maven 3.6.3+, and Docker Compose. Create a private `.env` from `.env.example`, set unique local passwords, then start PostgreSQL:

```powershell
Copy-Item .env.example .env
docker compose up -d postgres
$env:APP_ADMIN_USERNAME = "admin"
$env:APP_ADMIN_PASSWORD = "use-a-local-secret"
$env:DATABASE_URL = "jdbc:postgresql://localhost:5432/inventory"
$env:DATABASE_USERNAME = "inventory"
$env:DATABASE_PASSWORD = "local-development-only"
mvn spring-boot:run
```

Open `http://localhost:8080/products`. Sign in with the admin environment values to add products. For an executable WAR:

```powershell
mvn -DskipTests package
java -jar target/inventory-console.war
```

Run `mvn verify` to execute the integration suite. It starts a PostgreSQL 17 container through Testcontainers, so Docker must be available to the test process.

JSP works with Tomcat when packaged as a WAR. JSP is not supported by an executable JAR; this project deliberately uses WAR packaging so both `java -jar` and a standard Tomcat deployment are supported. [Spring Boot JSP guidance](https://docs.spring.io/spring-boot/reference/web/servlet.html)

## External Tomcat 11

1. Build `target/inventory-console.war`.
2. Install Tomcat 11 with Java 21 and configure `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `APP_ADMIN_USERNAME`, and `APP_ADMIN_PASSWORD` in the service environment.
3. Apply `ops/tomcat/server.xml` after adapting connector limits and access-log paths for the host.
4. Deploy the WAR as `webapps/ROOT.war` or use its context path.
5. Terminate TLS at a trusted reverse proxy and set `SESSION_COOKIE_SECURE=true`. Replace the sample `RemoteIpValve` address with the proxy's exact trusted IP range; do not accept forwarded headers from arbitrary clients.

The XML `WEB-INF/web.xml` sets servlet session and cookie behavior. `ops/tomcat/server.xml` provides a separately managed Tomcat connector and access log. Spring Boot configuration remains in `application.yml`; XML is used where it matches the servlet-container deployment model. See the [Tomcat 11 configuration reference](https://tomcat.apache.org/tomcat-11.0-doc/config/) before adapting the server template.

## Operations and load behavior

- `/actuator/health`, `/livez`, and `/readyz` provide health and container probe signals. Health details remain hidden.
- The readiness probe checks the database because inventory requests require it; liveness stays independent of external systems to avoid restart cascades.
- `/actuator/prometheus` exposes Micrometer metrics and is protected by the admin role. The Prometheus example reads its Basic Auth password from a mounted secret file; do not put credentials in `prometheus.yml`.
- Tomcat's request threads, accept queue, and open connections are bounded and configurable with `TOMCAT_*` variables.
- HikariCP has a bounded pool (`DB_POOL_MAX`, default 20). Size the total pool across all replicas below PostgreSQL's connection budget; do not set the per-instance pool independently of replica count.
- Search uses escaped prefix queries, functional indexes, and 25-row `Slice` pagination that avoids total-count queries. Avoids unbounded list responses and leading-wildcard scans; move to keyset pagination if deep navigation becomes a hot path.
- JDBC-backed HTTP sessions allow multiple instances to share login sessions. Keep the application stateless outside the database.
- Graceful shutdown allows in-flight requests time to complete. The container uses JVM memory-aware sizing and runs without root privileges.
- Measure throughput and tail latency against the actual database and deployment platform before increasing pool/thread limits; these defaults are safe starting points, not a capacity guarantee.
- `ops/k6/inventory-smoke.js` supplies a 50-virtual-user browse profile with latency and error-rate thresholds. Use it only against a disposable staging environment and compare its results with an agreed service objective; the script is not a benchmark result.

## Security notes

Do not commit `.env` or production secrets. Use a secret manager in production. Put the app behind HTTPS, keep PostgreSQL private, restrict the Prometheus endpoint to monitoring, apply rate limits to login and API traffic at a trusted gateway, rotate admin credentials, and add an external identity provider for multi-user deployments. The starter app uses one environment-configured admin account so the sample remains small; this is not a substitute for production identity lifecycle management.

## Important validation status

The latest CI run passes the Java 21 WAR build, PostgreSQL-backed integration tests, and Docker image build. A local clean package build also passed with Temurin 21.0.13 (`mvn clean package -DskipTests`). Docker is not available in this environment, so the integration tests and k6 staging load were not run locally. This is a production-oriented portfolio project, not certified for production traffic until a staging load run meets the deployment's service objectives.

## Project map

```text
src/main/java/dev/aaron/inventory/    Spring Boot, MVC, API, persistence, security
src/main/resources/db/migration/      PostgreSQL and JDBC-session schema migrations
src/main/resources/static/assets/      Application static assets
src/main/webapp/WEB-INF/jsp/           JSP views using JSTL and Spring form tags
src/main/webapp/WEB-INF/web.xml        Servlet XML descriptor
src/test/java/                        PostgreSQL-backed integration tests
ops/tomcat/server.xml                  External Tomcat 11 connector configuration
ops/prometheus/prometheus.yml          Prometheus scrape example
ops/k6/inventory-smoke.js              Staging load profile
```
