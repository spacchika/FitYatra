package com.fityatra.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fityatra.app.data.entities.WorkoutSession
import com.fityatra.app.data.entities.WorkoutSet
import com.fityatra.app.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class WorkoutSessionViewModel(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    
    private val _currentSession = MutableStateFlow<WorkoutSession?>(null)
    val currentSession: StateFlow<WorkoutSession?> = _currentSession.asStateFlow()
    
    private val _currentSets = MutableStateFlow<List<WorkoutSet>>(emptyList())
    val currentSets: StateFlow<List<WorkoutSet>> = _currentSets.asStateFlow()
    
    private val _restTimer = MutableStateFlow(0)
    val restTimer: StateFlow<Int> = _restTimer.asStateFlow()
    
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()
    
    fun startWorkoutSession(planId: Long, isDeload: Boolean = false) {
        viewModelScope.launch {
            val session = WorkoutSession(
                planId = planId,
                date = Date(),
                isDeload = isDeload
            )
            val sessionId = workoutRepository.insertSession(session)
            _currentSession.value = session.copy(id = sessionId)
        }
    }
    
    fun addWorkoutSet(exerciseId: Long, reps: Int, weight: Double, restSeconds: Int = 0) {
        viewModelScope.launch {
            val sessionId = _currentSession.value?.id ?: return@launch
            val setNumber = _currentSets.value.count { it.exerciseId == exerciseId } + 1
            
            val workoutSet = WorkoutSet(
                sessionId = sessionId,
                exerciseId = exerciseId,
                setNumber = setNumber,
                reps = reps,
                weight = weight,
                restSeconds = restSeconds
            )
            
            workoutRepository.insertSet(workoutSet)
            loadCurrentSets()
        }
    }
    
    fun startRestTimer(seconds: Int) {
        _restTimer.value = seconds
        _isTimerRunning.value = true
        
        viewModelScope.launch {
            while (_restTimer.value > 0 && _isTimerRunning.value) {
                kotlinx.coroutines.delay(1000)
                _restTimer.value = _restTimer.value - 1
            }
            _isTimerRunning.value = false
        }
    }
    
    fun stopRestTimer() {
        _isTimerRunning.value = false
        _restTimer.value = 0
    }
    
    private fun loadCurrentSets() {
        viewModelScope.launch {
            val sessionId = _currentSession.value?.id ?: return@launch
            workoutRepository.getSetsBySession(sessionId).collect { sets ->
                _currentSets.value = sets
            }
        }
    }
    
    fun finishWorkoutSession() {
        _currentSession.value = null
        _currentSets.value = emptyList()
        stopRestTimer()
    }
    
    fun getAllSessions() = workoutRepository.getAllSessions()
    
    fun completeWorkoutSession(sessionId: Long) {
        viewModelScope.launch {
            // Mark session as completed
            // TODO: Update session completion status in database
            // For now, we'll just finish the current session if it matches
            if (_currentSession.value?.id == sessionId) {
                finishWorkoutSession()
            }
        }
    }
    
    fun getSessionsByPlan(planId: Long) = workoutRepository.getSessionsByPlan(planId)
    
    fun getSessionsBetweenDates(startDate: Date, endDate: Date) = 
        workoutRepository.getSessionsBetweenDates(startDate, endDate)
}
