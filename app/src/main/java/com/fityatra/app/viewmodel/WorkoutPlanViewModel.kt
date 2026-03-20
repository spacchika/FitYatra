package com.fityatra.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fityatra.app.data.entities.WorkoutPlan
import com.fityatra.app.data.entities.WorkoutPlanExercise
import com.fityatra.app.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutPlanViewModel(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    
    private val _workoutPlans = MutableStateFlow<List<WorkoutPlan>>(emptyList())
    val workoutPlans: StateFlow<List<WorkoutPlan>> = _workoutPlans.asStateFlow()
    
    private val _activePlan = MutableStateFlow<WorkoutPlan?>(null)
    val activePlan: StateFlow<WorkoutPlan?> = _activePlan.asStateFlow()
    
    private val _isCreatingPlan = MutableStateFlow(false)
    val isCreatingPlan: StateFlow<Boolean> = _isCreatingPlan.asStateFlow()
    
    init {
        loadWorkoutPlans()
        loadActivePlan()
    }
    
    private fun loadWorkoutPlans() {
        viewModelScope.launch {
            workoutRepository.getAllWorkoutPlans().collect { plans ->
                _workoutPlans.value = plans
            }
        }
    }
    
    private fun loadActivePlan() {
        viewModelScope.launch {
            val activePlan = workoutRepository.getActivePlan()
            _activePlan.value = activePlan
        }
    }
    
    fun createWorkoutPlan(name: String, description: String, daysPerWeek: Int) {
        viewModelScope.launch {
            _isCreatingPlan.value = true
            val plan = WorkoutPlan(
                name = name,
                description = description,
                daysPerWeek = daysPerWeek,
                isActive = false
            )
            workoutRepository.insertWorkoutPlan(plan)
            _isCreatingPlan.value = false
        }
    }
    
    fun createEnhancedPlan(
        name: String, 
        description: String, 
        daysPerWeek: Int, 
        goal: com.fityatra.app.ui.screens.FitnessGoal, 
        exercises: List<com.fityatra.app.ui.screens.ExerciseSuggestion>, 
        startDate: String
    ) {
        viewModelScope.launch {
            _isCreatingPlan.value = true
            
            // Create the workout plan with enhanced description
            val enhancedDescription = if (description.isNotBlank()) {
                "$description\n\nGoal: ${goal.displayName}\nStart Date: $startDate\nExercises: ${exercises.size}"
            } else {
                "Goal: ${goal.displayName}\nStart Date: $startDate\nExercises: ${exercises.size}"
            }
            
            val plan = WorkoutPlan(
                name = name,
                description = enhancedDescription,
                daysPerWeek = daysPerWeek,
                isActive = false
            )
            
            // Insert the plan and get its ID
            val planId = workoutRepository.insertWorkoutPlan(plan)
            
            // Distribute exercises properly across days
            val exercisesPerDay = kotlin.math.max(1, exercises.size / daysPerWeek)
            
            exercises.forEachIndexed { index, exerciseSuggestion ->
                // Calculate which day this exercise belongs to
                val dayOfWeek = (index / exercisesPerDay) + 1
                val dayIndex = index % exercisesPerDay
                
                // Determine exercise type based on position within the day
                val exerciseType = when {
                    dayIndex == 0 && exercisesPerDay > 1 -> "warmup" // First exercise of each day
                    dayIndex == exercisesPerDay - 1 && exercisesPerDay > 2 -> "cooldown" // Last exercise of each day
                    else -> "main" // Middle exercises or all exercises if only 1-2 per day
                }
                
                // Use a simple mapping of exercise names to IDs for now
                // This ensures consistent IDs for the same exercises
                val exerciseId = (exerciseSuggestion.name.hashCode().toLong().let { 
                    if (it < 0) -it else it 
                } % 1000) + 1
                
                val planExercise = WorkoutPlanExercise(
                    planId = planId,
                    exerciseId = exerciseId,
                    dayOfWeek = dayOfWeek,
                    orderInDay = dayIndex + 1,
                    exerciseType = exerciseType,
                    sets = when (goal) {
                        com.fityatra.app.ui.screens.FitnessGoal.BULK -> 4
                        com.fityatra.app.ui.screens.FitnessGoal.CUT -> 3
                        com.fityatra.app.ui.screens.FitnessGoal.MAINTAIN -> 3
                    },
                    reps = when (goal) {
                        com.fityatra.app.ui.screens.FitnessGoal.BULK -> 8
                        com.fityatra.app.ui.screens.FitnessGoal.CUT -> 12
                        com.fityatra.app.ui.screens.FitnessGoal.MAINTAIN -> 10
                    },
                    weight = 0.0,
                    restSeconds = when (exerciseType) {
                        "warmup" -> 30
                        "main" -> when (goal) {
                            com.fityatra.app.ui.screens.FitnessGoal.BULK -> 180
                            com.fityatra.app.ui.screens.FitnessGoal.CUT -> 60
                            com.fityatra.app.ui.screens.FitnessGoal.MAINTAIN -> 90
                        }
                        "cooldown" -> 30
                        else -> 60
                    },
                    notes = if (exerciseType == "warmup") "Warm-up exercise" 
                           else if (exerciseType == "cooldown") "Cool-down exercise" 
                           else ""
                )
                workoutRepository.insertWorkoutPlanExercise(planExercise)
            }
            
            // Implement calendar scheduling logic
            if (startDate.isNotBlank()) {
                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val parsedDate = dateFormat.parse(startDate)
                    if (parsedDate != null) {
                        scheduleWorkoutPlan(planId, parsedDate, daysPerWeek)
                    }
                } catch (e: Exception) {
                    // If date parsing fails, skip scheduling
                    // In a production app, you'd want to handle this error properly
                }
            }
            
            _isCreatingPlan.value = false
        }
    }
    
    fun setActivePlan(planId: Long) {
        viewModelScope.launch {
            workoutRepository.setActivePlan(planId)
            loadActivePlan()
        }
    }
    

    
    // Deload week logic
    fun calculateDeloadWeek(weeksSinceStart: Int, deloadFrequency: Int = 8): Boolean {
        return weeksSinceStart > 0 && weeksSinceStart % deloadFrequency == 0
    }
    
    fun calculateDeloadWeight(originalWeight: Double, deloadPercent: Int = 50): Double {
        return originalWeight * (100 - deloadPercent) / 100.0
    }
    
    // Weight progression suggestions
    fun suggestWeightIncrease(
        currentWeight: Double,
        repsCompleted: Int,
        targetReps: Int,
        increasePercent: Double = 2.5
    ): Double {
        return if (repsCompleted >= targetReps) {
            currentWeight * (1 + increasePercent / 100.0)
        } else {
            currentWeight
        }
    }
    
    fun suggestWeightDecrease(
        currentWeight: Double,
        repsCompleted: Int,
        targetReps: Int,
        decreasePercent: Double = 5.0
    ): Double {
        return if (repsCompleted < targetReps * 0.8) { // If completed less than 80% of target
            currentWeight * (1 - decreasePercent / 100.0)
        } else {
            currentWeight
        }
    }
    
    private suspend fun scheduleWorkoutPlan(planId: Long, startDate: java.util.Date, daysPerWeek: Int) {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = startDate
        
        // Schedule for 12 weeks initially (3 months)
        val totalWeeks = 12
        val deloadWeekInterval = 6 // Every 6 weeks
        
        for (week in 1..totalWeeks) {
            val isDeloadWeek = week % deloadWeekInterval == 0
            
            // Schedule workout days for this week
            for (day in 1..daysPerWeek) {
                val workoutSession = com.fityatra.app.data.entities.WorkoutSession(
                    planId = planId,
                    date = java.util.Date(calendar.timeInMillis),
                    isDeload = isDeloadWeek
                )
                
                workoutRepository.insertSession(workoutSession)
                
                // Move to next workout day (skip weekends for 3-day, or distribute evenly for 4-day)
                if (daysPerWeek == 3) {
                    // Monday, Wednesday, Friday pattern
                    when (day) {
                        1 -> calendar.add(java.util.Calendar.DAY_OF_MONTH, 2) // Mon to Wed
                        2 -> calendar.add(java.util.Calendar.DAY_OF_MONTH, 2) // Wed to Fri
                        3 -> calendar.add(java.util.Calendar.DAY_OF_MONTH, 3) // Fri to next Mon
                    }
                } else if (daysPerWeek == 4) {
                    // Monday, Tuesday, Thursday, Friday pattern
                    when (day) {
                        1 -> calendar.add(java.util.Calendar.DAY_OF_MONTH, 1) // Mon to Tue
                        2 -> calendar.add(java.util.Calendar.DAY_OF_MONTH, 2) // Tue to Thu
                        3 -> calendar.add(java.util.Calendar.DAY_OF_MONTH, 1) // Thu to Fri
                        4 -> calendar.add(java.util.Calendar.DAY_OF_MONTH, 3) // Fri to next Mon
                    }
                } else {
                    // For other patterns, just add a day
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
                }
            }
            
            // If we just finished a week, move to the start of next week
            if (daysPerWeek == 3 || daysPerWeek == 4) {
                // Already handled in the day loop above
            } else {
                // For custom patterns, ensure we move to next week
                calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1)
                calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)
            }
        }
    }
    
    // Workout Plan Editing Methods
    fun updateWorkoutPlan(plan: WorkoutPlan) {
        viewModelScope.launch {
            workoutRepository.updateWorkoutPlan(plan)
            loadWorkoutPlans()
        }
    }
    
    fun deleteWorkoutPlan(plan: WorkoutPlan) {
        viewModelScope.launch {
            // Delete all associated exercises first
            workoutRepository.deleteExercisesByPlan(plan.id)
            // Delete the plan itself
            workoutRepository.deleteWorkoutPlan(plan)
            loadWorkoutPlans()
            // If this was the active plan, clear it
            if (_activePlan.value?.id == plan.id) {
                _activePlan.value = null
            }
        }
    }
    
    fun addExerciseToPlan(
        planId: Long,
        exerciseId: Long,
        dayOfWeek: Int,
        exerciseType: String = "main",
        sets: Int = 3,
        reps: Int = 10,
        weight: Double = 0.0,
        restSeconds: Int = 60,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val maxOrder = workoutRepository.getMaxOrderForDay(planId, dayOfWeek, exerciseType) ?: 0
            val exercise = WorkoutPlanExercise(
                planId = planId,
                exerciseId = exerciseId,
                dayOfWeek = dayOfWeek,
                orderInDay = maxOrder + 1,
                exerciseType = exerciseType,
                sets = sets,
                reps = reps,
                weight = weight,
                restSeconds = restSeconds,
                notes = notes
            )
            workoutRepository.insertWorkoutPlanExercise(exercise)
        }
    }
    
    fun updatePlanExercise(exercise: WorkoutPlanExercise) {
        viewModelScope.launch {
            workoutRepository.updateWorkoutPlanExercise(exercise)
        }
    }
    
    fun deletePlanExercise(exercise: WorkoutPlanExercise) {
        viewModelScope.launch {
            workoutRepository.deleteWorkoutPlanExercise(exercise)
        }
    }
    
    fun reorderExercises(planId: Long, dayOfWeek: Int, exerciseType: String, exercises: List<WorkoutPlanExercise>) {
        viewModelScope.launch {
            exercises.forEachIndexed { index, exercise ->
                val updatedExercise = exercise.copy(orderInDay = index + 1)
                workoutRepository.updateWorkoutPlanExercise(updatedExercise)
            }
        }
    }
    
    fun getExercisesByPlan(planId: Long) = workoutRepository.getExercisesByPlan(planId)
    
    fun getExercisesByPlanAndDay(planId: Long, dayOfWeek: Int) = workoutRepository.getExercisesByPlanAndDay(planId, dayOfWeek)
    
    // Alias method for backward compatibility
    fun deletePlan(plan: WorkoutPlan) = deleteWorkoutPlan(plan)
}
