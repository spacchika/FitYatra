package com.fityatra.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Long = 1, // Single row for user settings
    val deloadFrequencyWeeks: Int = 8,
    val deloadWeightDropPercent: Int = 50,
    val restTimerDefault: Int = 90, // seconds
    val backupAuto: Boolean = false,
    val backupSchedule: String = "weekly" // daily, weekly, monthly
)
