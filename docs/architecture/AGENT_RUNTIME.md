# Tyson Agent Runtime

## Purpose

Tyson is being evolved from the Kai-derived application foundation into an agent runtime. The first architectural boundary is the execution kernel in `com.inspiredandroid.kai.agent`.

The kernel deliberately separates:

1. **Planning** — an LLM/model adapter proposes a bounded plan.
2. **Policy** — deterministic authorization decides whether each action is allowed.
3. **Execution** — native tools, MCP tools and skills execute through one gateway.
4. **Observation** — execution returns structured results.
5. **Verification** — success is independently established instead of inferred from tool completion.
6. **Recovery** — failed verification can retry within a bounded attempt budget.
7. **Events** — every lifecycle transition can be persisted for UI, audit and recovery.

## Control flow

```text
Goal
  -> Planner
  -> AgentPlan
  -> Policy
  -> Tool Gateway
  -> Observation
  -> Verifier
      | success -> next step
      | failure -> Recovery -> retry
      | exhausted -> Failed
  -> Completed
```

The model is **not** the authorization boundary. A future policy engine should inspect tool capabilities, filesystem scope, network access, secrets, resource limits and risk level before execution.

## First milestone

The current `AgentRuntime` is intentionally dependency-light and does not replace the existing chat/tool stack yet. The next integration step is to adapt the existing `ToolExecutor`, MCP manager and skill manager behind `AgentToolGateway`, then expose the runtime through the existing Koin application module.

## Durable execution target

Agent tasks should eventually be persisted with:

- task id and goal
- current state and step
- plan version
- attempts
- tool calls
- observations
- verification results
- approvals
- artifacts
- event history

JSON remains suitable for portable configuration/export. Durable agent state should move toward the existing SQL migration direction as task volume grows.

## Security invariants

- LLM output never directly authorizes privileged actions.
- Destructive and critical actions require explicit approval by default.
- Tool execution has bounded time and resource limits.
- Sandbox access is mediated by policy rather than granted implicitly.
- Verification is separate from execution.
- Task events are suitable for audit and restart recovery.

## Migration rule

Do not rewrite the existing Kai-derived infrastructure in one pass. Add Tyson-native boundaries around it, migrate one subsystem at a time, and keep the application buildable after every change.
