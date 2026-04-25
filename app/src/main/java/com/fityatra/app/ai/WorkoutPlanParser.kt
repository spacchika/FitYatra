package com.fityatra.app.ai

import com.fityatra.app.data.entities.WorkoutPlanExercise

data class ParsedPlan(
    val name: String,
    val exercises: List<ParsedExercise>
)

data class ParsedExercise(
    val name: String,
    val dayOfWeek: Int,
    val type: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Double,
    val restSeconds: Int,
    val orderInDay: Int
)

object WorkoutPlanParser {

    // Known exercise name → DB id mapping (matches PrefilledData.kt)
    private val exerciseIdMap: Map<String, Long> = mapOf(
        "bench press" to 1L, "incline bench press" to 2L, "dumbbell press" to 3L,
        "incline dumbbell press" to 4L, "push-ups" to 5L, "push ups" to 5L,
        "dips" to 6L, "chest fly" to 7L,
        "pull-ups" to 8L, "pull ups" to 8L, "chin-ups" to 9L, "chin ups" to 9L,
        "lat pulldown" to 10L, "barbell row" to 11L, "dumbbell row" to 12L,
        "t-bar row" to 13L, "deadlift" to 14L, "romanian deadlift" to 15L,
        "overhead press" to 16L, "dumbbell shoulder press" to 17L,
        "lateral raises" to 18L, "front raises" to 19L, "rear delt fly" to 20L,
        "upright row" to 21L, "arnold press" to 22L,
        "barbell curl" to 23L, "dumbbell curl" to 24L, "hammer curl" to 25L,
        "preacher curl" to 26L, "cable curl" to 27L, "concentration curl" to 28L,
        "close-grip bench press" to 29L, "tricep dips" to 30L,
        "overhead tricep extension" to 31L, "tricep pushdown" to 32L,
        "diamond push-ups" to 33L, "skull crushers" to 34L,
        "squat" to 35L, "front squat" to 36L, "leg press" to 37L,
        "lunges" to 38L, "bulgarian split squat" to 39L, "leg curl" to 40L,
        "leg extension" to 41L, "calf raises" to 42L, "hip thrust" to 43L,
        "plank" to 44L, "crunches" to 45L, "russian twists" to 46L,
        "leg raises" to 47L, "mountain climbers" to 48L, "dead bug" to 49L,
        "bicycle crunches" to 50L,
        "running" to 51L, "cycling" to 52L, "rowing" to 53L,
        "jump rope" to 54L, "burpees" to 55L
    )

    fun resolveExerciseId(name: String): Long {
        val key = name.lowercase().trim()
        return exerciseIdMap[key]
            ?: exerciseIdMap.entries.firstOrNull { key.contains(it.key) }?.value
            ?: (key.hashCode().toLong().let { if (it < 0) -it else it } % 55) + 1
    }

    /**
     * Extracts a [ParsedPlan] from an AI response that contains a [PLAN_START]...[PLAN_END] block.
     * Returns null if no valid plan block is found.
     */
    fun parse(aiResponse: String): ParsedPlan? {
        val start = aiResponse.indexOf("[PLAN_START]")
        val end = aiResponse.indexOf("[PLAN_END]")
        if (start == -1 || end == -1 || end <= start) return null

        val block = aiResponse.substring(start + "[PLAN_START]".length, end).trim()
        val lines = block.lines().map { it.trim() }.filter { it.isNotBlank() }

        var planName = "AI Coach Plan"
        var currentDay = 0
        val exercises = mutableListOf<ParsedExercise>()
        val orderPerDay = mutableMapOf<Int, Int>()

        for (line in lines) {
            when {
                line.startsWith("plan_name:", ignoreCase = true) -> {
                    planName = line.substringAfter(":").trim()
                }
                line.startsWith("[DAY", ignoreCase = true) -> {
                    // e.g. "[DAY 1]" or "[DAY 1] Monday"
                    val dayNum = line.removePrefix("[").removeSuffix("]")
                        .trim().removePrefix("DAY").trim()
                        .split(" ").firstOrNull()?.toIntOrNull()
                    if (dayNum != null) currentDay = dayNum
                }
                line.startsWith("exercise:", ignoreCase = true) && currentDay > 0 -> {
                    // Format: "exercise: <name> | type: <t> | sets: <n> | reps: <n> | weight: <n> | rest: <n>"
                    // First extract the exercise name (everything between "exercise:" and first "|")
                    val exerciseName = line.substringAfter("exercise:")
                        .substringBefore("|")
                        .trim()
                    if (exerciseName.isBlank()) continue

                    // Parse remaining key:value pairs from the pipe-separated fields
                    val fieldMap = line.split("|")
                        .drop(1) // skip the "exercise: <name>" part
                        .mapNotNull { field ->
                            val colonIdx = field.indexOf(':')
                            if (colonIdx == -1) null
                            else field.substring(0, colonIdx).trim().lowercase() to
                                 field.substring(colonIdx + 1).trim()
                        }
                        .toMap()

                    val order = orderPerDay.getOrDefault(currentDay, 0) + 1
                    orderPerDay[currentDay] = order

                    exercises.add(
                        ParsedExercise(
                            name = exerciseName,
                            dayOfWeek = currentDay,
                            type = fieldMap["type"] ?: "main",
                            sets = fieldMap["sets"]?.toIntOrNull() ?: 3,
                            reps = fieldMap["reps"]?.toIntOrNull() ?: 10,
                            weightKg = fieldMap["weight"]?.toDoubleOrNull() ?: 0.0,
                            restSeconds = fieldMap["rest"]?.toIntOrNull() ?: 60,
                            orderInDay = order
                        )
                    )
                }
            }
        }

        if (exercises.isEmpty()) return null
        return ParsedPlan(name = planName, exercises = exercises)
    }

    fun toWorkoutPlanExercises(parsed: ParsedPlan, planId: Long): List<WorkoutPlanExercise> {
        return parsed.exercises.map { ex ->
            WorkoutPlanExercise(
                planId = planId,
                exerciseId = resolveExerciseId(ex.name),
                dayOfWeek = ex.dayOfWeek,
                orderInDay = ex.orderInDay,
                exerciseType = ex.type,
                sets = ex.sets,
                reps = ex.reps,
                weight = ex.weightKg,
                restSeconds = ex.restSeconds,
                notes = ""
            )
        }
    }
}
