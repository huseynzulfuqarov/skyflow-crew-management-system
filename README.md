# SkyFlow — Crew Management System

Backend service for managing airline crew assignments, flight operations, aircraft tracking, and real-time notifications. Built for AZAL's internal operations team.

## Tech Stack

- **Java 21** + **Spring Boot 4.0.6**
- **PostgreSQL 16** — primary data store
- **Redis 7** — caching and token blacklisting
- **Flyway** — database migrations
- **Spring Security** + **JWT** (jjwt 0.12.6) — stateless auth
- **WebSocket (STOMP)** — real-time notifications
- **MapStruct** — DTO mapping
- **Swagger / OpenAPI 3** — API documentation
- **Docker Compose** — local development environment

## Project Structure

```
src/main/java/az/azal/skyflow/
├── aircraft/          # Aircraft registry (CRUD, status, maintenance tracking)
├── auth/              # Authentication (login, register, JWT refresh/logout)
├── crew/              # Crew members, status management, flight assignments
├── flight/            # Flights, delays, status history, completion workflow
├── notification/      # In-app notifications (unread, mark read, WebSocket push)
└── common/
    ├── audit/         # Audit logging
    ├── config/        # Security, Redis, WebSocket, OpenAPI configs
    ├── exception/     # Global exception handler + custom exceptions
    └── model/         # Base entity
```

Each module follows the same layered pattern: `controller → service → repository → model`, with `dto` and `mapper` packages for request/response transformation.

## Prerequisites

- **Java 21** (make sure `JAVA_HOME` is set)
- **Docker** and **Docker Compose**
- **Maven** (or use the included `mvnw` wrapper)

## Getting Started

### 1. Clone the repo

```bash
git clone https://github.com/your-org/skyflow-crew-management-system.git
cd skyflow-crew-management-system
```

### 2. Set up environment variables

Create a `.env` file in the project root (one is already included):

```
JWT_SECRET_KEY=<your-base64-encoded-secret>
```

You can generate one with:

```bash
openssl rand -base64 32
```

### 3. Start infrastructure

```bash
docker compose up -d
```

This spins up:

| Service    | Port  | Description              |
|------------|-------|--------------------------|
| PostgreSQL | 5432  | Database (`skyflow` DB)  |
| Redis      | 6379  | Cache / token blacklist  |

PostgreSQL health checks are configured — the container will report `healthy` once it's ready to accept connections.

### 4. Run the application

```bash
./mvnw spring-boot:run
```

On first run, Flyway will execute the migration scripts from `src/main/resources/db/migration/` and create all tables automatically.

The app starts on **http://localhost:8080**.

### 5. Open Swagger UI

Go to **http://localhost:8080/swagger-ui.html** to explore and test all endpoints. Use the "Authorize" button to paste your JWT token.

## API Overview

All endpoints are prefixed with `/api/v1`.

### Auth — `/api/v1/auth`

| Method | Endpoint     | Access  | Description                      |
|--------|-------------|---------|----------------------------------|
| POST   | `/login`    | Public  | Get access + refresh tokens      |
| POST   | `/register` | ADMIN   | Create a new user account        |
| POST   | `/refresh`  | Public  | Refresh an expired access token  |
| POST   | `/logout`   | Auth    | Invalidate access + refresh tokens |

### Aircraft — `/api/v1/aircraft`

| Method | Endpoint                    | Access     | Description                    |
|--------|-----------------------------|------------|--------------------------------|
| GET    | `/{registrationNumber}`     | VIEWER+    | Get aircraft by reg. number    |
| GET    | `/`                         | VIEWER+    | List all aircraft (paginated)  |
| POST   | `/`                         | OPERATIONS+| Register a new aircraft        |
| PUT    | `/{registrationNumber}`     | OPERATIONS+| Update aircraft details        |
| DELETE | `/{registrationNumber}`     | ADMIN      | Remove an aircraft             |

### Flights — `/api/v1/flights`

| Method | Endpoint                    | Access     | Description                    |
|--------|-----------------------------|------------|--------------------------------|
| GET    | `/{flightNumber}`           | VIEWER+    | Get flight by number           |
| GET    | `/`                         | VIEWER+    | List all flights (paginated)   |
| POST   | `/`                         | OPERATIONS+| Create a new flight            |
| PUT    | `/{flightNumber}`           | OPERATIONS+| Update flight details          |
| DELETE | `/{flightNumber}`           | ADMIN      | Delete a flight                |
| PATCH  | `/{flightNumber}/status`    | OPERATIONS+| Change flight status           |
| POST   | `/{id}/delay`               | OPERATIONS+| Report a flight delay          |
| POST   | `/{id}/complete`            | OPERATIONS+| Mark flight as completed       |

### Crew — `/api/v1/crew`

| Method | Endpoint              | Access     | Description                   |
|--------|-----------------------|------------|-------------------------------|
| GET    | `/{employeeId}`       | VIEWER+    | Get crew member by ID         |
| GET    | `/`                   | VIEWER+    | List all crew (paginated)     |
| POST   | `/`                   | OPERATIONS+| Add a new crew member         |
| PUT    | `/{employeeId}`       | OPERATIONS+| Update crew member info       |
| DELETE | `/{employeeId}`       | ADMIN      | Remove a crew member          |
| PATCH  | `/{id}/status`        | OPERATIONS+| Update crew member status     |

### Crew Assignments — `/api/v1/flight-crew-assignments`

| Method | Endpoint                | Access     | Description                     |
|--------|------------------------|------------|---------------------------------|
| POST   | `/assign/{flightId}`   | OPERATIONS+| Assign crew member to a flight  |

### Notifications — `/api/v1/notifications`

| Method | Endpoint        | Access | Description                       |
|--------|----------------|--------|-----------------------------------|
| GET    | `/unread`      | Auth   | Get unread notifications (paged)  |
| PATCH  | `/{id}/read`   | Auth   | Mark a notification as read       |
| PATCH  | `/read-all`    | Auth   | Mark all notifications as read    |

## Security & Roles

Authentication is JWT-based and fully stateless. Tokens are validated on every request through a custom filter. Refresh tokens allow re-authentication without credentials. Both access and refresh tokens can be invalidated via the logout endpoint (blacklisted in Redis).

The system uses a role hierarchy:

```
ADMIN > OPERATIONS > VIEWER
```

- **ADMIN** — full access, can register users, delete resources
- **OPERATIONS** — can create/update flights, manage crew, handle delays
- **VIEWER** — read-only access to flights, crew, and aircraft data

Role checks are enforced at the method level with `@PreAuthorize`.

## Database Migrations

Flyway manages the schema. Migration files live in:

```
src/main/resources/db/migration/
```

Naming convention: `V{number}__{description}.sql` (double underscore).

The `ddl-auto` is set to `validate`, so Hibernate only checks that the schema matches the entity definitions — it never modifies the database on its own. All schema changes must go through Flyway.

## WebSocket

Real-time notifications are pushed over WebSocket using STOMP protocol. The WebSocket endpoint is available at `/ws`.

## Pagination

Default page size is **3**, max is **10**. These can be adjusted in `application.yaml` under `spring.data.web.pageable`. All list endpoints accept standard Spring `Pageable` params (`page`, `size`, `sort`).

## Running Tests

```bash
./mvnw test
```

Tests use an in-memory **H2** database, so they don't need Docker to be running.

## Useful Commands

```bash
# Start everything (DB + Redis)
docker compose up -d

# Stop and remove containers (keep data)
docker compose down

# Stop and remove containers + wipe database
docker compose down -v

# Rebuild and run
./mvnw clean spring-boot:run

# Package as JAR
./mvnw clean package -DskipTests
java -jar target/skyflow-0.0.1-SNAPSHOT.jar
```

## Configuration

Key settings in `src/main/resources/application.yaml`:

| Property                                | Default           | Description                    |
|-----------------------------------------|-------------------|--------------------------------|
| `spring.datasource.url`                | `jdbc:postgresql://localhost:5432/skyflow` | DB connection |
| `spring.data.redis.host`              | `localhost`       | Redis host                     |
| `spring.jpa.hibernate.ddl-auto`        | `validate`        | Schema validation mode         |
| `skyflow.security.jwt.access-token-expiration`  | `900000` (15 min) | Access token TTL      |
| `skyflow.security.jwt.refresh-token-expiration` | `604800000` (7 days) | Refresh token TTL  |
