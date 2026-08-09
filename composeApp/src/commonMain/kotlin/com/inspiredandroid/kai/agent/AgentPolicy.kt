package com.inspiredandroid.kai.agent

/** Risk classification used by the policy boundary. */
enum class AgentRisk {
    SAFE,
    NORMAL,
    SENSITIVE,
    DANGEROUS,
    CRITICAL,
}

/**
 * Small deterministic policy implementation for the first runtime milestone.
 *
 * It is intentionally conservative: steps that explicitly require approval are
 * never auto-approved. A richer implementation can map tool capabilities and
 * runtime permissions onto this same contract.
 */
class DefaultAgentPolicy(
    private val approvalRequiredFor: Set<AgentRisk> = setOf(
        AgentRisk.DANGEROUS,
        AgentRisk.CRITICAL,
    ),
) : AgentPolicy {
    override suspend fun authorize(task: AgentTask, step: AgentStep): Authorization {
        if (step.requiresApproval) {
            return Authorization.RequireApproval("The plan explicitly requested user approval.")
        }

        val risk = step.arguments["risk"]
            ?.uppercase()
            ?.let { value -> runCatching { AgentRisk.valueOf(value) }.getOrNull() }
            ?: AgentRisk.NORMAL

        return if (risk in approvalRequiredFor) {
            Authorization.RequireApproval("${risk.name.lowercase()} action requires approval.")
        } else {
            Authorization.Allow
        }
    }
}
