package com.fityatra.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fityatra.app.ai.ParsedPlan
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
    data class PlanReview(val plan: ParsedPlan, val aiMessage: String) : OnboardingUiState()
    object SavingPlan : OnboardingUiState()
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

    private var pendingProfile: UserProfile? = null
    private var pendingAiMessage: String? = null
    private var pendingPlan: ParsedPlan? = null

    /** Calls the AI to generate a plan preview without saving anything. */
    fun requestPlanPreview(
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
                    isOnboarded = false,
                    createdAt = System.currentTimeMillis()
                )
                pendingProfile = profile

                val (aiMessage, parsedPlan) = aiCoachRepository.generateOnboardingPlanPreview(profile)
                pendingAiMessage = aiMessage
                pendingPlan = parsedPlan

                _uiState.value = OnboardingUiState.PlanReview(parsedPlan, aiMessage)
            } catch (e: Exception) {
                _uiState.value = OnboardingUiState.Error(
                    e.message ?: "Something went wrong generating your plan."
                )
            }
        }
    }

    /** User approved the suggested plan — save everything and mark onboarding complete. */
    fun acceptPlan() {
        val profile = pendingProfile ?: return
        val aiMessage = pendingAiMessage ?: return
        val plan = pendingPlan ?: return

        viewModelScope.launch {
            _uiState.value = OnboardingUiState.SavingPlan
            try {
                val onboardedProfile = profile.copy(isOnboarded = true)
                userProfileRepository.saveUserProfile(onboardedProfile)

                val planId = aiCoachRepository.saveOnboardingPlan(profile, aiMessage, plan)
                workoutRepository.setActivePlan(planId)

                _uiState.value = OnboardingUiState.Success(planId)
            } catch (e: Exception) {
                _uiState.value = OnboardingUiState.Error(
                    e.message ?: "Failed to save your plan."
                )
            }
        }
    }

    /** User wants a different plan — call the AI again with the same profile. */
    fun regeneratePlan() {
        val profile = pendingProfile ?: run { _uiState.value = OnboardingUiState.Idle; return }
        viewModelScope.launch {
            _uiState.value = OnboardingUiState.GeneratingPlan
            try {
                val (aiMessage, parsedPlan) = aiCoachRepository.generateOnboardingPlanPreview(profile)
                pendingAiMessage = aiMessage
                pendingPlan = parsedPlan
                _uiState.value = OnboardingUiState.PlanReview(parsedPlan, aiMessage)
            } catch (e: Exception) {
                _uiState.value = OnboardingUiState.Error(
                    e.message ?: "Could not regenerate the plan."
                )
            }
        }
    }

    fun resetError() {
        _uiState.value = OnboardingUiState.Idle
    }
}
