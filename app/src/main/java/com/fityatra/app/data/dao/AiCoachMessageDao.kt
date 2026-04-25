package com.fityatra.app.data.dao

import androidx.room.*
import com.fityatra.app.data.entities.AiCoachMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface AiCoachMessageDao {

    @Query("SELECT * FROM ai_coach_messages WHERE sessionDate = :date ORDER BY timestamp ASC")
    fun getMessagesByDate(date: String): Flow<List<AiCoachMessage>>

    @Query("SELECT * FROM ai_coach_messages WHERE sessionDate = :date ORDER BY timestamp ASC")
    suspend fun getMessagesByDateSync(date: String): List<AiCoachMessage>

    @Query("SELECT * FROM ai_coach_messages ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(limit: Int = 30): List<AiCoachMessage>

    @Insert
    suspend fun insertMessage(message: AiCoachMessage): Long

    @Query("DELETE FROM ai_coach_messages WHERE sessionDate < :cutoffDate")
    suspend fun deleteOldMessages(cutoffDate: String)
}
