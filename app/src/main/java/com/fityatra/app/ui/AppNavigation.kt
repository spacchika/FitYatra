package com.fityatra.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fityatra.app.ui.screens.HomeScreen
import com.fityatra.app.ui.screens.WorkoutSessionScreen
import com.fityatra.app.ui.screens.ExerciseListScreen
import com.fityatra.app.ui.screens.WorkoutPlansScreen
import com.fityatra.app.ui.screens.CalendarScreen
import com.fityatra.app.ui.screens.ProgressScreen
import com.fityatra.app.ui.screens.SettingsScreen
import com.fityatra.app.viewmodel.WorkoutSessionViewModel
import com.fityatra.app.viewmodel.ExerciseViewModel
import com.fityatra.app.viewmodel.WorkoutPlanViewModel

@Composable
fun AppNavigation(
    workoutSessionViewModel: WorkoutSessionViewModel,
    exerciseViewModel: ExerciseViewModel,
    workoutPlanViewModel: WorkoutPlanViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
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
            SettingsScreen(navController) 
        }
    }
}
