---
mode: agent
description: Generate an implementation plan for new features or refactoring
model: Claude Sonnet 4
tools: ['search', 'think', 'edit']
---

Read the prompt file and create an implementation plan with the prompt filename changing the "prompt" to "plan".

- This plan will be used as a LLM prompt, use imperative language and be pragmatic.
- Identify Components: Determine which components need to be created/modified
- Don't:
    - Create any testing plan
    - Split the plan in time slices
    - Create any summary or benefits session
    - Use any existing other implementation plan as reference
    - Create temporary files
- Create a draft code for each component implementation/change
    - Follow the [Coding Guidelines](../instructions/codingguidelines.md)
    - For existing components, just write what will be changed
- Segregate this plan in sequential steps
    - Don't create empty steps
    - Follow the structure below: