package com.fityatra.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_coach_messages")
data class AiCoachMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    // "yyyy-MM-dd" date string used to group messages by day
    val sessionDate: String
)
