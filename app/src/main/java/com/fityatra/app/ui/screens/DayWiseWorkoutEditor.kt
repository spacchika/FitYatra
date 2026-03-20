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
import com.fityatra.app.viewmodel.WorkoutPlanViewModel
import com.fityatra.app.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayWiseWorkoutEditor(
    workoutPlan: WorkoutPlan,
    workoutPlanViewModel: WorkoutPlanViewModel = viewModel(),
    exerciseViewModel: ExerciseViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    var selectedDay by remember { mutableStateOf(1) }
    var selectedExerciseType by remember { mutableStateOf("warmup") }
    var showAddExerciseDialog by remember { mutableStateOf(false) }
    
    val planExercises by workoutPlanViewModel.getExercisesByPlan(workoutPlan.id).collectAsState(initial = emptyList())
    val allExercises by exerciseViewModel.exercises.collectAsState()
    
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
                    text = "Edit: ${workoutPlan.name}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Day-wise Exercise Management",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Button(onClick = onNavigateBack) {
                Text("✓ Done")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Day selector
        Text(
            text = "Select Day",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            days.forEachIndexed { index, day ->
                FilterChip(
                    onClick = { selectedDay = index + 1 },
                    label = { Text(day, fontSize = 12.sp) },
                    selected = selectedDay == index + 1,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Exercise type selector
        Text(
            text = "Exercise Type",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val exerciseTypes = listOf(
                "warmup" to "🔥 Warm-up", 
                "main" to "💪 Main", 
                "cooldown" to "❄️ Cool-down"
            )
            exerciseTypes.forEach { (type, label) ->
                FilterChip(
                    onClick = { selectedExerciseType = type },
                    label = { Text(label, fontSize = 12.sp) },
                    selected = selectedExerciseType == type,
                    modifier = Modifier.weight(1f)
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
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            
            Button(
                onClick = { showAddExerciseDialog = true }
            ) {
                Text("+ Add")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Exercise list
        if (filteredExercises.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No exercises for this day and type.\nTap 'Add' to add exercises.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredExercises) { exercise ->
                    DayWiseExerciseCard(
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
    }
    
    // Add Exercise Dialog
    if (showAddExerciseDialog) {
        AddExerciseDialog(
            availableExercises = allExercises,
            onDismiss = { showAddExerciseDialog = false },
            onAddExercise = { selectedExercise ->
                workoutPlanViewModel.addExerciseToPlan(
                    planId = workoutPlan.id,
                    exerciseId = selectedExercise.id,
                    dayOfWeek = selectedDay,
                    exerciseType = selectedExerciseType
                )
                showAddExerciseDialog = false
            }
        )
    }
}

@Composable
fun DayWiseExerciseCard(
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
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exerciseDetails?.name ?: "Exercise #${exercise.exerciseId}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${exercise.sets} sets × ${exercise.reps} reps @ ${exercise.weight}kg",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row {
                    TextButton(onClick = { isExpanded = !isExpanded }) {
                        Text(if (isExpanded) "▲" else "▼", fontSize = 12.sp)
                    }
                    TextButton(onClick = { onDeleteExercise(exercise) }) {
                        Text("🗑️", fontSize = 12.sp)
                    }
                }
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Exercise details editing
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editingSets,
                        onValueChange = { editingSets = it },
                        label = { Text("Sets", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = editingReps,
                        onValueChange = { editingReps = it },
                        label = { Text("Reps", fontSize = 12.sp) },
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
                        label = { Text("Weight (kg)", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = editingRest,
                        onValueChange = { editingRest = it },
                        label = { Text("Rest (sec)", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = editingNotes,
                    onValueChange = { editingNotes = it },
                    label = { Text("Notes", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
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

@Composable
fun AddExerciseDialog(
    availableExercises: List<com.fityatra.app.data.entities.Exercise>,
    onDismiss: () -> Unit,
    onAddExercise: (com.fityatra.app.data.entities.Exercise) -> Unit
) {
    var selectedExercise by remember { mutableStateOf<com.fityatra.app.data.entities.Exercise?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Exercise") },
        text = {
            LazyColumn {
                items(availableExercises) { exercise ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .clickable { selectedExercise = exercise },
                        colors = if (selectedExercise?.id == exercise.id) {
                            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        } else {
                            CardDefaults.cardColors()
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = exercise.name,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Category ID: ${exercise.categoryId}", // TODO: Load actual category name
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedExercise?.let { onAddExercise(it) }
                },
                enabled = selectedExercise != null
            ) {
                Text("Add Exercise")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
