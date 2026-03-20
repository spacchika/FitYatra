package com.fityatra.app.data.dao

import androidx.room.*
import com.fityatra.app.data.entities.WorkoutSession
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE planId = :planId ORDER BY date DESC")
    fun getSessionsByPlan(planId: Long): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): WorkoutSession?

    @Query("SELECT * FROM workout_sessions WHERE date BETWEEN :startDate AND :endDate ORDER BY date")
    fun getSessionsBetweenDates(startDate: Date, endDate: Date): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE isDeload = 1 ORDER BY date DESC")
    fun getDeloadSessions(): Flow<List<WorkoutSession>>

    @Insert
    suspend fun insertSession(session: WorkoutSession): Long

    @Update
    suspend fun updateSession(session: WorkoutSession)

    @Delete
    suspend fun deleteSession(session: WorkoutSession)
}
