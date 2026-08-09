package com.inspiredandroid.kai.agent

/** Durable lifecycle states for a Tyson agent task. */
enum class AgentState {
    CREATED,
    PLANNING,
    WAITING_APPROVAL,
    EXECUTING,
    VERIFYING,
    RECOVERING,
    COMPLETED,
    FAILED,
    CANCELLED,
}

data class AgentTask(
    val id: String,
    val goal: String,
    val state: AgentState = AgentState.CREATED,
    val stepIndex: Int = 0,
    val attempt: Int = 0,
    val lastError: String? = null,
)

data class AgentStep(
    val id: String,
    val description: String,
    val toolName: String,
    val arguments: Map<String, String> = emptyMap(),
    val requiresApproval: Boolean = false,
)

data class AgentPlan(
    val steps: List<AgentStep>,
)

data class AgentObservation(
    val success: Boolean,
    val output: String,
    val metadata: Map<String, String> = emptyMap(),
)

data class VerificationResult(
    val success: Boolean,
    val message: String,
)
