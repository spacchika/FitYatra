package com.fityatra.app.data.dao

import androidx.room.*
import com.fityatra.app.data.entities.WorkoutPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanDao {
    @Query("SELECT * FROM workout_plans ORDER BY name")
    fun getAllWorkoutPlans(): Flow<List<WorkoutPlan>>

    @Query("SELECT * FROM workout_plans WHERE id = :id")
    suspend fun getWorkoutPlanById(id: Long): WorkoutPlan?

    @Query("SELECT * FROM workout_plans WHERE isActive = 1 LIMIT 1")
    suspend fun getActivePlan(): WorkoutPlan?

    @Insert
    suspend fun insertWorkoutPlan(plan: WorkoutPlan): Long

    @Update
    suspend fun updateWorkoutPlan(plan: WorkoutPlan)

    @Delete
    suspend fun deleteWorkoutPlan(plan: WorkoutPlan)

    @Query("UPDATE workout_plans SET isActive = 0")
    suspend fun deactivateAllPlans()

    @Query("UPDATE workout_plans SET isActive = 1 WHERE id = :planId")
    suspend fun setActivePlan(planId: Long)
}
