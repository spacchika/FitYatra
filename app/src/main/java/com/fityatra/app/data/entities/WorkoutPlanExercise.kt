package com.fityatra.app.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_plan_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutPlan::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkoutPlanExercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val planId: Long,
    val exerciseId: Long,
    val dayOfWeek: Int, // 1=Monday, 2=Tuesday, etc.
    val orderInDay: Int,
    val exerciseType: String = "main", // "warmup", "main", "cooldown"
    val sets: Int = 3,
    val reps: Int = 10,
    val weight: Double = 0.0,
    val restSeconds: Int = 60,
    val notes: String = ""
)
