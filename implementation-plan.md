# World Cup Match Tracker Implementation Plan

**Branch:** feature/world-cup-match-tracker
**Scope:** Build a Spring Boot backend and lightweight UI for FIFA World Cup 2026 match results with H2 persistence, search and filter support, sample data, and clear local run instructions.
**Affected modules:** world-cup-match-tracker-service, world-cup-match-tracker-app
**Estimated steps:** 7

## Description

Implement an API-first backend in the service module, seed a large dataset, expose match result endpoints for listing, searching, adding, and removing records, and add a simple browser-based UI under the app folder that consumes the API. Keep the implementation modular and maintainable by separating domain, persistence, service, and presentation concerns.

## Sequential steps

### Step 1: Define the match result model and persistence layer
**Goal:** Create the core domain entity and repository so the application can persist match data in H2.
**Components:**
- world-cup-match-tracker-service/src/main/java/com/addario/worldcupmatchtracker/domain/MatchResult.java
- world-cup-match-tracker-service/src/main/java/com/addario/worldcupmatchtracker/repository/MatchResultRepository.java
- world-cup-match-tracker-service/src/main/resources/application.properties

**Draft code:**
```java
@Entity
@Table(name = "match_results")
public class MatchResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String homeTeam;

    @Column(nullable = false)
    private String awayTeam;

    @Column(nullable = false)
    private Integer homeScore;

    @Column(nullable = false)
    private Integer awayScore;

    @Column(nullable = false)
    private LocalDate matchDate;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String stage;
}
```

```java
public interface MatchResultRepository extends JpaRepository<MatchResult, Long>, JpaSpecificationExecutor<MatchResult> {
}
```

**Changes:**
- Add the `MatchResult` entity with fields for teams, score, date, location, and stage.
- Configure H2 as the datasource and enable schema generation for local development.

**Done when:** The application starts successfully and the `match_results` table is created in H2.

### Step 2: Implement the service layer and REST API contract
**Goal:** Expose clear endpoints for listing, searching, adding, and removing match results.
**Components:**
- world-cup-match-tracker-service/src/main/java/com/addario/worldcupmatchtracker/dto/MatchResultRequest.java
- world-cup-match-tracker-service/src/main/java/com/addario/worldcupmatchtracker/dto/MatchResultResponse.java
- world-cup-match-tracker-service/src/main/java/com/addario/worldcupmatchtracker/service/MatchResultService.java
- world-cup-match-tracker-service/src/main/java/com/addario/worldcupmatchtracker/api/MatchResultResource.java

**Draft code:**
```java
@RestController
@RequestMapping("/api/matches")
public class MatchResultResource {
    private final MatchResultService service;

    @GetMapping
    public Page<MatchResultResponse> list(
            @RequestParam(required = false) String team,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String stage,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.search(team, date, stage, page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MatchResultResponse create(@Valid @RequestBody MatchResultRequest request) {
        return service.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
```

**Changes:**
- Add `GET /api/matches` for listing and filtering by team, date, and stage.
- Add `POST /api/matches` for creating a match result.
- Add `DELETE /api/matches/{id}` for removing a match result.
- Use DTOs at the API boundary so the entity is not exposed directly.

**Done when:** The endpoints return JSON and respond correctly to create and delete operations.

### Step 3: Add server-side pagination and filtering for large datasets
**Goal:** Keep the application responsive when the dataset grows beyond a few hundred records.
**Components:**
- world-cup-match-tracker-service/src/main/java/com/addario/worldcupmatchtracker/service/MatchResultService.java
- world-cup-match-tracker-service/src/main/java/com/addario/worldcupmatchtracker/repository/MatchResultRepository.java

**Draft code:**
```java
public Page<MatchResultResponse> search(String team, LocalDate date, String stage, int page, int size) {
    Specification<MatchResult> spec = Specification.where(null);
    if (team != null && !team.isBlank()) {
        spec = spec.and((root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("homeTeam")), "%" + team.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("awayTeam")), "%" + team.toLowerCase() + "%"))
        );
    }
    if (date != null) {
        spec = spec.and((root, query, cb) -> cb.equal(root.get("matchDate"), date));
    }
    if (stage != null && !stage.isBlank()) {
        spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("stage")), stage.toLowerCase()));
    }
    return repository.findAll(spec, PageRequest.of(page, size, Sort.by("matchDate").descending()))
            .map(mapper::toResponse);
}
```

**Changes:**
- Introduce paging and filtering in the service layer.
- Ensure queries run server-side instead of loading the entire dataset into memory.

**Done when:** The API accepts pagination parameters and returns filtered slices of data.

### Step 4: Seed the database with a large sample dataset
**Goal:** Provide easy setup with at least 1,000 match records.
**Components:**
- world-cup-match-tracker-service/src/main/java/com/addario/worldcupmatchtracker/config/DataLoader.java

**Draft code:**
```java
@Component
public class DataLoader implements ApplicationRunner {
    private final MatchResultRepository repository;

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() == 0) {
            List<MatchResult> matches = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                matches.add(new MatchResult(
                        pickTeam(i),
                        pickTeam(i + 7),
                        i % 5,
                        (i + 1) % 5,
                        LocalDate.of(2026, 6, 11 + (i % 30)),
                        pickVenue(i),
                        pickStage(i)
                ));
            }
            repository.saveAll(matches);
        }
    }
}
```

**Changes:**
- Add a startup seeder that creates 1,000+ representative match records when the database is empty.
- Use a deterministic set of teams, venues, and stages so the sample data is easy to inspect.

**Done when:** The application boots with a populated dataset and the list endpoint returns seeded records.

### Step 5: Build the lightweight browser UI under the app folder
**Goal:** Provide a functional front end for viewing and filtering match results.
**Components:**
- world-cup-match-tracker-app/index.html
- world-cup-match-tracker-app/styles.css
- world-cup-match-tracker-app/app.js

**Draft code:**
```html
<input id="teamFilter" placeholder="Filter by team" />
<input id="dateFilter" type="date" />
<select id="stageFilter">
  <option value="">All stages</option>
  <option value="group">Group</option>
  <option value="quarter-final">Quarter-final</option>
</select>
<table id="matchesTable"></table>
```

```javascript
async function loadMatches() {
  const params = new URLSearchParams({
    team: teamFilter.value,
    date: dateFilter.value,
    stage: stageFilter.value
  });
  const response = await fetch(`/api/matches?${params.toString()}`);
  const data = await response.json();
  renderMatches(data.content);
}
```

**Changes:**
- Create a simple static UI with a filter form and a results table.
- Fetch data from the backend and render team, score, date, location, and stage.
- Keep the UI minimal and focused on usability rather than visual complexity.

**Done when:** Opening the app displays match results and responds to team, date, and stage filters.

### Step 6: Add container and documentation support
**Goal:** Make the project easy to run with Maven or Docker Compose and document the setup clearly.
**Components:**
- docker-compose.yml
- README.md
- world-cup-match-tracker-service/src/main/resources/application.properties

**Changes:**
- Add a Docker Compose definition that builds and runs the backend service.
- Update the README with prerequisites, Maven startup steps, Docker Compose startup steps, example API calls, and a note that sample data is seeded automatically.
- Document the main API endpoints and their expected request/response shape.

**Done when:** The README contains setup instructions for both Maven and Docker Compose.

### Step 7: Finalize the build configuration and package the application
**Goal:** Ensure the project can be built and launched cleanly from the repository root.
**Components:**
- world-cup-match-tracker-service/pom.xml
- world-cup-match-tracker-service/src/main/java/com/addario/worldcupmatchtracker/WorldcupmatchtrackerApplication.java

**Draft code:**
```xml
<repositories>
    <repository>
        <id>central</id>
        <name>Maven Central Repository</name>
        <url>https://repo.maven.apache.org/maven2</url>
    </repository>
    <repository>
        <id>spring-releases</id>
        <name>Spring Releases</name>
        <url>https://repo.spring.io/release</url>
    </repository>
</repositories>
```

**Changes:**
- Add the required Spring Boot dependencies for web, validation, JPA, and H2.
- Ensure the Maven configuration includes resolvable repositories for the chosen dependencies.
- Ensure the application entry point is wired correctly for local execution.
- Keep the code layout consistent with the existing package structure.

**Done when:** `mvn clean package` succeeds and the backend can be started with Maven or Docker Compose.

## Constraints

- Do not introduce a second database technology; use H2 for persistence.
- Do not add a separate authentication or user-management subsystem.
- Keep the UI lightweight and static rather than building a full SPA framework.
- Keep implementation changes confined to the service and app modules unless documentation or container files require updates.
