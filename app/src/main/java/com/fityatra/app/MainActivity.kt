package com.fityatra.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.fityatra.app.ai.ClaudeAiService
import com.fityatra.app.data.AppPreferences
import com.fityatra.app.data.DatabaseInitializer
import com.fityatra.app.data.FitYatraDatabase
import com.fityatra.app.health.HealthConnectManager
import com.fityatra.app.repository.AiCoachRepository
import com.fityatra.app.repository.ExerciseRepository
import com.fityatra.app.repository.UserProfileRepository
import com.fityatra.app.repository.WorkoutRepository
import com.fityatra.app.ui.AppNavigation
import com.fityatra.app.ui.theme.FitYatraTheme
import com.fityatra.app.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ── Database ──────────────────────────────────────────────────────
        val database = FitYatraDatabase.getDatabase(this)
        val databaseInitializer = DatabaseInitializer(
            database.categoryDao(),
            database.exerciseDao()
        )
        databaseInitializer.initializeDatabase()

        // ── Preferences ───────────────────────────────────────────────────
        val appPreferences = AppPreferences(this)

        // ── Repositories ──────────────────────────────────────────────────
        val exerciseRepository = ExerciseRepository(
            database.exerciseDao(),
            database.categoryDao()
        )
        val workoutRepository = WorkoutRepository(
            database.workoutPlanDao(),
            database.workoutPlanExerciseDao(),
            database.workoutSessionDao(),
            database.workoutSetDao()
        )
        val userProfileRepository = UserProfileRepository(
            database.userProfileDao(),
            appPreferences
        )
        val aiCoachRepository = AiCoachRepository(
            aiCoachMessageDao = database.aiCoachMessageDao(),
            aiService = ClaudeAiService(),
            appPreferences = appPreferences,
            workoutRepository = workoutRepository,
            exerciseRepository = exerciseRepository
        )

        // ── Health Connect ────────────────────────────────────────────────
        val healthConnectManager = HealthConnectManager(this)

        // ── ViewModels ────────────────────────────────────────────────────
        val exerciseViewModel = ExerciseViewModel(exerciseRepository)
        val workoutPlanViewModel = WorkoutPlanViewModel(workoutRepository)
        val workoutSessionViewModel = WorkoutSessionViewModel(workoutRepository)
        val onboardingViewModel = OnboardingViewModel(
            userProfileRepository = userProfileRepository,
            aiCoachRepository = aiCoachRepository,
            workoutRepository = workoutRepository
        )
        val aiCoachViewModel = AiCoachViewModel(
            aiCoachRepository = aiCoachRepository,
            userProfileRepository = userProfileRepository,
            workoutRepository = workoutRepository,
            exerciseRepository = exerciseRepository,
            healthConnectManager = healthConnectManager
        )

        // ── Navigation start destination ──────────────────────────────────
        val isOnboarded = userProfileRepository.isOnboarded()

        setContent {
            FitYatraTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation(
                        workoutSessionViewModel = workoutSessionViewModel,
                        exerciseViewModel = exerciseViewModel,
                        workoutPlanViewModel = workoutPlanViewModel,
                        onboardingViewModel = onboardingViewModel,
                        aiCoachViewModel = aiCoachViewModel,
                        appPreferences = appPreferences,
                        isOnboarded = isOnboarded
                    )
                }
            }
        }
    }
}
