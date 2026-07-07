# World Cup Match Tracker

A lightweight Spring Boot backend and static browser UI for browsing FIFA World Cup 2026 match results. The service stores matches in an H2 database, exposes a REST API, and seeds a representative dataset automatically on startup.

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker Desktop (optional, for Compose)

## Run with Maven

From the repository root:

```bash
cd world-cup-match-tracker-service
./mvnw spring-boot:run
```

The API will be available at:
- http://localhost:8080/api/matches
- http://localhost:8080/h2-console

### Sample API calls

List matches:

```bash
curl "http://localhost:8080/api/matches?size=5"
```

Filter by team:

```bash
curl "http://localhost:8080/api/matches?team=Argentina"
```

Filter by stage and date:

```bash
curl "http://localhost:8080/api/matches?stage=group&date=2026-06-11"
```

Create a match:

```bash
curl -X POST http://localhost:8080/api/matches \
  -H "Content-Type: application/json" \
  -d '{"homeTeam":"Brazil","awayTeam":"Uruguay","homeScore":2,"awayScore":1,"matchDate":"2026-06-14","location":"Miami","stage":"group"}'
```

## Run with Docker Compose

From the repository root:

```bash
docker compose up --build
```

Then open:
- http://localhost:8080/api/matches
- http://localhost:8080/h2-console

## UI

Open the static UI in your browser from:

- world-cup-match-tracker-app/index.html

The UI calls the backend API and supports filtering by team, date, and stage.

## Notes

- Sample data is seeded automatically when the database is empty.
- The backend uses H2 in-memory storage for local development.
- The main API contract is:
  - GET /api/matches
  - GET /api/matches/{id}
  - POST /api/matches
  - DELETE /api/matches/{id}
