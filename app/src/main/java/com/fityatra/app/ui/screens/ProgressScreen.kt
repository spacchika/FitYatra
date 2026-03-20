package com.fityatra.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

data class ExerciseProgress(
    val exerciseName: String,
    val currentWeight: Double,
    val previousWeight: Double,
    val currentReps: Int,
    val previousReps: Int,
    val trend: ProgressTrend
)

enum class ProgressTrend {
    INCREASING, DECREASING, STABLE
}

@Composable
fun ProgressScreen(navController: NavHostController) {
    // Mock data for progress tracking
    val progressData = remember {
        listOf(
            ExerciseProgress("Bench Press", 80.0, 75.0, 8, 8, ProgressTrend.INCREASING),
            ExerciseProgress("Squat", 100.0, 105.0, 6, 8, ProgressTrend.DECREASING),
            ExerciseProgress("Deadlift", 120.0, 120.0, 5, 5, ProgressTrend.STABLE),
            ExerciseProgress("Overhead Press", 50.0, 47.5, 8, 6, ProgressTrend.INCREASING),
            ExerciseProgress("Barbell Row", 70.0, 70.0, 8, 8, ProgressTrend.STABLE)
        )
    }
    
    val totalWorkouts = 24
    val currentStreak = 5
    val averageWorkoutTime = 65 // minutes
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Progress & Stats",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Summary Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(
                title = "Total Workouts",
                value = totalWorkouts.toString(),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            StatCard(
                title = "Current Streak",
                value = "$currentStreak days",
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            StatCard(
                title = "Avg Duration",
                value = "${averageWorkoutTime}min",
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Exercise Progress Section
        Text(
            text = "Exercise Progress",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(progressData) { progress ->
                ExerciseProgressCard(progress = progress)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Back Button
        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ExerciseProgressCard(progress: ExerciseProgress) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = progress.exerciseName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                // Trend Icon
                when (progress.trend) {
                    ProgressTrend.INCREASING -> {
                        Text(
                            "↗️",
                            color = Color.Green
                        )
                    }
                    ProgressTrend.DECREASING -> {
                        Text(
                            "↘️",
                            color = Color.Red
                        )
                    }
                    ProgressTrend.STABLE -> {
                        Text(
                            "➡️",
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Weight Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Weight",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${progress.currentWeight}kg",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Previous: ${progress.previousWeight}kg",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column {
                    Text(
                        text = "Reps",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${progress.currentReps}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Previous: ${progress.previousReps}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Weight Suggestion
            val suggestion = calculateWeightSuggestion(progress)
            if (suggestion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = suggestion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

fun calculateWeightSuggestion(progress: ExerciseProgress): String {
    return when {
        progress.currentReps > progress.previousReps && progress.currentWeight >= progress.previousWeight -> {
            val suggestedWeight = progress.currentWeight * 1.025 // 2.5% increase
            "Suggestion: Try ${String.format("%.1f", suggestedWeight)}kg next session"
        }
        progress.currentReps < progress.previousReps * 0.8 -> {
            val suggestedWeight = progress.currentWeight * 0.95 // 5% decrease
            "Suggestion: Consider ${String.format("%.1f", suggestedWeight)}kg next session"
        }
        else -> ""
    }
}
