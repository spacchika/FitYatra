package com.fityatra.app.repository

import com.fityatra.app.data.dao.WorkoutPlanDao
import com.fityatra.app.data.dao.WorkoutPlanExerciseDao
import com.fityatra.app.data.dao.WorkoutSessionDao
import com.fityatra.app.data.dao.WorkoutSetDao
import com.fityatra.app.data.entities.WorkoutPlan
import com.fityatra.app.data.entities.WorkoutPlanExercise
import com.fityatra.app.data.entities.WorkoutSession
import com.fityatra.app.data.entities.WorkoutSet
import kotlinx.coroutines.flow.Flow
import java.util.Date

class WorkoutRepository(
    private val workoutPlanDao: WorkoutPlanDao,
    private val workoutPlanExerciseDao: WorkoutPlanExerciseDao,
    private val workoutSessionDao: WorkoutSessionDao,
    private val workoutSetDao: WorkoutSetDao
) {
    // Workout Plans
    fun getAllWorkoutPlans(): Flow<List<WorkoutPlan>> = workoutPlanDao.getAllWorkoutPlans()
    
    suspend fun getWorkoutPlanById(id: Long): WorkoutPlan? = workoutPlanDao.getWorkoutPlanById(id)
    
    suspend fun getActivePlan(): WorkoutPlan? = workoutPlanDao.getActivePlan()
    
    suspend fun insertWorkoutPlan(plan: WorkoutPlan): Long = workoutPlanDao.insertWorkoutPlan(plan)
    
    suspend fun updateWorkoutPlan(plan: WorkoutPlan) = workoutPlanDao.updateWorkoutPlan(plan)
    
    suspend fun deleteWorkoutPlan(plan: WorkoutPlan) = workoutPlanDao.deleteWorkoutPlan(plan)
    
    suspend fun setActivePlan(planId: Long) {
        workoutPlanDao.deactivateAllPlans()
        workoutPlanDao.setActivePlan(planId)
    }
    
    // Workout Sessions
    fun getAllSessions(): Flow<List<WorkoutSession>> = workoutSessionDao.getAllSessions()
    
    fun getSessionsByPlan(planId: Long): Flow<List<WorkoutSession>> = 
        workoutSessionDao.getSessionsByPlan(planId)
    
    suspend fun getSessionById(id: Long): WorkoutSession? = workoutSessionDao.getSessionById(id)
    
    fun getSessionsBetweenDates(startDate: Date, endDate: Date): Flow<List<WorkoutSession>> = 
        workoutSessionDao.getSessionsBetweenDates(startDate, endDate)
    
    suspend fun insertSession(session: WorkoutSession): Long = workoutSessionDao.insertSession(session)
    
    suspend fun updateSession(session: WorkoutSession) = workoutSessionDao.updateSession(session)
    
    suspend fun deleteSession(session: WorkoutSession) = workoutSessionDao.deleteSession(session)
    
    // Workout Sets
    fun getSetsBySession(sessionId: Long): Flow<List<WorkoutSet>> = 
        workoutSetDao.getSetsBySession(sessionId)
    
    fun getSetsByExercise(exerciseId: Long): Flow<List<WorkoutSet>> = 
        workoutSetDao.getSetsByExercise(exerciseId)
    
    suspend fun getMaxWeightForExercise(exerciseId: Long): Double? = 
        workoutSetDao.getMaxWeightForExercise(exerciseId)
    
    suspend fun insertSet(set: WorkoutSet): Long = workoutSetDao.insertSet(set)
    
    suspend fun insertSets(sets: List<WorkoutSet>) = workoutSetDao.insertSets(sets)
    
    suspend fun updateSet(set: WorkoutSet) = workoutSetDao.updateSet(set)
    
    suspend fun deleteSet(set: WorkoutSet) = workoutSetDao.deleteSet(set)
    
    // Workout Plan Exercises
    fun getExercisesByPlan(planId: Long): Flow<List<WorkoutPlanExercise>> = 
        workoutPlanExerciseDao.getExercisesByPlan(planId)
    
    fun getExercisesByPlanAndDay(planId: Long, dayOfWeek: Int): Flow<List<WorkoutPlanExercise>> = 
        workoutPlanExerciseDao.getExercisesByPlanAndDay(planId, dayOfWeek)
    
    suspend fun insertWorkoutPlanExercise(exercise: WorkoutPlanExercise): Long = 
        workoutPlanExerciseDao.insertWorkoutPlanExercise(exercise)
    
    suspend fun insertWorkoutPlanExercises(exercises: List<WorkoutPlanExercise>) = 
        workoutPlanExerciseDao.insertWorkoutPlanExercises(exercises)
    
    suspend fun updateWorkoutPlanExercise(exercise: WorkoutPlanExercise) = 
        workoutPlanExerciseDao.updateWorkoutPlanExercise(exercise)
    
    suspend fun deleteWorkoutPlanExercise(exercise: WorkoutPlanExercise) = 
        workoutPlanExerciseDao.deleteWorkoutPlanExercise(exercise)
    
    suspend fun deleteExercisesByPlan(planId: Long) = 
        workoutPlanExerciseDao.deleteExercisesByPlan(planId)
    
    suspend fun getMaxOrderForDay(planId: Long, dayOfWeek: Int, exerciseType: String): Int? = 
        workoutPlanExerciseDao.getMaxOrderForDay(planId, dayOfWeek, exerciseType)
}
