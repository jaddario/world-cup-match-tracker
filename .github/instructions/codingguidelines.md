---
applyTo: '**/*.java'
---

# Java Coding Guidelines

## Language & Syntax

- Use Java 21 features and syntax.
- Use `var` for local variable declarations.
- Avoid using full package names in the code, use imports instead.
- Always keep a blank line before and after methods, if statements, loops, and try/catch blocks.
- Use private and package-private visibility whenever possible. Only use public and protected when really necessary.

## Dependency Injection

- Don't use `@Inject` to inject class dependencies, use constructor injection instead.

## Lombok

- Use Lombok annotations to reduce boilerplate code.
- Use `@SneakyThrows` only when it's necessary to avoid checked exceptions.

## Exception Handling

- Don't create try/catch blocks for Exception handling, unless it's explicitly mentioned in the implementation plan or instructions.

## Validation

- For field validations and entity existence checks, use `ValidationException` and its subclasses with `OperationError` objects with appropriate error messages with values placeholders.

## Checkstyle Rules

The project enforces checkstyle rules defined in `checkstyle.xml` at the repository root. All generated code must comply with these rules:

- Java files must not exceed **1000 lines**.
- Lines must not exceed **125 characters** (excluding `package`, `import` statements and URLs).
- Do not leave **unused imports** in the code.
- Do not leave **unused local variables** in the code.
- Do not use **fully qualified class names** (e.g. `com.euronext.securities.{project-name}.infrastructure...`) inline in the code. Always use import statements instead.
- No whitespace **after** unary operators: `~`, `--`, `.`, `++`, `!`, unary `-`, unary `+`.
- No whitespace **before**: `,`, `--` (postfix), `++` (postfix), `;`.