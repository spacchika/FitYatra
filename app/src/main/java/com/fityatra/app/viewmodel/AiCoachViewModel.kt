package com.fityatra.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fityatra.app.data.entities.AiCoachMessage
import com.fityatra.app.data.entities.Exercise
import com.fityatra.app.data.entities.UserProfile
import com.fityatra.app.data.entities.WorkoutPlan
import com.fityatra.app.data.entities.WorkoutPlanExercise
import com.fityatra.app.data.model.WellnessData
import com.fityatra.app.health.HealthConnectManager
import com.fityatra.app.repository.AiCoachRepository
import com.fityatra.app.repository.ExerciseRepository
import com.fityatra.app.repository.UserProfileRepository
import com.fityatra.app.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class AiCoachViewModel(
    private val aiCoachRepository: AiCoachRepository,
    private val userProfileRepository: UserProfileRepository,
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val healthConnectManager: HealthConnectManager
) : ViewModel() {

    private val _messages = MutableStateFlow<List<AiCoachMessage>>(emptyList())
    val messages: StateFlow<List<AiCoachMessage>> = _messages.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _activePlan = MutableStateFlow<WorkoutPlan?>(null)
    val activePlan: StateFlow<WorkoutPlan?> = _activePlan.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _wellnessData = MutableStateFlow(WellnessData())
    val wellnessData: StateFlow<WellnessData> = _wellnessData.asStateFlow()

    private val _healthConnectAvailable = MutableStateFlow(false)
    val healthConnectAvailable: StateFlow<Boolean> = _healthConnectAvailable.asStateFlow()

    private val _todaysExercises = MutableStateFlow<List<WorkoutPlanExercise>>(emptyList())
    val todaysExercises: StateFlow<List<WorkoutPlanExercise>> = _todaysExercises.asStateFlow()

    private val _exerciseNames = MutableStateFlow<Map<Long, String>>(emptyMap())
    val exerciseNames: StateFlow<Map<Long, String>> = _exerciseNames.asStateFlow()

    init {
        loadContext()
        observeTodaysMessages()
    }

    private fun loadContext() {
        viewModelScope.launch {
            try {
                _userProfile.value = userProfileRepository.getUserProfile()
                _activePlan.value = workoutRepository.getActivePlan()
                _healthConnectAvailable.value = healthConnectManager.isAvailable()

                if (healthConnectManager.isAvailable() && healthConnectManager.hasPermissions()) {
                    _wellnessData.value = healthConnectManager.readWellnessData()
                }

                loadTodaysExercises()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load data: ${e.message}"
            }
        }
    }

    private fun loadTodaysExercises() {
        viewModelScope.launch {
            try {
                val plan = _activePlan.value ?: return@launch
                val calDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                val planDay = ((calDay + 5) % 7) + 1
                workoutRepository.getExercisesByPlanAndDay(plan.id, planDay).collect { exercises ->
                    _todaysExercises.value = exercises
                    resolveExerciseNames(exercises)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load exercises: ${e.message}"
            }
        }
    }

    private suspend fun resolveExerciseNames(exercises: List<WorkoutPlanExercise>) {
        val names = mutableMapOf<Long, String>()
        exercises.forEach { ex ->
            if (!names.containsKey(ex.exerciseId)) {
                val exercise = exerciseRepository.getExerciseById(ex.exerciseId)
                names[ex.exerciseId] = exercise?.name ?: "Exercise #${ex.exerciseId}"
            }
        }
        _exerciseNames.value = names
    }

    private fun observeTodaysMessages() {
        viewModelScope.launch {
            try {
                val today = com.fityatra.app.ai.AiPromptBuilder.todayDateString()
                aiCoachRepository.getMessagesByDate(today).collect { msgs ->
                    _messages.value = msgs
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load messages: ${e.message}"
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _isSending.value) return
        viewModelScope.launch {
            _isSending.value = true
            _errorMessage.value = null
            try {
                aiCoachRepository.sendMessage(
                    userText = text,
                    userProfile = _userProfile.value,
                    activePlan = _activePlan.value,
                    todaysExercises = _todaysExercises.value,
                    exerciseNames = _exerciseNames.value,
                    wellnessData = _wellnessData.value
                )
            } catch (e: Exception) {
                _errorMessage.value = "Failed to reach AI Coach: ${e.message}"
            } finally {
                _isSending.value = false
            }
        }
    }

    fun refreshWellnessData() {
        viewModelScope.launch {
            if (healthConnectManager.isAvailable() && healthConnectManager.hasPermissions()) {
                _wellnessData.value = healthConnectManager.readWellnessData()
            }
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }
}
