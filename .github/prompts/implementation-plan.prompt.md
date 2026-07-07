---
agent: 'agent'
description: 'Execute a structured implementation plan step by step, with review gates between each step.'
model: Claude Sonnet 4.5 (copilot)
tools: ['search/changes', 'search/codebase', 'edit/editFiles', 'read/problems', 'runCommands']
---

Execute the implementation plan referenced by the user.

## Standards

Apply these instruction files on every step — treat them as always active:

- `.github/instructions/coddingguidelines.md` see [instructions/coddingguidelines.md](.github/instructions/coddingguidelines.md) — Java coding standards
- `.github/instructions/project-standards.instructions.md` see [instructions/project-standards.instructions.md](.github/instructions/project-standards.instructions.md) — architecture and project conventions
- `.github/instructions/testing.instructions.md` — see [instructions/testing.instructions.md](.github/instructions/testing.instructions.md) - unit test standards (applies to the test step only)

## Before starting

1. Read the plan file in full.
2. Read only the files listed under **Step 1** — nothing else.
3. State the step goal and "Done when" condition before writing any code.

## Execution loop

For each step:

1. Implement only what the step defines. No refactors, no additions outside the file list.
2. After completing the step, output this block and stop:

```
### Step N complete

**Changed:** FileA.java, FileB.java
**Done when met:** yes | no — <one sentence>
**Assumptions:** <list or "none">
**Waiting for:** approval to continue
```

3. Do not proceed until the user replies `ok`, `next`, or `continue`.

## Deviation handling

- Files not listed in the step → flag, do not change them.
- Ambiguous step → ask one question, stop.
- Build broken by the step → fix only what the step introduced.

## What NOT to do

- Do not combine steps.
- Do not add logging unless the step requires it.
- Do not add comments restating the plan.
- Do not invoke `java-docs` or `refactoring-extract-method` prompts unless the user asks.
- Do not declare new dependencies not listed in the plan constraints.