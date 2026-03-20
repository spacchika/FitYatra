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
import androidx.navigation.NavHostController
import com.fityatra.app.data.entities.WorkoutSet
import com.fityatra.app.viewmodel.WorkoutSessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSessionScreen(
    navController: NavHostController,
    viewModel: WorkoutSessionViewModel
) {
    val currentSession by viewModel.currentSession.collectAsState()
    val currentSets by viewModel.currentSets.collectAsState()
    val restTimer by viewModel.restTimer.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    
    var showAddSetDialog by remember { mutableStateOf(false) }
    var selectedExerciseId by remember { mutableStateOf(0L) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Workout Session",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                if (currentSession != null) {
                    Text(
                        text = if (currentSession!!.isDeload) "Deload Week" else "Regular Workout",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Rest Timer
        if (isTimerRunning) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rest Timer: ${restTimer}s",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { viewModel.stopRestTimer() }) {
                        Text("⏹️ Stop")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Current Sets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Current Sets",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            TextButton(
                onClick = { showAddSetDialog = true },
                modifier = Modifier.size(48.dp)
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Sets List
        LazyColumn {
            items(currentSets) { set ->
                WorkoutSetItem(
                    set = set,
                    onStartRest = { restSeconds ->
                        viewModel.startRestTimer(restSeconds)
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Finish Workout Button
        if (currentSession != null) {
            Button(
                onClick = {
                    viewModel.finishWorkoutSession()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Finish Workout")
            }
        } else {
            Button(
                onClick = {
                    viewModel.startWorkoutSession(planId = 1L) // TODO: Get actual plan ID
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Workout")
            }
        }
    }
    
    // Add Set Dialog
    if (showAddSetDialog) {
        AddSetDialog(
            onDismiss = { showAddSetDialog = false },
            onAddSet = { exerciseId, reps, weight, restSeconds ->
                viewModel.addWorkoutSet(exerciseId, reps, weight, restSeconds)
                showAddSetDialog = false
            }
        )
    }
}

@Composable
fun WorkoutSetItem(
    set: WorkoutSet,
    onStartRest: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Exercise ${set.exerciseId}", // TODO: Get actual exercise name
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Set ${set.setNumber}: ${set.reps} reps @ ${set.weight}kg",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (set.restSeconds > 0) {
                IconButton(
                    onClick = { onStartRest(set.restSeconds) }
                ) {
                    Text("▶️")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSetDialog(
    onDismiss: () -> Unit,
    onAddSet: (Long, Int, Double, Int) -> Unit
) {
    var exerciseId by remember { mutableStateOf("1") }
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var restSeconds by remember { mutableStateOf("90") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Set") },
        text = {
            Column {
                OutlinedTextField(
                    value = exerciseId,
                    onValueChange = { exerciseId = it },
                    label = { Text("Exercise ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Reps") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = restSeconds,
                    onValueChange = { restSeconds = it },
                    label = { Text("Rest (seconds)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val exerciseIdLong = exerciseId.toLongOrNull() ?: 1L
                    val repsInt = reps.toIntOrNull() ?: 0
                    val weightDouble = weight.toDoubleOrNull() ?: 0.0
                    val restInt = restSeconds.toIntOrNull() ?: 90
                    onAddSet(exerciseIdLong, repsInt, weightDouble, restInt)
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
