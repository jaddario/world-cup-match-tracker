---
description: 'Guidelines for building Java base applications'
applyTo: '**/*.java'
---

# Java Base Application Standards

## General Instructions

- First, prompt the user if they want to integrate static analysis tools (SonarQube, PMD, Checkstyle)
  into their project setup. If yes, provide guidance on tool selection and configuration.
- If the user declines static analysis tools or wants to proceed without them, continue with implementing the Best practices, bug patterns and code smell prevention guidelines outlined below.
- Address code smells proactively during development rather than accumulating technical debt.
- Focus on readability, maintainability, and performance when refactoring identified issues.
- Use IDE / Code editor reported warnings and suggestions to catch common patterns early in development.

## Best practices

- **Records**: For classes primarily intended to store data (e.g., DTOs, immutable data structures), **Java Records should be used instead of traditional classes**.
- **Pattern Matching**: Utilize pattern matching for `instanceof` and `switch` expression to simplify conditional logic and type casting.
- **Type Inference**: Use `var` for local variable declarations to improve readability, but only when the type is explicitly clear from the right-hand side of the expression.
- **Immutability**: Favor immutable objects. Make classes and fields `final` where possible. Use collections from `List.of()`/`Map.of()` for fixed data. Use `Stream.toList()` to create immutable lists.
- **Streams and Lambdas**: Use the Streams API and lambda expressions for collection processing. Employ method references (e.g., `stream.map(Foo::toBar)`).
- **Null Handling**: Avoid returning or accepting `null`. Use `Optional<T>` for possibly-absent values and `Objects` utility methods like `equals()` and `requireNonNull()`.

### Naming Conventions

- Follow Google's Java style guide:
    - `UpperCamelCase` for class and interface names.
    - `lowerCamelCase` for method and variable names.
    - `UPPER_SNAKE_CASE` for constants.
    - `lowercase` for package names.
- Use nouns for classes (`UserService`) and verbs for methods (`getUserById`).
- Avoid abbreviations and Hungarian notation.

### General Coding Standards

Our foundational principles for writing clean, modern, and efficient Java code.

* **Leverage Java 21 Features:**

    * **Sealed Types:** Employ `sealed` classes and interfaces with `permits` for controlled inheritance hierarchies, ensuring all possible subtypes are known and handled. Use `non-sealed` where appropriate to allow further extension.
        ```java
        public sealed interface Shape permits Circle, Rectangle {}
        public final class Circle implements Shape {}
        public non-sealed class Rectangle implements Shape {} // Allows further extension
        ```
    * **Switch Expressions with Pattern Matching:** Use `switch` expressions for concise, exhaustive handling of multiple cases, especially with pattern matching.
        ```java
        double area = switch (shape) {
            case Circle c    -> Math.PI * c.radius() * c.radius();
            case Rectangle r -> r.length() * r.width();
            default          -> throw new IllegalArgumentException("Unknown shape");
        };
        ```
    * **Pattern Matching for `instanceof`:** Utilize `if (obj instanceof MyType t)` for type checking and casting in a single, safe step.
        ```java
        if (event instanceof UserCreatedEvent userEvent) {
            // 'userEvent' is automatically cast and available here
            log.info("User created: {}", userEvent.userId());
        }
        ```

* **Handling Optionality:**
    * Avoid using `Optional.get()` without checking for presence (`Optional.isPresent()` or `Optional.orElseThrow()`).
    * Prefer methods like `orElse()`, `orElseGet()`, `map()`, `flatMap()`, `filter()`, and `ifPresent()` for processing `Optional` values.

* **Service Design (DI-enabled):**
    * **Avoid static utility classes** with many static methods. Prefer creating DI-enabled services that can be injected, allowing for easier testing, mockability, and adherence to SOLID principles.

* **SOLID Principles:**
    * Continuously apply **SOLID principles** (Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion) to design robust and maintainable code.

* **Stream Operations:**
    * Prefer **Stream API operations** over traditional manual loops for collection processing. This leads to more concise, readable, and often more efficient code.
    * **Use `Collectors`:** Leverage `Collectors.groupingBy`, `mapping`, `joining`, `reducing`, `partitioningBy` for complex aggregation and transformation.
    * **`flatMap`:** Use `flatMap` to flatten nested collections or streams into a single stream.
    * **Avoid in-place mutation:** Do not mutate collections within stream pipelines; instead, use `map()`, `filter()`, and `collect()` to produce new collections.

* **Control Flow Simplification:**
    * Replace complex `if-else` chains with **early returns and guard clauses** to improve readability and reduce nesting.
    ```java
    // Bad
    if (isValid) {
        if (isAuthenticated) {
            // ... logic ...
        } else {
            // handle not authenticated
        }
    } else {
        // handle not valid
    }

    // Good
    if (!isValid) {
        // handle not valid, return
    }
    if (!isAuthenticated) {
        // handle not authenticated, return
    }
    // ... main logic ...
    ```

* **Class Cohesion:**
    * **Avoid "God Classes"** (large, monolithic classes that do too many things).
    * Each class should adhere to the **Single Responsibility Principle**, doing one thing well.

## Logging & Observability

Guidelines for effective logging and system observability using **SLF4J** with **Lombok `@Slf4j`**.

* **Logging Framework:**

    * Use **SLF4J** as the logging facade.
    * Leverage **Lombok `@Slf4j`** to inject the logger:
    ```java
    @Slf4j
    public class ExampleService {
        // log.info(...)
    }
    ```
    * Avoid manual logger instantiation (LoggerFactory.getLogger(...)).
    * Use a single logging backend configured by Quarkus (e.g., JBoss Logging / Logback).

* **Log Levels:**

    * Use **appropriate log levels**:
        * `INFO`: For significant business events, major state changes, or application startup/shutdown.
        * `WARN`: For recoverable issues, potential problems, or situations that don't immediately cause failure but warrant attention.
        * `ERROR`: For unexpected failures, exceptions, or critical issues that prevent normal operation.
        * `DEBUG`/`TRACE`: For detailed technical information during development or troubleshooting.

* **Structured Logging:**
    * Avoid direct string concatenation in log messages:
        ```java
        // Bad
        log.info("User " + userId + " logged in.");
        // Good (using parameterized logging)
        log.info("User {} logged in.", userId);
        ```
* **Sensitive Data:**
    * **Never log sensitive data** such as passwords, authentication tokens, personally identifiable information (PII), or financial details. Ensure sensitive fields are properly masked or redacted before logging.

* **Exception Logging:**
    * Always log exceptions with the throwable as the last parameter.
      ```java
      try {
          // code that may throw
      } catch (Exception e) {
          log.error("An error occurred while processing request for user {}", userId, e);
      }
      ```
    * Avoid logging stack traces at multiple layers.

* **General Rules:**
    * Logs should explain **what happened** and **why**, not restate code.
    * Avoid excessive logging inside loops or hot paths.
    * Do not use logs as a control-flow mechanism.
    * Ensure logs add diagnostic or business value.

### Bug Patterns

| Rule ID | Description                                                 | Example / Notes                                                                                  |
| ------- | ----------------------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| `S2095` | Resources should be closed                                  | Use try-with-resources when working with streams, files, sockets, etc.                           |
| `S1698` | Objects should be compared with `.equals()` instead of `==` | Especially important for Strings and boxed primitives.                                           |
| `S1905` | Redundant casts should be removed                           | Clean up unnecessary or unsafe casts.                                                            |
| `S3518` | Conditions should not always evaluate to true or false      | Watch for infinite loops or if-conditions that never change.                                     |
| `S108`  | Unreachable code should be removed                          | Code after `return`, `throw`, etc., must be cleaned up.                                          |

## Code Smells

| Rule ID | Description                                            | Example / Notes                                                               |
| ------- | ------------------------------------------------------ | ----------------------------------------------------------------------------- |
| `S107`  | Methods should not have too many parameters            | Refactor into helper classes or use builder pattern.                          |
| `S121`  | Duplicated blocks of code should be removed            | Consolidate logic into shared methods.                                        |
| `S138`  | Methods should not be too long                         | Break complex logic into smaller, testable units.                             |
| `S3776` | Cognitive complexity should be reduced                 | Simplify nested logic, extract methods, avoid deep `if` trees.                |
| `S1192` | String literals should not be duplicated               | Replace with constants or enums.                                              |
| `S1854` | Unused assignments should be removed                   | Avoid dead variables—remove or refactor.                                      |
| `S109`  | Magic numbers should be replaced with constants        | Improves readability and maintainability.                                     |
| `S1188` | Catch blocks should not be empty                       | Always log or handle exceptions meaningfully.                                 |

## Build and Verification

- After adding or modifying code, verify the project continues to build successfully.
- If the project uses Maven, run `mvn clean install`.
- Ensure all tests pass as part of the build.