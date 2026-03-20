package com.fityatra.app.ui.screens

import androidx.compose.foundation.clickable
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
import com.fityatra.app.data.entities.WorkoutSession
import com.fityatra.app.viewmodel.WorkoutPlanViewModel
import com.fityatra.app.viewmodel.WorkoutSessionViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSessionFlow(
    workoutSession: WorkoutSession,
    workoutPlan: WorkoutPlan,
    dayOfWeek: Int,
    workoutPlanViewModel: WorkoutPlanViewModel = viewModel(),
    workoutSessionViewModel: WorkoutSessionViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    var currentExerciseIndex by remember { mutableStateOf(0) }
    var showExerciseDetail by remember { mutableStateOf(false) }
    var selectedExercise by remember { mutableStateOf<WorkoutPlanExercise?>(null) }
    
    val planExercises by workoutPlanViewModel.getExercisesByPlanAndDay(workoutPlan.id, dayOfWeek).collectAsState(initial = emptyList())
    val sortedExercises = planExercises.sortedWith(compareBy<WorkoutPlanExercise> { 
        when (it.exerciseType) {
            "warmup" -> 0
            "main" -> 1
            "cooldown" -> 2
            else -> 3
        }
    }.thenBy { it.orderInDay })
    
    if (showExerciseDetail && selectedExercise != null) {
        ExerciseDetailScreen(
            exercise = selectedExercise!!,
            workoutSession = workoutSession,
            isDeloadDay = workoutSession.isDeload,
            workoutSessionViewModel = workoutSessionViewModel,
            onNavigateBack = { 
                showExerciseDetail = false
                selectedExercise = null
            },
            onCompleteExercise = {
                showExerciseDetail = false
                selectedExercise = null
                // Move to next exercise or complete workout
                if (currentExerciseIndex < sortedExercises.size - 1) {
                    currentExerciseIndex++
                }
            }
        )
    } else {
        WorkoutOverviewScreen(
            workoutSession = workoutSession,
            workoutPlan = workoutPlan,
            exercises = sortedExercises,
            onNavigateBack = onNavigateBack,
            onStartExercise = { exercise ->
                selectedExercise = exercise
                showExerciseDetail = true
            },
            onCompleteWorkout = {
                // Mark workout as completed
                workoutSessionViewModel.completeWorkoutSession(workoutSession.id)
                onNavigateBack()
            }
        )
    }
}

@Composable
fun WorkoutOverviewScreen(
    workoutSession: WorkoutSession,
    workoutPlan: WorkoutPlan,
    exercises: List<WorkoutPlanExercise>,
    onNavigateBack: () -> Unit,
    onStartExercise: (WorkoutPlanExercise) -> Unit,
    onCompleteWorkout: () -> Unit
) {
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
                    text = if (workoutSession.isDeload) "🔥 Deload Day - Use 50-60% weight" else "💪 Regular Workout",
                    fontSize = 14.sp,
                    color = if (workoutSession.isDeload) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
            
            Button(onClick = onNavigateBack) {
                Text("← Back")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Workout progress
        val completedExercises = 0 // TODO: Track completed exercises
        LinearProgressIndicator(
            progress = if (exercises.isNotEmpty()) completedExercises.toFloat() / exercises.size else 0f,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Progress: $completedExercises / ${exercises.size} exercises",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Exercise categories
        val warmupExercises = exercises.filter { it.exerciseType == "warmup" }
        val mainExercises = exercises.filter { it.exerciseType == "main" }
        val cooldownExercises = exercises.filter { it.exerciseType == "cooldown" }
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (warmupExercises.isNotEmpty()) {
                item {
                    ExerciseCategorySection(
                        title = "🔥 Warm-up",
                        exercises = warmupExercises,
                        isDeloadDay = workoutSession.isDeload,
                        onStartExercise = onStartExercise
                    )
                }
            }
            
            if (mainExercises.isNotEmpty()) {
                item {
                    ExerciseCategorySection(
                        title = "💪 Main Workout",
                        exercises = mainExercises,
                        isDeloadDay = workoutSession.isDeload,
                        onStartExercise = onStartExercise
                    )
                }
            }
            
            if (cooldownExercises.isNotEmpty()) {
                item {
                    ExerciseCategorySection(
                        title = "❄️ Cool-down",
                        exercises = cooldownExercises,
                        isDeloadDay = workoutSession.isDeload,
                        onStartExercise = onStartExercise
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onCompleteWorkout,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✓ Complete Workout")
                }
            }
        }
    }
}

@Composable
fun ExerciseCategorySection(
    title: String,
    exercises: List<WorkoutPlanExercise>,
    isDeloadDay: Boolean,
    onStartExercise: (WorkoutPlanExercise) -> Unit
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
            
            exercises.forEach { exercise ->
                ExerciseOverviewCard(
                    exercise = exercise,
                    isDeloadDay = isDeloadDay,
                    onStartExercise = { onStartExercise(exercise) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ExerciseOverviewCard(
    exercise: WorkoutPlanExercise,
    isDeloadDay: Boolean,
    onStartExercise: () -> Unit
) {
    val suggestedWeight = if (isDeloadDay) exercise.weight * 0.55 else exercise.weight
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStartExercise() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Exercise #${exercise.exerciseId}", // TODO: Get actual exercise name
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${exercise.sets} sets × ${exercise.reps} reps",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isDeloadDay) {
                    Text(
                        text = "Suggested: ${String.format("%.1f", suggestedWeight)}kg (55% of normal)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text(
                        text = "Last: ${exercise.weight}kg",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text("▶", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun ExerciseDetailScreen(
    exercise: WorkoutPlanExercise,
    workoutSession: WorkoutSession,
    isDeloadDay: Boolean,
    workoutSessionViewModel: WorkoutSessionViewModel,
    onNavigateBack: () -> Unit,
    onCompleteExercise: () -> Unit
) {
    var currentSet by remember { mutableStateOf(1) }
    var currentWeight by remember { mutableStateOf(if (isDeloadDay) exercise.weight * 0.55 else exercise.weight) }
    var currentReps by remember { mutableStateOf(exercise.reps) }
    var restTimer by remember { mutableStateOf(0) }
    var isResting by remember { mutableStateOf(false) }
    
    val suggestedWeight = if (isDeloadDay) exercise.weight * 0.55 else exercise.weight
    
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
                    text = "Exercise #${exercise.exerciseId}", // TODO: Get actual exercise name
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Set $currentSet of ${exercise.sets}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Button(onClick = onNavigateBack) {
                Text("← Back")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Deload day warning
        if (isDeloadDay) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🔥 Deload Day",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "Use 50-60% of your normal weight to allow recovery",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "Suggested weight: ${String.format("%.1f", suggestedWeight)}kg",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Previous workout history (placeholder)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "📊 Previous Workout",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Last time: ${exercise.weight}kg × ${exercise.reps} reps × ${exercise.sets} sets",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Rest time: ${exercise.restSeconds}s",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Current set input
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Current Set",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = currentWeight.toString(),
                        onValueChange = { currentWeight = it.toDoubleOrNull() ?: currentWeight },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    OutlinedTextField(
                        value = currentReps.toString(),
                        onValueChange = { currentReps = it.toIntOrNull() ?: currentReps },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        // Record the set
                        // TODO: Save set data
                        
                        if (currentSet < exercise.sets) {
                            // Start rest timer
                            isResting = true
                            restTimer = exercise.restSeconds
                            currentSet++
                        } else {
                            // Exercise completed
                            onCompleteExercise()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (currentSet < exercise.sets) "✓ Complete Set & Rest" else "✓ Complete Exercise"
                    )
                }
            }
        }
        
        // Rest timer
        if (isResting) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⏱️ Rest Time",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${restTimer}s",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { isResting = false }
                    ) {
                        Text("Skip Rest")
                    }
                }
            }
        }
    }
}
