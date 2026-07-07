# Commit Message Guidelines

Clear and consistent commit messages improve project history readability
and help tools like GitHub Copilot generate better suggestions.

All commits must follow the **Conventional Commits** format.

## Format

Follow the **Conventional Commits** style:

    <type>: <short imperative description>

    <body (optional)>
    Refs: #123 | Fixes: #123 (optional)

### Rules


- Use lowercase for the **type** (`feat`, not `Feat`).
- Use the **imperative mood** (“add” not “added” or “adds”).
- Limit the **subject line to 50 characters**.
- Leave a **blank line** between the subject and body.
- Wrap the **body at 72 characters** per line.
- Use **bullet points** for multiple changes.
- Reference issues using `Refs:` or `Fixes:` where applicable.


## Commit Types

| Type | Description |
|------|-------------|
| **feat** | Add a new feature or functionality |
| **fix** | Fix a bug or defect |
| **docs** | Documentation-only changes |
| **style** | Code formatting or style changes (no logic impact) |
| **refactor** | Code restructuring or cleanup without behavior change |
| **test** | Add or modify tests |
| **chore** | Routine maintenance or minor build/config changes |
| **build** | Changes affecting the build system or external dependencies (e.g., Maven, npm, CI tools) |

## Examples

### New Feature

```text
feat: add user authentication endpoint

- Implement JWT token generation
- Add password hashing and validation middleware
Refs: #123
```

### Bug Fix

```text
fix: resolve null pointer in user service

Handle case where user profile is undefined
Fixes: #456
```

### Refactor

```text
refactor: simplify order processing logic

- Extract payment handling into separate service
- Remove duplicate validation
- Improve error handling and logging
```

### Documentation

```text
docs: update README with setup instructions
```

------------------------------------------------------------------------

## Copilot Hints

Use these patterns as prompts when writing commit messages to help GitHub Copilot generate better suggestions:


-   `feat:` → new feature summary
-   `fix:` → concise bug fix description
-   `refactor:` → cleanup or logic improvement
-   `docs:` → documentation update
-   `chore:` → maintenance or cleanup
-   `build:` → dependency or build-related change


## Best practices
-   Keep commits **atomic**: one logical change per commit
-   Use the body to explain **why** the change was made, not only what
    changed
-   Avoid vague messages like `update` or `fix stuff`
-   Always link related issues using `Fixes: #id` or `Refs: #id` when
    applicable