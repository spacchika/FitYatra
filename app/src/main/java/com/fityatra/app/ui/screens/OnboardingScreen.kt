@file:OptIn(ExperimentalMaterial3Api::class)

package com.fityatra.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fityatra.app.ai.ParsedExercise
import com.fityatra.app.ai.ParsedPlan
import com.fityatra.app.viewmodel.OnboardingUiState
import com.fityatra.app.viewmodel.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    navController: NavHostController,
    viewModel: OnboardingViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is OnboardingUiState.Success) {
            navController.navigate("ai_coach") {
                popUpTo("onboarding") { inclusive = true }
            }
        }
    }

    when (uiState) {
        is OnboardingUiState.GeneratingPlan -> {
            PlanLoadingScreen("Your AI Coach is building your personalised plan…")
            return
        }
        is OnboardingUiState.SavingPlan -> {
            PlanLoadingScreen("Saving your plan…")
            return
        }
        is OnboardingUiState.PlanReview -> {
            val state = uiState as OnboardingUiState.PlanReview
            PlanReviewScreen(
                plan = state.plan,
                aiMessage = state.aiMessage,
                onAccept = { viewModel.acceptPlan() },
                onRegenerate = { viewModel.regeneratePlan() },
                onBack = { viewModel.resetError() }
            )
            return
        }
        else -> { /* fall through to the 6-step wizard */ }
    }

    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 6

    // --- Step 1: Body Metrics ---
    var ageText by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }
    var heightText by remember { mutableStateOf("") }

    // --- Step 2: Fitness Goal ---
    var selectedGoal by remember { mutableStateOf("") }
    val goals = listOf(
        "build_muscle" to "Build Muscle",
        "lose_weight" to "Lose Weight",
        "improve_endurance" to "Improve Endurance",
        "general_fitness" to "General Fitness",
        "increase_flexibility" to "Increase Flexibility"
    )

    // --- Step 3: Experience & Frequency ---
    var selectedExperience by remember { mutableStateOf("beginner") }
    var daysPerWeek by remember { mutableStateOf(4) }

    // --- Step 4: Health Conditions ---
    val predefinedConditions = listOf("None", "Back pain", "Knee pain", "Heart condition", "Diabetes", "Hypertension", "Shoulder injury")
    val selectedConditions = remember { mutableStateListOf<String>() }
    var otherCondition by remember { mutableStateOf("") }

    // --- Step 5: Workout Preferences & Equipment ---
    val preferences = listOf("strength", "cardio", "hiit", "yoga", "sports_specific")
    val preferenceLabels = mapOf(
        "strength" to "Strength Training",
        "cardio" to "Cardio",
        "hiit" to "HIIT",
        "yoga" to "Yoga / Flexibility",
        "sports_specific" to "Sports-Specific"
    )
    val selectedPrefs = remember { mutableStateListOf<String>() }

    val equipmentOptions = listOf(
        "barbell" to "Barbell & Plates",
        "dumbbell" to "Dumbbells",
        "cables" to "Cables / Machines",
        "pull_up_bar" to "Pull-up Bar",
        "resistance_bands" to "Resistance Bands",
        "bodyweight" to "No Equipment (Bodyweight)"
    )
    val selectedEquipment = remember { mutableStateListOf<String>() }

    // --- Step 6: Summary & Submit ---
    val errorText = (uiState as? OnboardingUiState.Error)?.message

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Up Your Profile") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))

            // Progress indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(totalSteps) { index ->
                    val filled = index < currentStep
                    LinearProgressIndicator(
                        progress = if (filled) 1f else 0f,
                        modifier = Modifier.weight(1f).height(4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
            Text(
                text = "Step $currentStep of $totalSteps",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(24.dp))

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { it } + fadeIn()) togetherWith
                                (slideOutHorizontally { -it } + fadeOut())
                    } else {
                        (slideInHorizontally { -it } + fadeIn()) togetherWith
                                (slideOutHorizontally { it } + fadeOut())
                    }
                },
                label = "onboarding_step"
            ) { step ->
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    when (step) {
                        1 -> BodyMetricsStep(
                            ageText = ageText, onAgeChange = { ageText = it },
                            weightText = weightText, onWeightChange = { weightText = it },
                            heightText = heightText, onHeightChange = { heightText = it }
                        )
                        2 -> GoalSelectionStep(
                            goals = goals,
                            selectedGoal = selectedGoal,
                            onGoalSelected = { selectedGoal = it }
                        )
                        3 -> ExperienceStep(
                            selectedExperience = selectedExperience,
                            onExperienceSelected = { selectedExperience = it },
                            daysPerWeek = daysPerWeek,
                            onDaysChanged = { daysPerWeek = it }
                        )
                        4 -> HealthConditionsStep(
                            predefinedConditions = predefinedConditions,
                            selectedConditions = selectedConditions,
                            otherCondition = otherCondition,
                            onOtherChange = { otherCondition = it }
                        )
                        5 -> PreferencesEquipmentStep(
                            preferences = preferences,
                            preferenceLabels = preferenceLabels,
                            selectedPrefs = selectedPrefs,
                            equipmentOptions = equipmentOptions,
                            selectedEquipment = selectedEquipment
                        )
                        6 -> SummaryStep(
                            age = ageText, weight = weightText, height = heightText,
                            goal = goals.firstOrNull { it.first == selectedGoal }?.second ?: selectedGoal,
                            experience = selectedExperience,
                            daysPerWeek = daysPerWeek,
                            conditions = buildConditionsList(selectedConditions, otherCondition),
                            preferences = selectedPrefs.joinToString(", "),
                            equipment = selectedEquipment.joinToString(", "),
                            errorText = errorText,
                            onRetry = { viewModel.resetError() }
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStep > 1) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back")
                    }
                }

                Button(
                    onClick = {
                        if (currentStep < totalSteps) {
                            currentStep++
                        } else {
                            val conditions = buildConditionsList(selectedConditions, otherCondition)
                            viewModel.requestPlanPreview(
                                age = ageText.toIntOrNull() ?: 25,
                                weightKg = weightText.toFloatOrNull() ?: 70f,
                                heightCm = heightText.toFloatOrNull() ?: 170f,
                                fitnessGoal = selectedGoal.ifBlank { "general_fitness" },
                                healthConditions = conditions,
                                workoutPreferences = selectedPrefs.joinToString("|"),
                                availableEquipment = if (selectedEquipment.isEmpty()) "bodyweight"
                                                     else selectedEquipment.joinToString("|"),
                                experienceLevel = selectedExperience,
                                daysPerWeek = daysPerWeek
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = when (currentStep) {
                        1 -> ageText.isNotBlank() && weightText.isNotBlank() && heightText.isNotBlank()
                        2 -> selectedGoal.isNotBlank()
                        else -> true
                    }
                ) {
                    Text(if (currentStep < totalSteps) "Next" else "Get My Plan")
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Loading screens ──────────────────────────────────────────────────────────

@Composable
private fun PlanLoadingScreen(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(64.dp))
            Text(message, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Text("This may take a moment.", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ── Plan review screen ───────────────────────────────────────────────────────

@Composable
private fun PlanReviewScreen(
    plan: ParsedPlan,
    aiMessage: String,
    onAccept: () -> Unit,
    onRegenerate: () -> Unit,
    onBack: () -> Unit
) {
    val dayGroups = plan.exercises.groupBy { it.dayOfWeek }.toSortedMap()
    val intro = aiMessage.substringBefore("[PLAN_START]").trim()
        .lines().filter { it.isNotBlank() }.take(5).joinToString("\n")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your AI Plan") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("← Edit") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onRegenerate,
                        modifier = Modifier.weight(1f)
                    ) { Text("Regenerate") }
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f)
                    ) { Text("Looks Good!") }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Plan name
            item {
                Text(
                    plan.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // AI intro text
            if (intro.isNotBlank()) {
                item {
                    Text(
                        intro,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Text(
                    "Review your plan below, then accept or ask for a new one.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // One card per training day
            items(dayGroups.entries.toList()) { (day, exercises) ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Day $day — ${dayName(day)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        HorizontalDivider()
                        exercises.forEach { exercise ->
                            ExerciseRow(exercise)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseRow(exercise: ParsedExercise) {
    val (chipColor, chipTextColor) = when (exercise.type.lowercase()) {
        "warmup"   -> MaterialTheme.colorScheme.tertiaryContainer to
                      MaterialTheme.colorScheme.onTertiaryContainer
        "cooldown" -> MaterialTheme.colorScheme.secondaryContainer to
                      MaterialTheme.colorScheme.onSecondaryContainer
        else       -> MaterialTheme.colorScheme.primaryContainer to
                      MaterialTheme.colorScheme.onPrimaryContainer
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            color = chipColor,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                exercise.type.uppercase(),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = chipTextColor
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(exercise.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            val detail = buildString {
                append("${exercise.sets}×${exercise.reps} reps")
                if (exercise.weightKg > 0) append(" · ${exercise.weightKg}kg")
                append(" · ${exercise.restSeconds}s rest")
            }
            Text(detail, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun dayName(dayOfWeek: Int) = when (dayOfWeek) {
    1 -> "Monday"; 2 -> "Tuesday"; 3 -> "Wednesday"; 4 -> "Thursday"
    5 -> "Friday"; 6 -> "Saturday"; 7 -> "Sunday"; else -> "Day $dayOfWeek"
}

// ── Wizard step composables ──────────────────────────────────────────────────

@Composable
private fun BodyMetricsStep(
    ageText: String, onAgeChange: (String) -> Unit,
    weightText: String, onWeightChange: (String) -> Unit,
    heightText: String, onHeightChange: (String) -> Unit
) {
    Text("Personal Info", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text("Tell us about yourself so we can tailor your plan.", style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(8.dp))

    OutlinedTextField(
        value = ageText,
        onValueChange = onAgeChange,
        label = { Text("Age") },
        suffix = { Text("years") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    OutlinedTextField(
        value = weightText,
        onValueChange = onWeightChange,
        label = { Text("Weight") },
        suffix = { Text("kg") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    OutlinedTextField(
        value = heightText,
        onValueChange = onHeightChange,
        label = { Text("Height") },
        suffix = { Text("cm") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
private fun GoalSelectionStep(
    goals: List<Pair<String, String>>,
    selectedGoal: String,
    onGoalSelected: (String) -> Unit
) {
    Text("Your Fitness Goal", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text("What do you primarily want to achieve?", style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(8.dp))

    goals.forEach { (key, label) ->
        OutlinedCard(
            onClick = { onGoalSelected(key) },
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(
                containerColor = if (selectedGoal == key)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = selectedGoal == key, onClick = { onGoalSelected(key) })
                Spacer(Modifier.width(12.dp))
                Text(label, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun ExperienceStep(
    selectedExperience: String,
    onExperienceSelected: (String) -> Unit,
    daysPerWeek: Int,
    onDaysChanged: (Int) -> Unit
) {
    Text("Experience & Frequency", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text("How experienced are you and how often can you train?", style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(8.dp))

    Text("Experience Level", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
    listOf("beginner" to "Beginner", "intermediate" to "Intermediate", "advanced" to "Advanced").forEach { (key, label) ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(selected = selectedExperience == key, onClick = { onExperienceSelected(key) })
            Text(label, modifier = Modifier.padding(start = 8.dp))
        }
    }

    Spacer(Modifier.height(16.dp))
    Text("Training Days per Week", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
    Text("$daysPerWeek days", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
    Slider(
        value = daysPerWeek.toFloat(),
        onValueChange = { onDaysChanged(it.toInt()) },
        valueRange = 2f..6f,
        steps = 3
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("2", style = MaterialTheme.typography.labelSmall)
        Text("6", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun HealthConditionsStep(
    predefinedConditions: List<String>,
    selectedConditions: MutableList<String>,
    otherCondition: String,
    onOtherChange: (String) -> Unit
) {
    Text("Health Conditions", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text("Any health considerations we should know about? (optional)", style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(8.dp))

    predefinedConditions.forEach { condition ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = selectedConditions.contains(condition),
                onCheckedChange = { checked ->
                    if (condition == "None") {
                        selectedConditions.clear()
                        if (checked) selectedConditions.add("None")
                    } else {
                        selectedConditions.remove("None")
                        if (checked) selectedConditions.add(condition)
                        else selectedConditions.remove(condition)
                    }
                }
            )
            Text(condition, modifier = Modifier.padding(start = 8.dp))
        }
    }

    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = otherCondition,
        onValueChange = onOtherChange,
        label = { Text("Other (describe briefly)") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 2,
        maxLines = 3
    )
}

@Composable
private fun PreferencesEquipmentStep(
    preferences: List<String>,
    preferenceLabels: Map<String, String>,
    selectedPrefs: MutableList<String>,
    equipmentOptions: List<Pair<String, String>>,
    selectedEquipment: MutableList<String>
) {
    Text("Preferences & Equipment", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text("What kind of training do you enjoy?", style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(8.dp))

    Text("Workout Style", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
    preferences.forEach { pref ->
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(
                checked = selectedPrefs.contains(pref),
                onCheckedChange = { checked ->
                    if (checked) selectedPrefs.add(pref) else selectedPrefs.remove(pref)
                }
            )
            Text(preferenceLabels[pref] ?: pref, modifier = Modifier.padding(start = 8.dp))
        }
    }

    Spacer(Modifier.height(16.dp))
    Text("Available Equipment", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
    equipmentOptions.forEach { (key, label) ->
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(
                checked = selectedEquipment.contains(key),
                onCheckedChange = { checked ->
                    if (key == "bodyweight") selectedEquipment.clear()
                    else selectedEquipment.remove("bodyweight")
                    if (checked) selectedEquipment.add(key) else selectedEquipment.remove(key)
                }
            )
            Text(label, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun SummaryStep(
    age: String, weight: String, height: String,
    goal: String, experience: String, daysPerWeek: Int,
    conditions: String, preferences: String, equipment: String,
    errorText: String?,
    onRetry: () -> Unit
) {
    Text("Review Your Profile", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text("Everything look correct? Tap 'Get My Plan' and your AI Coach will suggest a personalised workout plan for you to review.",
        style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(8.dp))

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryRow("Age", "$age years")
            SummaryRow("Weight", "$weight kg")
            SummaryRow("Height", "$height cm")
            SummaryRow("Goal", goal)
            SummaryRow("Experience", experience)
            SummaryRow("Training days", "$daysPerWeek days/week")
            if (conditions.isNotBlank()) SummaryRow("Health conditions", conditions)
            if (preferences.isNotBlank()) SummaryRow("Preferences", preferences)
            if (equipment.isNotBlank()) SummaryRow("Equipment", equipment)
        }
    }

    if (errorText != null) {
        Spacer(Modifier.height(8.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(
                    "Could not generate AI plan: $errorText",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                TextButton(onClick = onRetry) { Text("Retry") }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

private fun buildConditionsList(selected: List<String>, other: String): String {
    val conditions = selected.filter { it != "None" }.toMutableList()
    if (other.isNotBlank()) conditions.add(other.trim())
    return if (conditions.isEmpty()) "none" else conditions.joinToString("|")
}
