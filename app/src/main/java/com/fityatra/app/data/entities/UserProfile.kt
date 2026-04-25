package com.fityatra.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Long = 1,
    val age: Int = 0,
    val weightKg: Float = 0f,
    val heightCm: Float = 0f,
    // "build_muscle" | "lose_weight" | "improve_endurance" | "general_fitness"
    val fitnessGoal: String = "",
    // pipe-separated: "diabetes|hypertension"
    val healthConditions: String = "",
    // pipe-separated: "strength|cardio|hiit|yoga"
    val workoutPreferences: String = "",
    // pipe-separated: "barbell|dumbbell|bodyweight|cables|machines|resistance_bands"
    val availableEquipment: String = "",
    // "beginner" | "intermediate" | "advanced"
    val experienceLevel: String = "beginner",
    val daysPerWeek: Int = 4,
    val isOnboarded: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
