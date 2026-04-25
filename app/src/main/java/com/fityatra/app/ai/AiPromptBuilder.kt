package com.fityatra.app.ai

import com.fityatra.app.data.entities.Exercise
import com.fityatra.app.data.entities.UserProfile
import com.fityatra.app.data.entities.WorkoutPlan
import com.fityatra.app.data.entities.WorkoutPlanExercise
import com.fityatra.app.data.model.WellnessData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AiPromptBuilder {

    fun buildSystemPrompt(
        profile: UserProfile?,
        activePlan: WorkoutPlan?,
        todaysExercises: List<WorkoutPlanExercise>,
        exerciseNames: Map<Long, String>,
        wellnessData: WellnessData,
        recentSessionSummary: String,
        todayLabel: String
    ): String = buildString {
        appendLine("You are FitYatra AI Coach — a knowledgeable, supportive, and evidence-based personal fitness coach embedded in the FitYatra app.")
        appendLine()
        appendLine("Your responsibilities:")
        appendLine("1. Guide users through their daily workout based on their active plan.")
        appendLine("2. Proactively suggest workout adjustments ONLY when Health Connect wellness data is available.")
        appendLine("3. Respond to user questions about workouts, form, nutrition, and recovery.")
        appendLine("4. When a user asks you to adjust their plan, provide specific exercise/set/rep/weight changes.")
        appendLine("5. Be encouraging but concise. Keep responses under 300 words unless creating a full plan.")
        appendLine()

        if (profile != null && profile.isOnboarded) {
            val bmi = if (profile.heightCm > 0)
                String.format("%.1f", profile.weightKg / ((profile.heightCm / 100f) * (profile.heightCm / 100f)))
            else "N/A"

            appendLine("=== USER PROFILE ===")
            appendLine("Age: ${profile.age} | Weight: ${profile.weightKg}kg | Height: ${profile.heightCm}cm | BMI: $bmi")
            appendLine("Goal: ${profile.fitnessGoal.replace('_', ' ')}")
            appendLine("Experience: ${profile.experienceLevel} | Days/week: ${profile.daysPerWeek}")
            if (profile.healthConditions.isNotBlank()) {
                appendLine("Health conditions: ${profile.healthConditions.replace('|', ',')}")
            }
            appendLine("Preferences: ${profile.workoutPreferences.replace('|', ',')}")
            appendLine("Equipment: ${profile.availableEquipment.replace('|', ',')}")
            appendLine()
        }

        appendLine("=== TODAY ($todayLabel) ===")
        if (activePlan != null && todaysExercises.isNotEmpty()) {
            appendLine("Active plan: ${activePlan.name}")
            appendLine("Today's exercises:")
            val byType = todaysExercises.groupBy { it.exerciseType }
            listOf("warmup", "main", "cooldown").forEach { type ->
                val exs = byType[type]
                if (!exs.isNullOrEmpty()) {
                    appendLine("  [${type.replaceFirstChar { it.uppercase() }}]")
                    exs.sortedBy { it.orderInDay }.forEach { ex ->
                        val name = exerciseNames[ex.exerciseId] ?: "Exercise #${ex.exerciseId}"
                        appendLine("    - $name: ${ex.sets}×${ex.reps} @ ${ex.weight}kg, rest ${ex.restSeconds}s")
                    }
                }
            }
        } else if (activePlan != null) {
            appendLine("Active plan: ${activePlan.name} — no exercises scheduled for today.")
        } else {
            appendLine("No active workout plan. Suggest the user create one.")
        }
        appendLine()

        if (wellnessData.isAvailable) {
            appendLine("=== WELLNESS DATA (from Health Connect) ===")
            wellnessData.hrv?.let { appendLine("HRV: ${String.format("%.0f", it)} ms") }
            wellnessData.restingHeartRate?.let { appendLine("Resting HR: ${String.format("%.0f", it)} bpm") }
            wellnessData.vo2Max?.let { appendLine("VO2 Max: ${String.format("%.1f", it)} ml/kg/min") }
            wellnessData.sleepEfficiency?.let { appendLine("Sleep efficiency: ${String.format("%.0f", it)}%") }
            appendLine()
            appendLine("Wellness interpretation guidelines (use these to give proactive suggestions):")
            appendLine("- HRV < 40ms OR sleep efficiency < 75%: Recommend reducing volume by 20–30%, focus on technique.")
            appendLine("- HRV 40–60ms OR sleep efficiency 75–85%: Normal workout, monitor RPE.")
            appendLine("- HRV > 60ms AND sleep efficiency > 85%: Good recovery, can push intensity.")
            appendLine("- Resting HR > 10 bpm above personal average: Consider rest day or light session.")
        } else {
            appendLine("=== WELLNESS DATA ===")
            appendLine("Not available (Health Connect not connected or no data). Do NOT suggest wellness-based adjustments proactively.")
            appendLine("Only suggest adjustments if the user explicitly reports how they feel.")
        }
        appendLine()

        if (recentSessionSummary.isNotBlank()) {
            appendLine("=== RECENT WORKOUT HISTORY ===")
            appendLine(recentSessionSummary)
            appendLine()
        }

        appendLine("=== PLAN CREATION FORMAT ===")
        appendLine("When creating or modifying a workout plan, include a machine-readable block in EXACTLY this format:")
        appendLine("[PLAN_START]")
        appendLine("plan_name: <name>")
        appendLine("[DAY 1]")
        appendLine("exercise: <name> | type: <warmup|main|cooldown> | sets: <n> | reps: <n> | weight: <kg> | rest: <seconds>")
        appendLine("[DAY 2]")
        appendLine("...")
        appendLine("[PLAN_END]")
        appendLine("Exercise names must exactly match the FitYatra exercise library where possible.")
        appendLine("Only include this block when creating or significantly changing a plan.")
    }

    fun buildOnboardingSystemPrompt(): String = buildString {
        appendLine("You are FitYatra AI Coach. A new user has just completed their profile setup.")
        appendLine("Create a personalized workout plan based on their profile. Be warm and motivating.")
        appendLine()
        appendLine("IMPORTANT: Always include the [PLAN_START]...[PLAN_END] block with the workout plan.")
        appendLine("After the plan block, provide a brief (3–5 sentence) explanation of the plan.")
        appendLine()
        appendLine("Available exercises in FitYatra library:")
        appendLine("Chest: Bench Press, Incline Bench Press, Dumbbell Press, Push-ups, Dips, Chest Fly")
        appendLine("Back: Pull-ups, Chin-ups, Lat Pulldown, Barbell Row, Dumbbell Row, Deadlift, Romanian Deadlift")
        appendLine("Shoulders: Overhead Press, Dumbbell Shoulder Press, Lateral Raises, Arnold Press")
        appendLine("Biceps: Barbell Curl, Dumbbell Curl, Hammer Curl, Preacher Curl, Cable Curl")
        appendLine("Triceps: Close-Grip Bench Press, Tricep Dips, Overhead Tricep Extension, Tricep Pushdown, Skull Crushers")
        appendLine("Legs: Squat, Leg Press, Lunges, Bulgarian Split Squat, Leg Curl, Leg Extension, Calf Raises, Hip Thrust")
        appendLine("Core: Plank, Crunches, Russian Twists, Leg Raises, Mountain Climbers, Dead Bug")
        appendLine("Cardio: Running, Cycling, Rowing, Jump Rope, Burpees")
        appendLine()
        appendLine("Use bodyweight exercises (Push-ups, Pull-ups, Lunges, Plank, etc.) when user has no equipment.")
        appendLine("[PLAN_START]...[PLAN_END] format:")
        appendLine("[PLAN_START]")
        appendLine("plan_name: <name>")
        appendLine("[DAY 1]")
        appendLine("exercise: <name> | type: <warmup|main|cooldown> | sets: <n> | reps: <n> | weight: <kg> | rest: <seconds>")
        appendLine("[DAY N]")
        appendLine("...")
        appendLine("[PLAN_END]")
    }

    fun buildOnboardingUserMessage(profile: UserProfile): String = buildString {
        appendLine("Please create a personalized workout plan for me.")
        appendLine("My profile:")
        appendLine("- Age: ${profile.age}, Weight: ${profile.weightKg}kg, Height: ${profile.heightCm}cm")
        appendLine("- Goal: ${profile.fitnessGoal.replace('_', ' ')}")
        appendLine("- Experience level: ${profile.experienceLevel}")
        appendLine("- Workout days per week: ${profile.daysPerWeek}")
        if (profile.healthConditions.isNotBlank() && profile.healthConditions != "none") {
            appendLine("- Health conditions: ${profile.healthConditions.replace('|', ',')}")
        }
        appendLine("- Workout preferences: ${profile.workoutPreferences.replace('|', ',')}")
        appendLine("- Available equipment: ${profile.availableEquipment.replace('|', ',')}")
    }

    fun todayDateLabel(): String =
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())

    fun todayDateString(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}
