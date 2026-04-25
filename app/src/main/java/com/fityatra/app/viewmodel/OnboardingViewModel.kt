package com.fityatra.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fityatra.app.data.entities.UserProfile
import com.fityatra.app.repository.AiCoachRepository
import com.fityatra.app.repository.UserProfileRepository
import com.fityatra.app.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class OnboardingUiState {
    object Idle : OnboardingUiState()
    object GeneratingPlan : OnboardingUiState()
    data class Success(val planId: Long) : OnboardingUiState()
    data class Error(val message: String) : OnboardingUiState()
}

class OnboardingViewModel(
    private val userProfileRepository: UserProfileRepository,
    private val aiCoachRepository: AiCoachRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Idle)
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    /**
     * Saves the user profile, calls AI to generate a plan, stores it, and sets it active.
     */
    fun completeOnboarding(
        age: Int,
        weightKg: Float,
        heightCm: Float,
        fitnessGoal: String,
        healthConditions: String,
        workoutPreferences: String,
        availableEquipment: String,
        experienceLevel: String,
        daysPerWeek: Int
    ) {
        viewModelScope.launch {
            _uiState.value = OnboardingUiState.GeneratingPlan
            try {
                val profile = UserProfile(
                    age = age,
                    weightKg = weightKg,
                    heightCm = heightCm,
                    fitnessGoal = fitnessGoal,
                    healthConditions = healthConditions,
                    workoutPreferences = workoutPreferences,
                    availableEquipment = availableEquipment,
                    experienceLevel = experienceLevel,
                    daysPerWeek = daysPerWeek,
                    isOnboarded = true,
                    createdAt = System.currentTimeMillis()
                )
                userProfileRepository.saveUserProfile(profile)

                val planId = aiCoachRepository.generateOnboardingPlan(profile)
                workoutRepository.setActivePlan(planId)

                _uiState.value = OnboardingUiState.Success(planId)
            } catch (e: Exception) {
                _uiState.value = OnboardingUiState.Error(
                    e.message ?: "Something went wrong during onboarding."
                )
            }
        }
    }

    fun resetError() {
        _uiState.value = OnboardingUiState.Idle
    }
}
