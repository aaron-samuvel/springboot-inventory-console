# Change log
## 2026-11-10
### Spring Boot Inventory Console
- Built a Java 21 / Spring Boot 4.1.2 application as an executable WAR for embedded Tomcat and external Apache Tomcat 11 deployment, including Spring MVC, JSP, JSTL, Spring form tags, and `WEB-INF/web.xml`.
- Added PostgreSQL schema migrations through Flyway, indexed product search, bounded 25-row pagination, JDBC-backed sessions, and configurable HikariCP/Tomcat limits.
- Added environment-configured admin credentials, BCrypt password encoding, CSRF protection, health/readiness probes, Prometheus metrics, structured logs, graceful shutdown, and a non-root container image.
- Added PostgreSQL-backed integration coverage, Docker image packaging, CI artifact upload, and a 50-virtual-user k6 profile.
### Verification
The [latest CI run](https://github.com/aaron-samuvel/springboot-inventory-console/actions/runs/42846443580) passed Maven verification (including PostgreSQL integration tests), built the Docker image, and uploaded the WAR artifact.
The k6 profile has not yet been run against staging. Tune capacity on the intended deployment and database, then compare p95 latency and error rate with the service objective before calling the system production-certified.

## 2026-11-12
### Verification update
- A local clean package build passed with Temurin 21.0.13 using `mvn clean package -DskipTests`; test execution was skipped.
- Docker is not available in this environment, so PostgreSQL-backed integration tests and the k6 staging load profile were not run locally. The staging load run remains required before making production-capacity claims.
