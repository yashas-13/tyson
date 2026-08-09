package com.inspiredandroid.kai.agent

/**
 * Minimal Tyson execution kernel.
 *
 * The runtime intentionally keeps planning, authorization, execution and verification
 * as separate stages. This prevents the LLM from becoming the authority that both
 * chooses and authorizes an action.
 */
class AgentRuntime(
    private val planner: AgentPlanner,
    private val policy: AgentPolicy,
    private val tools: AgentToolGateway,
    private val verifier: AgentVerifier,
    private val events: AgentEventSink,
    private val maxRecoveryAttempts: Int = 2,
) {
    suspend fun run(task: AgentTask): AgentTask {
        var current = task
        events.emit(AgentEvent.StateChanged(current))

        val plan = try {
            current = current.copy(state = AgentState.PLANNING)
            events.emit(AgentEvent.StateChanged(current))
            planner.createPlan(current.goal)
        } catch (error: Throwable) {
            val failed = current.copy(
                state = AgentState.FAILED,
                lastError = error.message ?: error::class.simpleName,
            )
            events.emit(AgentEvent.Failed(failed, failed.lastError ?: "Planning failed"))
            return failed
        }

        if (plan.steps.isEmpty()) {
            val completed = current.copy(state = AgentState.COMPLETED)
            events.emit(AgentEvent.Completed(completed))
            return completed
        }

        var index = current.stepIndex
        while (index < plan.steps.size) {
            val step = plan.steps[index]
            var attempts = 0
            var stepComplete = false

            while (!stepComplete) {
                current = current.copy(
                    state = AgentState.EXECUTING,
                    stepIndex = index,
                    attempt = attempts,
                    lastError = null,
                )
                events.emit(AgentEvent.StateChanged(current))

                when (val authorization = policy.authorize(current, step)) {
                    Authorization.Allow -> Unit
                    is Authorization.RequireApproval -> {
                        val waiting = current.copy(state = AgentState.WAITING_APPROVAL)
                        events.emit(AgentEvent.StateChanged(waiting))
                        events.emit(AgentEvent.ApprovalRequired(current.id, step, authorization.reason))
                        return waiting
                    }
                    is Authorization.Deny -> {
                        val failed = current.copy(state = AgentState.FAILED, lastError = authorization.reason)
                        events.emit(AgentEvent.Failed(failed, authorization.reason))
                        return failed
                    }
                }

                events.emit(AgentEvent.StepStarted(current.id, step, index))
                val observation = try {
                    tools.execute(step)
                } catch (error: Throwable) {
                    AgentObservation(false, error.message ?: error::class.simpleName.orEmpty())
                }

                events.emit(AgentEvent.StepCompleted(current.id, step, observation))
                current = current.copy(state = AgentState.VERIFYING)
                events.emit(AgentEvent.StateChanged(current))

                val verification = try {
                    verifier.verify(current, step, observation)
                } catch (error: Throwable) {
                    VerificationResult(false, error.message ?: "Verification failed")
                }

                if (verification.success) {
                    stepComplete = true
                    index++
                    continue
                }

                attempts++
                val message = verification.message.ifBlank { "Step verification failed" }
                events.emit(AgentEvent.StepFailed(current.id, step, message))

                if (attempts > maxRecoveryAttempts) {
                    val failed = current.copy(state = AgentState.FAILED, attempt = attempts, lastError = message)
                    events.emit(AgentEvent.Failed(failed, message))
                    return failed
                }

                current = current.copy(
                    state = AgentState.RECOVERING,
                    attempt = attempts,
                    lastError = message,
                )
                events.emit(AgentEvent.StateChanged(current))
            }
        }

        val completed = current.copy(
            state = AgentState.COMPLETED,
            stepIndex = plan.steps.size,
            lastError = null,
        )
        events.emit(AgentEvent.Completed(completed))
        return completed
    }
}
