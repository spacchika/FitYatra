package com.fityatra.app.data.dao

import androidx.room.*
import com.fityatra.app.data.entities.WorkoutPlanExercise
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutPlanExerciseDao {
    @Query("SELECT * FROM workout_plan_exercises WHERE planId = :planId ORDER BY dayOfWeek, orderInDay")
    fun getExercisesByPlan(planId: Long): Flow<List<WorkoutPlanExercise>>
    
    @Query("SELECT * FROM workout_plan_exercises WHERE planId = :planId AND dayOfWeek = :dayOfWeek ORDER BY orderInDay")
    fun getExercisesByPlanAndDay(planId: Long, dayOfWeek: Int): Flow<List<WorkoutPlanExercise>>
    
    @Insert
    suspend fun insertWorkoutPlanExercise(exercise: WorkoutPlanExercise): Long
    
    @Insert
    suspend fun insertWorkoutPlanExercises(exercises: List<WorkoutPlanExercise>)
    
    @Update
    suspend fun updateWorkoutPlanExercise(exercise: WorkoutPlanExercise)
    
    @Delete
    suspend fun deleteWorkoutPlanExercise(exercise: WorkoutPlanExercise)
    
    @Query("DELETE FROM workout_plan_exercises WHERE planId = :planId")
    suspend fun deleteExercisesByPlan(planId: Long)
    
    @Query("SELECT MAX(orderInDay) FROM workout_plan_exercises WHERE planId = :planId AND dayOfWeek = :dayOfWeek AND exerciseType = :exerciseType")
    suspend fun getMaxOrderForDay(planId: Long, dayOfWeek: Int, exerciseType: String): Int?
}
