package com.fityatra.app.repository

import com.fityatra.app.ai.AiPromptBuilder
import com.fityatra.app.ai.ChatMessage
import com.fityatra.app.ai.ClaudeAiService
import com.fityatra.app.ai.WorkoutPlanParser
import com.fityatra.app.data.AppPreferences
import com.fityatra.app.data.dao.AiCoachMessageDao
import com.fityatra.app.data.entities.AiCoachMessage
import com.fityatra.app.data.entities.Exercise
import com.fityatra.app.data.entities.UserProfile
import com.fityatra.app.data.entities.WorkoutPlan
import com.fityatra.app.data.entities.WorkoutPlanExercise
import com.fityatra.app.data.model.WellnessData
import kotlinx.coroutines.flow.Flow

class AiCoachRepository(
    private val aiCoachMessageDao: AiCoachMessageDao,
    private val aiService: ClaudeAiService,
    private val appPreferences: AppPreferences,
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository
) {
    companion object {
        // Max history messages sent to Claude per request
        private const val MAX_HISTORY = 20
    }

    fun getMessagesByDate(date: String): Flow<List<AiCoachMessage>> =
        aiCoachMessageDao.getMessagesByDate(date)

    /**
     * Sends a user message to Claude and persists both the user turn and AI reply.
     * Returns the AI reply text.
     */
    suspend fun sendMessage(
        userText: String,
        userProfile: UserProfile?,
        activePlan: WorkoutPlan?,
        todaysExercises: List<WorkoutPlanExercise>,
        exerciseNames: Map<Long, String>,
        wellnessData: WellnessData,
        recentSessionSummary: String = ""
    ): String {
        val today = AiPromptBuilder.todayDateString()

        // Persist user message
        aiCoachMessageDao.insertMessage(
            AiCoachMessage(
                role = "user",
                content = userText,
                timestamp = System.currentTimeMillis(),
                sessionDate = today
            )
        )

        // Build conversation history for Claude
        val history = aiCoachMessageDao
            .getRecentMessages(MAX_HISTORY)
            .reversed()
            .dropLast(1) // exclude the message just inserted (it will be the new user turn)
            .map { ChatMessage(it.role, it.content) }

        val systemPrompt = AiPromptBuilder.buildSystemPrompt(
            profile = userProfile,
            activePlan = activePlan,
            todaysExercises = todaysExercises,
            exerciseNames = exerciseNames,
            wellnessData = wellnessData,
            recentSessionSummary = recentSessionSummary,
            todayLabel = AiPromptBuilder.todayDateLabel()
        )

        val apiKey = appPreferences.claudeApiKey
        val replyResult = aiService.sendMessage(
            apiKey = apiKey,
            systemPrompt = systemPrompt,
            history = history,
            userMessage = userText
        )

        val replyText = replyResult.getOrElse { e ->
            "Sorry, I couldn't connect right now. ${e.message}"
        }

        // Persist assistant reply
        aiCoachMessageDao.insertMessage(
            AiCoachMessage(
                role = "assistant",
                content = replyText,
                timestamp = System.currentTimeMillis(),
                sessionDate = today
            )
        )

        // If the reply contains a plan block, persist it
        if (replyText.contains("[PLAN_START]")) {
            parsePlanAndStore(replyText)
        }

        return replyText
    }

    /**
     * Sends the onboarding prompt to Claude to generate a personalised workout plan.
     * Persists the plan in the DB and marks it active. Returns the plan id.
     */
    suspend fun generateOnboardingPlan(profile: UserProfile): Long {
        val systemPrompt = AiPromptBuilder.buildOnboardingSystemPrompt()
        val userMessage = AiPromptBuilder.buildOnboardingUserMessage(profile)

        val today = AiPromptBuilder.todayDateString()
        aiCoachMessageDao.insertMessage(
            AiCoachMessage(
                role = "user",
                content = userMessage,
                timestamp = System.currentTimeMillis(),
                sessionDate = today
            )
        )

        val replyResult = aiService.sendMessage(
            apiKey = appPreferences.claudeApiKey,
            systemPrompt = systemPrompt,
            history = emptyList(),
            userMessage = userMessage
        )

        val replyText = replyResult.getOrElse { e ->
            buildFallbackPlanMessage(profile)
        }

        aiCoachMessageDao.insertMessage(
            AiCoachMessage(
                role = "assistant",
                content = replyText,
                timestamp = System.currentTimeMillis(),
                sessionDate = today
            )
        )

        return parsePlanAndStore(replyText) ?: createDefaultPlan(profile)
    }

    private suspend fun parsePlanAndStore(aiResponse: String): Long? {
        val parsed = WorkoutPlanParser.parse(aiResponse) ?: return null

        val plan = WorkoutPlan(
            name = parsed.name,
            description = "AI-generated plan",
            daysPerWeek = parsed.exercises.maxOfOrNull { it.dayOfWeek } ?: 3,
            isActive = false
        )
        val planId = workoutRepository.insertWorkoutPlan(plan)

        val planExercises = WorkoutPlanParser.toWorkoutPlanExercises(parsed, planId)
        planExercises.forEach { workoutRepository.insertWorkoutPlanExercise(it) }

        return planId
    }

    private suspend fun createDefaultPlan(profile: UserProfile): Long {
        val plan = WorkoutPlan(
            name = "My Fitness Plan",
            description = "Personalised for ${profile.fitnessGoal.replace('_', ' ')}",
            daysPerWeek = profile.daysPerWeek,
            isActive = false
        )
        return workoutRepository.insertWorkoutPlan(plan)
    }

    private fun buildFallbackPlanMessage(profile: UserProfile) = """
        Welcome to FitYatra! I've created a starter plan for you based on your goal:
        ${profile.fitnessGoal.replace('_', ' ')}.
        You can customise it from the Workout Plans screen or chat with me anytime!

        [PLAN_START]
        plan_name: My Starter Plan
        [DAY 1]
        exercise: Push-ups | type: warmup | sets: 2 | reps: 10 | weight: 0 | rest: 30
        exercise: Bench Press | type: main | sets: 3 | reps: 10 | weight: 0 | rest: 90
        exercise: Dumbbell Row | type: main | sets: 3 | reps: 10 | weight: 0 | rest: 90
        exercise: Plank | type: cooldown | sets: 3 | reps: 30 | weight: 0 | rest: 30
        [DAY 2]
        exercise: Running | type: main | sets: 1 | reps: 20 | weight: 0 | rest: 60
        exercise: Squat | type: main | sets: 3 | reps: 10 | weight: 0 | rest: 90
        exercise: Lunges | type: main | sets: 3 | reps: 12 | weight: 0 | rest: 60
        [DAY 3]
        exercise: Pull-ups | type: main | sets: 3 | reps: 8 | weight: 0 | rest: 90
        exercise: Overhead Press | type: main | sets: 3 | reps: 10 | weight: 0 | rest: 90
        exercise: Crunches | type: cooldown | sets: 3 | reps: 15 | weight: 0 | rest: 30
        [PLAN_END]
    """.trimIndent()

    fun getApiKey(): String = appPreferences.claudeApiKey
}
