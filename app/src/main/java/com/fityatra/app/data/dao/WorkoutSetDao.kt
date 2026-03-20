package com.fityatra.app.data.dao

import androidx.room.*
import com.fityatra.app.data.entities.WorkoutSet
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSetDao {
    @Query("SELECT * FROM workout_sets WHERE sessionId = :sessionId ORDER BY exerciseId, setNumber")
    fun getSetsBySession(sessionId: Long): Flow<List<WorkoutSet>>

    @Query("SELECT * FROM workout_sets WHERE exerciseId = :exerciseId ORDER BY sessionId DESC, setNumber")
    fun getSetsByExercise(exerciseId: Long): Flow<List<WorkoutSet>>

    @Query("SELECT * FROM workout_sets WHERE id = :id")
    suspend fun getSetById(id: Long): WorkoutSet?

    @Query("SELECT MAX(weight) FROM workout_sets WHERE exerciseId = :exerciseId")
    suspend fun getMaxWeightForExercise(exerciseId: Long): Double?

    @Query("SELECT AVG(weight) FROM workout_sets WHERE exerciseId = :exerciseId AND sessionId IN (SELECT id FROM workout_sessions WHERE date >= :sinceDate)")
    suspend fun getAverageWeightSince(exerciseId: Long, sinceDate: Long): Double?

    @Insert
    suspend fun insertSet(set: WorkoutSet): Long

    @Insert
    suspend fun insertSets(sets: List<WorkoutSet>)

    @Update
    suspend fun updateSet(set: WorkoutSet)

    @Delete
    suspend fun deleteSet(set: WorkoutSet)

    @Query("DELETE FROM workout_sets WHERE sessionId = :sessionId")
    suspend fun deleteSetsBySession(sessionId: Long)
}
