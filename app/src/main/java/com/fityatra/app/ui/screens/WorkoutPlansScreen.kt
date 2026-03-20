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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fityatra.app.data.entities.WorkoutPlan
import com.fityatra.app.viewmodel.WorkoutPlanViewModel
import com.fityatra.app.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPlansScreen(
    navController: NavHostController,
    viewModel: WorkoutPlanViewModel
) {
    val workoutPlans by viewModel.workoutPlans.collectAsState()
    val activePlan by viewModel.activePlan.collectAsState()
    val isCreatingPlan by viewModel.isCreatingPlan.collectAsState()
    
    var showCreatePlanDialog by remember { mutableStateOf(false) }
    var editingPlan by remember { mutableStateOf<WorkoutPlan?>(null) }
    
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
            Text(
                text = "Workout Plans",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            FloatingActionButton(
                onClick = { showCreatePlanDialog = true },
                modifier = Modifier.size(48.dp)
            ) {
                Text("+")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Active Plan Card
        activePlan?.let { plan ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Active Plan",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = plan.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${plan.daysPerWeek} days per week",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (plan.description.isNotEmpty()) {
                        Text(
                            text = plan.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // All Plans
        Text(
            text = "All Plans",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(workoutPlans) { plan ->
                WorkoutPlanItem(
                    plan = plan,
                    isActive = plan.id == activePlan?.id,
                    onSetActive = { viewModel.setActivePlan(plan.id) },
                    onEdit = { 
                        editingPlan = plan
                    },
                    onDelete = { viewModel.deletePlan(plan) }
                )
            }
        }
    }
    
    // Create Plan Dialog
    if (showCreatePlanDialog) {
        CreatePlanDialog(
            isLoading = isCreatingPlan,
            onDismiss = { showCreatePlanDialog = false },
            onCreatePlan = { name, description, daysPerWeek, goal, exercises, startDate ->
                // Create the plan with enhanced data
                viewModel.createEnhancedPlan(name, description, daysPerWeek, goal, exercises, startDate)
                showCreatePlanDialog = false
            }
        )
    }
    
    // Edit Workout Plan Dialog (Simplified to prevent crashes)
    editingPlan?.let { plan ->
        EditWorkoutPlanDialog(
            workoutPlan = plan,
            onDismiss = { editingPlan = null },
            onSave = { updatedPlan ->
                viewModel.updateWorkoutPlan(updatedPlan)
                editingPlan = null
            },
            onDelete = {
                viewModel.deletePlan(plan)
                editingPlan = null
            }
        )
    }
}

@Composable
fun WorkoutPlanItem(
    plan: WorkoutPlan,
    isActive: Boolean,
    onSetActive: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (isActive) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = plan.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${plan.daysPerWeek} days per week",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (plan.description.isNotEmpty()) {
                    Text(
                        text = plan.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Row {
                IconButton(onClick = onEdit) {
                    Text("✏️", color = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { /* TODO: Navigate to WorkoutPlanDetailScreen */ }) {
                    Text("👁️", color = MaterialTheme.colorScheme.secondary)
                }
                if (!isActive) {
                    IconButton(onClick = onSetActive) {
                        Text("✓", color = MaterialTheme.colorScheme.primary)
                    }
                }
                IconButton(onClick = onDelete) {
                    Text("🗑️", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

// Fitness Goals Enum
enum class FitnessGoal(val displayName: String) {
    BULK("Build Muscle (Bulk)"),
    CUT("Lose Weight (Cut)"),
    MAINTAIN("Maintain Weight")
}

// Exercise Suggestion Data
data class ExerciseSuggestion(
    val name: String,
    val category: String,
    val isSelected: Boolean = true
)

// Generate exercise suggestions based on fitness goal and frequency
fun generateExerciseSuggestions(goal: FitnessGoal, daysPerWeek: Int): List<ExerciseSuggestion> {
    val baseExercises = when (goal) {
        FitnessGoal.BULK -> listOf(
            ExerciseSuggestion("Bench Press", "Chest"),
            ExerciseSuggestion("Squats", "Legs"),
            ExerciseSuggestion("Deadlifts", "Back"),
            ExerciseSuggestion("Overhead Press", "Shoulders"),
            ExerciseSuggestion("Barbell Rows", "Back"),
            ExerciseSuggestion("Pull-ups", "Back"),
            ExerciseSuggestion("Dips", "Chest"),
            ExerciseSuggestion("Bulgarian Split Squats", "Legs"),
            ExerciseSuggestion("Incline Dumbbell Press", "Chest"),
            ExerciseSuggestion("Lateral Raises", "Shoulders")
        )
        FitnessGoal.CUT -> listOf(
            ExerciseSuggestion("Burpees", "Cardio"),
            ExerciseSuggestion("Mountain Climbers", "Cardio"),
            ExerciseSuggestion("Jump Squats", "Legs"),
            ExerciseSuggestion("Push-ups", "Chest"),
            ExerciseSuggestion("Plank", "Core"),
            ExerciseSuggestion("High Knees", "Cardio"),
            ExerciseSuggestion("Lunges", "Legs"),
            ExerciseSuggestion("Russian Twists", "Core"),
            ExerciseSuggestion("Jumping Jacks", "Cardio"),
            ExerciseSuggestion("Bicycle Crunches", "Core")
        )
        FitnessGoal.MAINTAIN -> listOf(
            ExerciseSuggestion("Bench Press", "Chest"),
            ExerciseSuggestion("Squats", "Legs"),
            ExerciseSuggestion("Deadlifts", "Back"),
            ExerciseSuggestion("Push-ups", "Chest"),
            ExerciseSuggestion("Pull-ups", "Back"),
            ExerciseSuggestion("Plank", "Core"),
            ExerciseSuggestion("Lunges", "Legs"),
            ExerciseSuggestion("Overhead Press", "Shoulders"),
            ExerciseSuggestion("Dumbbell Rows", "Back"),
            ExerciseSuggestion("Leg Press", "Legs")
        )
    }
    
    // Adjust number of exercises based on frequency
    val exerciseCount = when (daysPerWeek) {
        3 -> 6
        4 -> 8
        5 -> 10
        6 -> 12
        else -> 6
    }
    
    return baseExercises.take(exerciseCount)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlanDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onCreatePlan: (String, String, Int, FitnessGoal, List<ExerciseSuggestion>, String) -> Unit
) {
    var currentStep by remember { mutableStateOf(1) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var daysPerWeek by remember { mutableStateOf("3") }
    var fitnessGoal by remember { mutableStateOf(FitnessGoal.BULK) }
    var startDate by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var goalExpanded by remember { mutableStateOf(false) }
    
    val dayOptions = listOf("3", "4", "5", "6")
    
    // Generate exercise suggestions based on goal
    val exerciseSuggestions = remember(fitnessGoal, daysPerWeek) {
        generateExerciseSuggestions(fitnessGoal, daysPerWeek.toIntOrNull() ?: 3)
    }
    
    var selectedExercises by remember(exerciseSuggestions) {
        mutableStateOf(exerciseSuggestions.toMutableList())
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Workout Plan - Step $currentStep of 3") },
        text = {
            Column {
                when (currentStep) {
                    1 -> {
                        // Step 1: Basic Info
                        Text(
                            text = "Basic Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Plan Name") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            enabled = !isLoading
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded && !isLoading }
                        ) {
                            OutlinedTextField(
                                value = "$daysPerWeek days per week",
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Frequency") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                enabled = !isLoading
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                dayOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text("$option days per week") },
                                        onClick = {
                                            daysPerWeek = option
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        ExposedDropdownMenuBox(
                            expanded = goalExpanded,
                            onExpandedChange = { goalExpanded = !goalExpanded && !isLoading }
                        ) {
                            OutlinedTextField(
                                value = fitnessGoal.displayName,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Fitness Goal") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                enabled = !isLoading
                            )
                            ExposedDropdownMenu(
                                expanded = goalExpanded,
                                onDismissRequest = { goalExpanded = false }
                            ) {
                                FitnessGoal.values().forEach { goal ->
                                    DropdownMenuItem(
                                        text = { Text(goal.displayName) },
                                        onClick = {
                                            fitnessGoal = goal
                                            goalExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    2 -> {
                        // Step 2: Exercise Selection
                        Text(
                            text = "Suggested Exercises",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Based on your ${fitnessGoal.displayName} goal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LazyColumn(
                            modifier = Modifier.height(300.dp)
                        ) {
                            items(selectedExercises.size) { index ->
                                val exercise = selectedExercises[index]
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = exercise.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = exercise.category,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row {
                                        Checkbox(
                                            checked = exercise.isSelected,
                                            onCheckedChange = { checked ->
                                                selectedExercises = selectedExercises.toMutableList().apply {
                                                    this[index] = exercise.copy(isSelected = checked)
                                                }
                                            }
                                        )
                                        TextButton(
                                            onClick = {
                                                selectedExercises = selectedExercises.toMutableList().apply {
                                                    removeAt(index)
                                                }
                                            }
                                        ) {
                                            Text("🗑️")
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                    
                    3 -> {
                        // Step 3: Schedule
                        Text(
                            text = "Schedule Your Plan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = { Text("Start Date (YYYY-MM-DD)") },
                            placeholder = { Text("2024-01-15") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "📅 Schedule Preview",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• ${selectedExercises.count { it.isSelected }} exercises selected",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "• $daysPerWeek workout days per week",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "• Deload weeks every 6-8 weeks",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "• Goal: ${fitnessGoal.displayName}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when (currentStep) {
                        1 -> {
                            if (name.isNotBlank()) {
                                currentStep = 2
                            }
                        }
                        2 -> {
                            currentStep = 3
                        }
                        3 -> {
                            if (name.isNotBlank() && startDate.isNotBlank()) {
                                onCreatePlan(
                                    name, 
                                    description, 
                                    daysPerWeek.toIntOrNull() ?: 3,
                                    fitnessGoal,
                                    selectedExercises.filter { it.isSelected },
                                    startDate
                                )
                            }
                        }
                    }
                },
                enabled = !isLoading && when (currentStep) {
                    1 -> name.isNotBlank()
                    2 -> selectedExercises.any { it.isSelected }
                    3 -> name.isNotBlank() && startDate.isNotBlank()
                    else -> false
                }
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text(
                        when (currentStep) {
                            1, 2 -> "Next"
                            3 -> "Create Plan"
                            else -> "Next"
                        }
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    if (currentStep > 1) {
                        currentStep--
                    } else {
                        onDismiss()
                    }
                },
                enabled = !isLoading
            ) {
                Text(
                    if (currentStep > 1) "Back" else "Cancel"
                )
            }
        }
    )
}

@Composable
fun EditWorkoutPlanDialog(
    workoutPlan: WorkoutPlan,
    onDismiss: () -> Unit,
    onSave: (WorkoutPlan) -> Unit,
    onDelete: () -> Unit
) {
    var editedName by remember { mutableStateOf(workoutPlan.name) }
    var editedDescription by remember { mutableStateOf(workoutPlan.description) }
    var editedDaysPerWeek by remember { mutableStateOf(workoutPlan.daysPerWeek.toString()) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Workout Plan") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Plan Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = editedDescription,
                    onValueChange = { editedDescription = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                OutlinedTextField(
                    value = editedDaysPerWeek,
                    onValueChange = { editedDaysPerWeek = it },
                    label = { Text("Days per Week") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Button(
                    onClick = {
                        // Navigate to day-wise editor
                        // For now, we'll add this as a placeholder
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📅 Edit Day-wise Exercises")
                }
                
                Text(
                    text = "Use the button above for detailed day-wise exercise editing.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = { showDeleteConfirmation = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("🗑️ Delete")
                }
                
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                
                Button(
                    onClick = {
                        val updatedPlan = workoutPlan.copy(
                            name = editedName,
                            description = editedDescription,
                            daysPerWeek = editedDaysPerWeek.toIntOrNull() ?: workoutPlan.daysPerWeek
                        )
                        onSave(updatedPlan)
                    }
                ) {
                    Text("✓ Save")
                }
            }
        },
        dismissButton = null
    )
    
    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Workout Plan") },
            text = { Text("Are you sure you want to delete \"${workoutPlan.name}\"? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
