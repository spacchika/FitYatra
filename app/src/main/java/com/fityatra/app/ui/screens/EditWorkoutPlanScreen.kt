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
import com.fityatra.app.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWorkoutPlanScreen(
    workoutPlan: WorkoutPlan,
    workoutPlanViewModel: WorkoutPlanViewModel = viewModel(),
    exerciseViewModel: ExerciseViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editingPlan by remember { mutableStateOf(workoutPlan) }
    var selectedDay by remember { mutableStateOf(1) }
    var selectedExerciseType by remember { mutableStateOf("warmup") }
    
    val planExercises by workoutPlanViewModel.getExercisesByPlan(workoutPlan.id).collectAsState(initial = emptyList())
    val allExercises by exerciseViewModel.exercises.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with plan name and actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = editingPlan.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = editingPlan.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row {
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("🗑️ Delete Plan")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(onClick = onNavigateBack) {
                    Text("✓ Done")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Day selector
        Text(
            text = "Select Day",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            days.forEachIndexed { index, day ->
                FilterChip(
                    onClick = { selectedDay = index + 1 },
                    label = { Text(day) },
                    selected = selectedDay == index + 1
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Exercise type selector
        Text(
            text = "Exercise Type",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val exerciseTypes = listOf("warmup" to "🔥 Warm-up", "main" to "💪 Main", "cooldown" to "❄️ Cool-down")
            exerciseTypes.forEach { (type, label) ->
                FilterChip(
                    onClick = { selectedExerciseType = type },
                    label = { Text(label) },
                    selected = selectedExerciseType == type
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Exercises for selected day and type
        val filteredExercises = planExercises.filter { 
            it.dayOfWeek == selectedDay && it.exerciseType == selectedExerciseType 
        }.sortedBy { it.orderInDay }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Exercises (${filteredExercises.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            
            Button(
                onClick = { 
                    // Add new exercise logic - for now just add a placeholder
                    if (allExercises.isNotEmpty()) {
                        workoutPlanViewModel.addExerciseToPlan(
                            planId = workoutPlan.id,
                            exerciseId = allExercises.first().id,
                            dayOfWeek = selectedDay,
                            exerciseType = selectedExerciseType
                        )
                    }
                }
            ) {
                Text("+ Add Exercise")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Exercise list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredExercises) { exercise ->
                ExerciseEditCard(
                    exercise = exercise,
                    allExercises = allExercises,
                    onUpdateExercise = { updatedExercise ->
                        workoutPlanViewModel.updatePlanExercise(updatedExercise)
                    },
                    onDeleteExercise = { exerciseToDelete ->
                        workoutPlanViewModel.deletePlanExercise(exerciseToDelete)
                    }
                )
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Workout Plan") },
            text = { Text("Are you sure you want to delete \"${editingPlan.name}\"? This action cannot be undone and will remove all associated exercises and sessions.") },
            confirmButton = {
                Button(
                    onClick = {
                        workoutPlanViewModel.deleteWorkoutPlan(editingPlan)
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ExerciseEditCard(
    exercise: WorkoutPlanExercise,
    allExercises: List<com.fityatra.app.data.entities.Exercise>,
    onUpdateExercise: (WorkoutPlanExercise) -> Unit,
    onDeleteExercise: (WorkoutPlanExercise) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var editingSets by remember { mutableStateOf(exercise.sets.toString()) }
    var editingReps by remember { mutableStateOf(exercise.reps.toString()) }
    var editingWeight by remember { mutableStateOf(exercise.weight.toString()) }
    var editingRest by remember { mutableStateOf(exercise.restSeconds.toString()) }
    var editingNotes by remember { mutableStateOf(exercise.notes) }
    
    val exerciseDetails = allExercises.find { it.id == exercise.exerciseId }
    
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
                Column {
                    Text(
                        text = exerciseDetails?.name ?: "Unknown Exercise",
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${exercise.sets} sets × ${exercise.reps} reps @ ${exercise.weight}kg",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row {
                    TextButton(onClick = { isExpanded = !isExpanded }) {
                        Text(if (isExpanded) "▲" else "▼")
                    }
                    TextButton(
                        onClick = { onDeleteExercise(exercise) }
                    ) {
                        Text("🗑️")
                    }
                }
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Exercise details editing
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editingSets,
                        onValueChange = { editingSets = it },
                        label = { Text("Sets") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = editingReps,
                        onValueChange = { editingReps = it },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editingWeight,
                        onValueChange = { editingWeight = it },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = editingRest,
                        onValueChange = { editingRest = it },
                        label = { Text("Rest (sec)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = editingNotes,
                    onValueChange = { editingNotes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        val updatedExercise = exercise.copy(
                            sets = editingSets.toIntOrNull() ?: exercise.sets,
                            reps = editingReps.toIntOrNull() ?: exercise.reps,
                            weight = editingWeight.toDoubleOrNull() ?: exercise.weight,
                            restSeconds = editingRest.toIntOrNull() ?: exercise.restSeconds,
                            notes = editingNotes
                        )
                        onUpdateExercise(updatedExercise)
                        isExpanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("✓ Save Changes")
                }
            }
        }
    }
}
