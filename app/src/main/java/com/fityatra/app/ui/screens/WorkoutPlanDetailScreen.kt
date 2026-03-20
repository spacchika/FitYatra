package com.fityatra.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fityatra.app.data.entities.WorkoutPlan
import com.fityatra.app.data.entities.WorkoutPlanExercise
import com.fityatra.app.viewmodel.WorkoutPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPlanDetailScreen(
    workoutPlan: WorkoutPlan,
    workoutPlanViewModel: WorkoutPlanViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    var selectedDay by remember { mutableStateOf(1) }
    
    // Get exercises for the selected day
    val dayExercises by workoutPlanViewModel.getExercisesByPlanAndDay(workoutPlan.id, selectedDay)
        .collectAsState(initial = emptyList())
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = workoutPlan.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${workoutPlan.daysPerWeek} days per week",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Button(onClick = onNavigateBack) {
                Text("← Back")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Day selector
        Text(
            text = "Select Day:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            
            for (day in 1..workoutPlan.daysPerWeek) {
                FilterChip(
                    onClick = { selectedDay = day },
                    label = { Text(dayNames.getOrElse(day - 1) { "Day $day" }) },
                    selected = selectedDay == day
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Exercise list for selected day
        Text(
            text = "Day $selectedDay Exercises:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (dayExercises.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No exercises scheduled for this day",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Use the edit button to add exercises",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Group exercises by type
                val warmupExercises = dayExercises.filter { it.exerciseType == "warmup" }
                val mainExercises = dayExercises.filter { it.exerciseType == "main" }
                val cooldownExercises = dayExercises.filter { it.exerciseType == "cooldown" }
                
                if (warmupExercises.isNotEmpty()) {
                    item {
                        ExerciseTypeSection(
                            title = "🔥 Warm-up",
                            exercises = warmupExercises
                        )
                    }
                }
                
                if (mainExercises.isNotEmpty()) {
                    item {
                        ExerciseTypeSection(
                            title = "💪 Main Workout",
                            exercises = mainExercises
                        )
                    }
                }
                
                if (cooldownExercises.isNotEmpty()) {
                    item {
                        ExerciseTypeSection(
                            title = "❄️ Cool-down",
                            exercises = cooldownExercises
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseTypeSection(
    title: String,
    exercises: List<WorkoutPlanExercise>
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            exercises.sortedBy { it.orderInDay }.forEach { exercise ->
                ExerciseDetailCard(exercise = exercise)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ExerciseDetailCard(
    exercise: WorkoutPlanExercise
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Exercise #${exercise.exerciseId}", // TODO: Get actual exercise name
                fontWeight = FontWeight.Medium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${exercise.sets} sets × ${exercise.reps} reps",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "Rest: ${exercise.restSeconds}s",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (exercise.weight > 0) {
                Text(
                    text = "Weight: ${exercise.weight}kg",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (exercise.notes.isNotBlank()) {
                Text(
                    text = "Notes: ${exercise.notes}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
