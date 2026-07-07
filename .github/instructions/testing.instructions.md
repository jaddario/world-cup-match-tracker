---
applyTo: "**/*Test.java"
---

# Unit Testing Instructions

These instructions define the unit testing standards and best practices for the platform-updater project. All developers must follow these guidelines when writing unit tests to ensure code quality, maintainability, and consistency across the codebase.

## 1. Naming Convention

Use the `shouldExpectedResult_whenScenario` convention for naming test methods. This approach enhances readability and clearly conveys the purpose of the test using the Given-When-Then pattern.

**Good Examples:**

```java
@Test
@DisplayName("Should update platform configuration when valid request is provided")
void shouldUpdatePlatformConfiguration_whenValidRequestIsProvided() {
    // test implementation
}

@Test
@DisplayName("Should throw validation exception when platform name is null")
void shouldThrowValidationException_whenPlatformNameIsNull() {
    // test implementation
}

@Test
@DisplayName("Should return updated platform with correct version when update is successful")
void shouldReturnUpdatedPlatformWithCorrectVersion_whenUpdateIsSuccessful() {
    // test implementation
}
```

**Poor Examples:**

```java
public void testValidRequest() {
    // test implementation
}

public void testUpdatePlatform() {
    // test implementation
}
```

**Convention Breakdown:**

- **should**: Indicates the expected behavior
- **ExpectedResult**: The outcome expected (e.g., UpdatePlatformConfiguration, ThrowValidationException)
- **when**: Separator keyword
- **Scenario**: Specific condition being validated (e.g., ValidRequestIsProvided, PlatformNameIsNull)

## 2. FIRST Principles Compliance

Unit tests must adhere to the FIRST principles to ensure they are efficient and maintainable:

- **Fast**: Tests should execute quickly to encourage frequent runs. Avoid external dependencies like databases or
  network calls in unit tests
- **Independent**: Tests should not depend on each other. A failure in one test should not cause other tests to fail
- **Repeatable**: Tests should yield consistent results regardless of execution frequency or environment
- **Self-Validating**: Tests should include clear assertions that indicate pass or fail without requiring manual
  inspection
- **Timely**: Write tests during or before the implementation of the corresponding code to facilitate test-driven
  development


## 3. Test One Unit at a Time
Each test case should validate only the behavior of the unit under test:

- **Isolate the Unit**: Use mocking or stubbing to isolate the unit being tested from its dependencies
- **Avoid Integration Testing**: Integration tests should be handled separately
- **One Use Case Per Test**: Each test should validate a single scenario

**Good Example:**

```java
@Test
@DisplayName("Should create platform update request when all fields are valid")
void shouldCreatePlatformUpdateRequest_whenAllFieldsAreValid() {
    // Given
    var platformId = UUID.randomUUID();
    var version = "1.2.3";
    var updateBy = "admin@example.com";

    // When
    var request = PlatformUpdateRequest.builder()
            .platformId(platformId)
            .version(version)
            .updatedBy(updateBy)
            .build();

    // Then
    assertAll(
            () -> assertThat(request).isNotNull(),
            () -> assertThat(request.getPlatformId()).isEqualTo(platformId),
            () -> assertThat(request.getVersion()).isEqualTo(version),
            () -> assertThat(request.getUpdatedBy()).isEqualTo(updateBy)
    );
}

@Test
@DisplayName("Should update platform successfully when platform exists")
void shouldUpdatePlatformSuccessfully_whenPlatformExists() {
    // Given
    var platformId = UUID.randomUUID();
    var updateRequest = createValidUpdateRequest(platformId);
    var existingPlatform = createExistingPlatform(platformId);
    
    when(platformRepository.findById(platformId)).thenReturn(Optional.of(existingPlatform));
    when(platformRepository.save(any(Platform.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    var result = platformUpdateService.update(updateRequest);

    // Then
    assertAll(
            () -> assertThat(result).isNotNull(),
            () -> assertThat(result.getPlatformId()).isEqualTo(platformId),
            () -> assertThat(result.getVersion()).isEqualTo(updateRequest.getVersion()),
            () -> verify(platformRepository).save(any(Platform.class))
    );
}
```

**Poor Example:**

```java
void testPlatformUpdate() {
    var platformId = UUID.randomUUID();
    var request = createUpdateRequest(platformId);
    var result = service.update(request);
    assertEquals("1.2.3", result.getVersion());

    // Testing multiple things in one test
    var anotherRequest = createUpdateRequest(UUID.randomUUID());
    var anotherResult = service.update(anotherRequest);
    assertEquals("1.2.4", anotherResult.getVersion());
}
```

## 4. Parameterized Tests

Use parameterized tests when testing the same logic with different inputs to avoid redundant test code.

**Guidelines:**

- Test a single functionality with different inputs
- Avoid overcomplicating by parameterizing too many things at once
- Include boundary conditions and edge cases
- Don't overuse parameterized tests - use them where they add value
- Use `@ParameterizedTest` with appropriate sources (`@ValueSource`, `@MethodSource`, `@CsvSource`)

**Example:**

```java
@ParameterizedTest
@ValueSource(strings = {"1.0.0", "2.1.3", "10.15.20"})
@DisplayName("Should validate version format when version follows semantic versioning")
void shouldValidateVersionFormat_whenVersionFollowsSemanticVersioning(String version) {
    // Given
    var validator = new VersionValidator();

    // When
    var isValid = validator.isValid(version);

    // Then
    assertThat(isValid).isTrue();
}

@ParameterizedTest
@MethodSource("providePlatformUpdateScenarios")
@DisplayName("Should handle different platform update scenarios when updates are requested")
void shouldHandleDifferentPlatformUpdateScenarios_whenUpdatesAreRequested(
        String platformName,
        String version,
        UpdateStatus expectedStatus) {
    // Given
    var request = PlatformUpdateRequest.builder()
            .platformName(platformName)
            .version(version)
            .build();
    
    when(platformRepository.findByName(platformName)).thenReturn(Optional.of(createPlatform(platformName)));

    // When
    var result = platformUpdateService.update(request);

    // Then
    assertAll(
            () -> assertThat(result).isNotNull(),
            () -> assertThat(result.getStatus()).isEqualTo(expectedStatus),
            () -> verify(platformRepository).save(any(Platform.class))
    );
}

private static Stream<Arguments> providePlatformUpdateScenarios() {
    return Stream.of(
            Arguments.of("Platform-A", "1.0.0", UpdateStatus.SUCCESS),
            Arguments.of("Platform-B", "2.1.0", UpdateStatus.SUCCESS),
            Arguments.of("Platform-C", "3.0.0-beta", UpdateStatus.PENDING)
    );
}

@ParameterizedTest
@CsvSource({
        "1.0.0, 2.0.0, true",
        "2.0.0, 1.0.0, false",
        "1.0.0, 1.0.0, false"
})
@DisplayName("Should compare versions correctly when comparing two semantic versions")
void shouldCompareVersionsCorrectly_whenComparingTwoSemanticVersions(
        String version1,
        String version2,
        boolean expected) {
    // Given
    var comparator = new VersionComparator();

    // When
    var result = comparator.isNewer(version1, version2);

    // Then
    assertThat(result).isEqualTo(expected);
}
```

## 5. Additional Best Practices

**AAA Pattern (Given-When-Then)**

Follow the Given-When-Then pattern consistently across all tests:

- Given (Arrange): Set up the test data and conditions
- When (Act): Execute the method being tested
- Then (Assert): Verify the expected outcome

### Annotations

Always use `@Test` (or `@ParameterizedTest`) and `@DisplayName` annotations for clarity and better reporting:

```java
@Test
@DisplayName("Should persist new platform when platform does not exist")
void shouldPersistNewPlatform_whenPlatformDoesNotExist() {
    // test implementation
}
```

### Test Class Structure

Use `@DisplayName` at the class level and organize tests with proper documentation:

```java
/**
 * Unit tests for PlatformUpdateService.
 * Tests the business logic for updating platform configurations.
 */
@DisplayName("PlatformUpdateService Unit Tests")
@ExtendWith(MockitoExtension.class)
class PlatformUpdateServiceTest {
    // test methods
}
```

### Assertions

Use `assertAll()` for multiple related assertions and prefer fluent assertion syntax using AssertJ for better readability:

```java
@Test
@DisplayName("Should update platform with all properties when update is successful")
void shouldUpdatePlatformWithAllProperties_whenUpdateIsSuccessful() {
    // Given
    var platformId = UUID.randomUUID();
    var updateRequest = createValidUpdateRequest(platformId);
    var existingPlatform = createExistingPlatform(platformId);
    
    when(platformRepository.findById(platformId)).thenReturn(Optional.of(existingPlatform));
    when(platformRepository.save(any(Platform.class))).thenReturn(existingPlatform);

    // When
    var result = platformUpdateService.update(updateRequest);

    // Then
    assertAll(
            () -> assertThat(result).isNotNull(),
            () -> assertThat(result.getPlatformId()).isEqualTo(platformId),
            () -> assertThat(result.getVersion()).isEqualTo(updateRequest.getVersion()),
            () -> assertThat(result.getUpdatedBy()).isEqualTo(updateRequest.getUpdatedBy()),
            () -> assertThat(result.getUpdatedAt()).isNotNull()
    );
}
```

### What to Test

- **Test Behavior, Not Implementation**: Test the observable output or behavior
- **Don't test internal fields or private methods**
- **Focus on public API and contracts**

### Test Data Generation

Create helper methods or test data builders for consistent test data generation:

**Good Examples:**

```java
// Helper methods for test data
private PlatformUpdateRequest createValidUpdateRequest(UUID platformId) {
    return PlatformUpdateRequest.builder()
            .platformId(platformId)
            .version("1.0.0")
            .updatedBy("test@example.com")
            .build();
}

private Platform createExistingPlatform(UUID platformId) {
    return Platform.builder()
            .id(platformId)
            .name("Test Platform")
            .version("0.9.0")
            .build();
}

// Test data builder class
class PlatformTestDataBuilder {
    private UUID id = UUID.randomUUID();
    private String name = "Default Platform";
    private String version = "1.0.0";

    public PlatformTestDataBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public PlatformTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PlatformTestDataBuilder withVersion(String version) {
        this.version = version;
        return this;
    }

    public Platform build() {
        return Platform.builder()
                .id(id)
                .name(name)
                .version(version)
                .build();
    }
}
```

**Benefits of Using Test Data Builders:**

- Consistent test data across the project
- Reduces setup complexity
- Easy to create variations for different test scenarios
- Improves test readability

### Mocking Patterns

Use Mockito annotations for clean dependency mocking:

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("PlatformUpdateService Unit Tests")
class PlatformUpdateServiceTest {

    @Mock
    private PlatformRepository platformRepository;

    @Mock
    private VersionValidator versionValidator;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PlatformUpdateService platformUpdateService;

    private UUID testPlatformId;
    private PlatformUpdateRequest validRequest;
    private Platform existingPlatform;

    @BeforeEach
    void setUp() {
        testPlatformId = UUID.randomUUID();
        validRequest = createValidUpdateRequest(testPlatformId);
        existingPlatform = createExistingPlatform(testPlatformId);
    }

    @Test
    @DisplayName("Should call repository save with correct data when update is valid")
    void shouldCallRepositorySaveWithCorrectData_whenUpdateIsValid() {
        // Given
        when(platformRepository.findById(testPlatformId)).thenReturn(Optional.of(existingPlatform));
        when(versionValidator.isValid(anyString())).thenReturn(true);

        // When
        platformUpdateService.update(validRequest);

        // Then
        verify(platformRepository).save(argThat(platform ->
                platform.getId().equals(testPlatformId) &&
                platform.getVersion().equals(validRequest.getVersion())
        ));
    }
}
```

### Exception Testing

Test exception scenarios using AssertJ's `assertThatThrownBy`:

```java
@Test
@DisplayName("Should throw IllegalArgumentException when platform ID is null")
void shouldThrowIllegalArgumentException_whenPlatformIdIsNull() {
    // Given
    var invalidRequest = PlatformUpdateRequest.builder()
            .platformId(null)
            .version("1.0.0")
            .build();

    // When & Then
    assertThatThrownBy(() -> platformUpdateService.update(invalidRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Platform ID cannot be null");
}

@Test
@DisplayName("Should throw PlatformNotFoundException when platform does not exist")
void shouldThrowPlatformNotFoundException_whenPlatformDoesNotExist() {
    // Given
    var nonExistentId = UUID.randomUUID();
    var request = createValidUpdateRequest(nonExistentId);
    when(platformRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> platformUpdateService.update(request))
            .isInstanceOf(PlatformNotFoundException.class)
            .hasMessage("Platform not found with ID: " + nonExistentId);
}
```

## 7. Code Quality Commitment

> "You should write unit tests as you write production code."

Keeping unit tests clean, well-organized, and well-maintained is essential to:

- Reduce long-term maintenance efforts
- Ensure the code remains easy to understand and modify
- Maintain high code quality standards
- Enable confident refactoring and changes

## 8. Tools and Dependencies

The project uses:

- **JUnit 5** for test framework
- **AssertJ** for fluent assertions (`assertThat()` syntax)
- **Mockito** for mocking dependencies (via `quarkus-junit5-mockito`)
- **Quarkus Test Framework** for integration testing
- **Maven Surefire Plugin** for test execution

## 9. Test Coverage Guidelines

* **Coverage Targets - Domain and Application (Service / Use Case):**
    * **Target:** 90%+ test coverage
    * **Modules:**
        * `core` (domain utilities, aggregators, processors)
        * `iam` (users, clients, delegations, subscriptions)
        * `issuance` (instrument domain and services)
    * **Focus Areas:**
        * Pure business rules and invariants
        * Validation logic and edge-case handling
        * Use cases and orchestration logic
        * Repository-independent services
    * **Rationale:**
        * These layers encapsulate the core business behavior and decision-making logic.
        * High coverage is required to prevent regressions, ensure correctness, and maintain confidence during refactoring.
        * Thorough testing here reduces risk and increases system stability.
* **Test Scenarios:**
    * Write tests for both the **happy path** (expected successful execution) and **edge cases**.
    * **Edge cases** include: invalid inputs, empty collections, boundary conditions, error conditions, null values, concurrency issues (where applicable).
* **Validation & Branches:**
    * **Validate all validations:** Ensure that input validation logic is thoroughly tested, confirming correct error responses for invalid data.
    * **Test all branches and decision points:** Use test cases to cover every `if/else`, `switch` case, and loop condition.
* **Exclusions:**
    * **Do not test simple POJOs** (Plain Old Java Objects) like DTOs, records, or entities that only contain getters/setters and have no custom logic. Their functionality is often guaranteed by the language or frameworks.
    * **Do not test generated code** (e.g., Lombok-generated methods, framework proxies).
* **Mocking External Services:**
    * For unit tests, **mock all external dependencies and services** (e.g., REST clients, database repositories, message queues, file systems). Use mocking frameworks like Mockito.
    * This ensures that unit tests are fast, isolated, and deterministic. Integration tests can then verify the interaction with real external services.

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=PlatformUpdateServiceTest

# Run tests with coverage
mvn clean test jacoco:report
```
Or use the VS Code tasks:

- "Run Tests" - runs all tests
- "Build Project" - includes test execution