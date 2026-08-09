package com.inspiredandroid.kai.agent

/** Model-facing planner. The implementation can wrap Tyson's existing provider stack. */
interface AgentPlanner {
    suspend fun createPlan(goal: String): AgentPlan
}

/** Policy is deliberately separate from the model and executor. */
interface AgentPolicy {
    suspend fun authorize(task: AgentTask, step: AgentStep): Authorization
}

sealed interface Authorization {
    data object Allow : Authorization
    data class RequireApproval(val reason: String) : Authorization
    data class Deny(val reason: String) : Authorization
}

/** Gateway over native tools, MCP tools and skills. */
interface AgentToolGateway {
    suspend fun execute(step: AgentStep): AgentObservation
}

/** Verification is independent from execution so success is never assumed. */
interface AgentVerifier {
    suspend fun verify(
        task: AgentTask,
        step: AgentStep,
        observation: AgentObservation,
    ): VerificationResult
}

interface AgentEventSink {
    suspend fun emit(event: AgentEvent)
}

sealed interface AgentEvent {
    data class StateChanged(val task: AgentTask) : AgentEvent
    data class StepStarted(val taskId: String, val step: AgentStep, val index: Int) : AgentEvent
    data class StepCompleted(val taskId: String, val step: AgentStep, val observation: AgentObservation) : AgentEvent
    data class ApprovalRequired(val taskId: String, val step: AgentStep, val reason: String) : AgentEvent
    data class StepFailed(val taskId: String, val step: AgentStep, val message: String) : AgentEvent
    data class Completed(val task: AgentTask) : AgentEvent
    data class Failed(val task: AgentTask, val message: String) : AgentEvent
}
