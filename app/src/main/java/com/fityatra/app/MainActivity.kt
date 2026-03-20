package com.fityatra.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.fityatra.app.data.FitYatraDatabase
import com.fityatra.app.data.DatabaseInitializer
import com.fityatra.app.repository.ExerciseRepository
import com.fityatra.app.repository.WorkoutRepository
import com.fityatra.app.viewmodel.ExerciseViewModel
import com.fityatra.app.viewmodel.WorkoutPlanViewModel
import com.fityatra.app.viewmodel.WorkoutSessionViewModel
import com.fityatra.app.ui.AppNavigation
import com.fityatra.app.ui.theme.FitYatraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize database
        val database = FitYatraDatabase.getDatabase(this)
        val databaseInitializer = DatabaseInitializer(
            database.categoryDao(),
            database.exerciseDao()
        )
        databaseInitializer.initializeDatabase()
        
        // Initialize repositories
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
        
        // Initialize ViewModels
        val exerciseViewModel = ExerciseViewModel(exerciseRepository)
        val workoutPlanViewModel = WorkoutPlanViewModel(workoutRepository)
        val workoutSessionViewModel = WorkoutSessionViewModel(workoutRepository)
        
        setContent {
            FitYatraTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation(
                        workoutSessionViewModel = workoutSessionViewModel,
                        exerciseViewModel = exerciseViewModel,
                        workoutPlanViewModel = workoutPlanViewModel
                    )
                }
            }
        }
    }
}
