---
applyTo: '*'
description: 'Quarkus development standards and instructions'
---

# Quarkus Application Standards (Java 21+)

This document defines standards and guidelines for building **high-quality, maintainable, secure, and performant Quarkus applications** using **Java 21+**.

These rules are intended to guide both developers and AI code assistants (e.g. Copilot).

---

## 1. Project Overview

- **Java:** 21 or later
- **Framework:** Quarkus 3.x (latest stable)
- **Build Tool:** Maven
- **Architecture:** Clean Architecture
- **Style Goals:** readability, explicitness, testability, security

---

## 2. Project Structure

### Multi-module Maven Project

Root project with the following modules (each with its own `pom.xml`):

- **core**
    - Aggregators
    - Processors
    - Routes
    - Shared utilities
    - Domain abstractions

- **iam**
    - Users
    - Clients
    - Delegations
    - Subscriptions

- **issuance**
    - Instrument issuance logic
    - Legacy to Instrument logic

- **startup**
    - Application bootstrap
    - Configuration
    - `application.yml`

---

## 3. Clean Architecture Rules

- Enforce clear module boundaries:
    - No circular dependencies
    - Dependencies must flow inward

- Use DTOs at REST boundaries:
    - Never expose entities directly

- Prefer immutability:
    - Use `record` for simple data carriers
    - Avoid setters where possible

---

## 4. Coding Standards

- Use **Javadoc** for public classes and methods
- Follow consistent Java formatting (Google Java Style or similar)
- Prefer Java 17+ features when appropriate:
    - Records
    - Sealed classes
    - Pattern matching
    - Text blocks

- Prefer **constructor injection**
- Avoid field injection (especially in tests)
- Handle exceptions explicitly
- Never swallow exceptions
- Validate inputs and **fail fast**

---

## 5. Naming Conventions

- **Classes:** `PascalCase`
    - Example: `UserService`, `ClientResource`

- **Methods / Variables:** `camelCase`
    - Example: `findUserById`, `isActive`

- **Constants:** `ALL_CAPS`
    - Example: `DEFAULT_PAGE_SIZE`

- **Packages:** by domain
    - Example: `users`, `clients`, `subscriptions`, `delegations`

---

# 6. Quarkus Patterns

- Use `@ApplicationScoped` for singletons; avoid `@Singleton`.
- Use `@Inject` for DI; prefer constructor injection for testability.
- REST: `@Path`, `@GET`, `@POST`, `@PUT`, `@DELETE`, `@Consumes(MediaType.APPLICATION_JSON)`, `@Produces(MediaType.APPLICATION_JSON)`.
- Status codes: `200`, `201`, `204`, `400`, `404`, `409`, `500`.
- Data Access: Panache entities/repositories, `@Transactional` on writes, pagination, named queries, avoid N+1 with fetch joins.
- Configuration: `@ConfigProperty` or SmallRye config mapping; environment variables for secrets.
- Use `@RequestScoped` for request lifecycle beans.
- Scope beans correctly to avoid memory/performance issues.

## 7. Quarkus Guidelines

- Use **Dev Mode** for development:
  ```bash
  mvn quarkus:dev


## 📚 Reference Links

### Official Documentation
- [Quarkus Documentation](https://quarkus.io/guides/)
- [Quarkus Reference Guide](https://quarkus.io/guides/all-config)
- [MicroProfile Specifications](https://microprofile.io/specifications/)
- [Jakarta EE Documentation](https://jakarta.ee/specifications/)

### Essential Extensions & Tools
- [Quarkus Extensions](https://quarkus.io/extensions/)
- [Panache ORM Guide](https://quarkus.io/guides/hibernate-orm-panache)
- [RESTEasy Reactive](https://quarkus.io/guides/resteasy-reactive)
- [SmallRye Config](https://quarkus.io/guides/config-reference)

## Quarkus Anti-Patterns
- Don't use field injection in tests—use constructor injection for better testability
- Don't hardcode configuration values—use Quarkus configuration system
- Don't ignore exceptions—implement proper error handling with Quarkus patterns
- Avoid traditional JPA patterns when Panache provides simpler alternatives
- Don't skip native compilation testing for production deployments