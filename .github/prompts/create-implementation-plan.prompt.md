---
agent: 'agent'
description: 'Generate a structured implementation plan for a feature or task, following project standards.'
model: Claude Sonnet 4.5 (copilot)
tools: ['search/codebase', 'edit/editFiles', 'read/problems', 'edit/createFile', 'edit/createDirectory']
---

Generate an implementation plan for the task described by the user.

## Before writing the plan

1. Read the relevant source files using `codebase` — only files directly related to the task scope.
2. Identify the layer (domain, application, infrastructure, API) and which modules are affected.
3. Do not read files unrelated to the task.

## Plan structure

Produce a markdown file at `docs/implementation-plans/<kebab-case-task-name>-plan.md` with the following sections:

### Header block

```
# <Task title>

**Branch:** feature/<kebab-case-name>
**Scope:** <one sentence: what changes and why>
**Affected modules:** <comma-separated list>
**Estimated steps:** <N>
```

### Description

Two to three sentences explaining what the task solves and what approach will be taken.
No implementation detail here — just business context and rationale.

### Mermaid diagram

A `flowchart TD` diagram showing the workflow of the implementation steps.
Use the following node style conventions:
- Steps: rectangular nodes
- Decision points: diamond nodes `{}`
- Review gates: stadium nodes `([Review])`
- Terminal states: double-border nodes `[[Done]]`

Keep it to the actual implementation steps — not an architecture diagram.

### Steps

Each step follows this exact format:

```markdown
### Step N: <short title>

**Goal:** <one sentence>
**Layer:** domain | application | infrastructure | api
**Files:**
- `path/to/FileA.java` — <what changes in this file>
- `path/to/FileB.java` — <what changes in this file>
**Changes:**
- <specific change bullet>
**Done when:** <observable, verifiable condition — compiles, test passes, endpoint returns X>
**Tests:** yes | no — <if yes: what to test>
```

No step should change more than 3-4 files.
No step should span more than one architectural layer.
Steps must be ordered so each one compiles independently.

### Constraints

Explicit list of what must NOT happen:
- No new dependencies unless listed here
- No schema changes unless listed here
- No changes outside the affected modules

### Test plan (final step)

Always add a dedicated test step as the last step.
List each test class, the scenarios to cover, and the coverage target per the project standard (90%+ for domain/application layers).

## Standards reference

At the end of the generated plan, add this section verbatim — do not duplicate the rules inline:

```markdown
## Standards

All steps are implemented under:

- `.github/instructions/coddingguidelines.md`
- `.github/instructions/project-standards.instructions.md`
- `.github/instructions/testing.instructions.md` (test step only)
```

## Output

- Write the plan file using `editFiles`.
- After writing, print a one-paragraph summary in chat: what the plan covers, how many steps, and which step contains the tests.
- Do not implement anything. Stop after writing the plan.