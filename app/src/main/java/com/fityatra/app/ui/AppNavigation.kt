package com.fityatra.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fityatra.app.data.AppPreferences
import com.fityatra.app.ui.screens.*
import com.fityatra.app.viewmodel.*

@Composable
fun AppNavigation(
    workoutSessionViewModel: WorkoutSessionViewModel,
    exerciseViewModel: ExerciseViewModel,
    workoutPlanViewModel: WorkoutPlanViewModel,
    onboardingViewModel: OnboardingViewModel,
    aiCoachViewModel: AiCoachViewModel,
    appPreferences: AppPreferences,
    isOnboarded: Boolean
) {
    val navController = rememberNavController()
    val startDestination = if (isOnboarded) "ai_coach" else "onboarding"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") {
            OnboardingScreen(navController, onboardingViewModel)
        }
        composable("ai_coach") {
            AiCoachScreen(navController, aiCoachViewModel)
        }
        composable("home") {
            HomeScreen(navController)
        }
        composable("workout_session") {
            WorkoutSessionScreen(navController, workoutSessionViewModel)
        }
        composable("exercises") {
            ExerciseListScreen(navController, exerciseViewModel)
        }
        composable("workout_plans") {
            WorkoutPlansScreen(navController, workoutPlanViewModel)
        }
        composable("calendar") {
            CalendarScreen(navController, workoutSessionViewModel)
        }
        composable("progress") {
            ProgressScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController, appPreferences)
        }
    }
}
