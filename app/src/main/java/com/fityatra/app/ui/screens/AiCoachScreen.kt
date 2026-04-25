package com.fityatra.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fityatra.app.ai.AiPromptBuilder
import com.fityatra.app.data.entities.AiCoachMessage
import com.fityatra.app.data.model.WellnessData
import com.fityatra.app.viewmodel.AiCoachViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiCoachScreen(
    navController: NavHostController,
    viewModel: AiCoachViewModel
) {
    val messages by viewModel.messages.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val activePlan by viewModel.activePlan.collectAsState()
    val wellnessData by viewModel.wellnessData.collectAsState()
    val healthConnectAvailable by viewModel.healthConnectAvailable.collectAsState()
    val todaysExercises by viewModel.todaysExercises.collectAsState()
    val exerciseNames by viewModel.exerciseNames.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Auto-scroll on new messages
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Show error in snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.dismissError()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AI Coach", fontWeight = FontWeight.Bold)
                        Text(
                            AiPromptBuilder.todayDateLabel(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            AiCoachBottomBar(navController = navController, currentRoute = "ai_coach")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── Today's Workout Card ──────────────────────────────────────
            TodaysWorkoutCard(
                activePlan = activePlan,
                todaysExercises = todaysExercises,
                exerciseNames = exerciseNames,
                onStartWorkout = { navController.navigate("workout_session") },
                onCreatePlan = { navController.navigate("workout_plans") }
            )

            // ── Wellness Banner (only when Health Connect data is available) ──
            if (healthConnectAvailable && wellnessData.isAvailable) {
                WellnessBanner(
                    wellnessData = wellnessData,
                    onRefresh = { viewModel.refreshWellnessData() }
                )
            }

            // ── Chat Messages ─────────────────────────────────────────────
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        WelcomeMessage(
                            userName = userProfile?.let { "there" } ?: "there",
                            hasPlan = activePlan != null
                        )
                    }
                }
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(message = msg)
                }
                if (isSending) {
                    item { TypingIndicator() }
                }
            }

            // ── Message Input ─────────────────────────────────────────────
            MessageInputBar(
                text = messageText,
                onTextChange = { messageText = it },
                isSending = isSending,
                onSend = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(messageText)
                        messageText = ""
                    }
                }
            )
        }
    }
}

// ─── Today's Workout Card ────────────────────────────────────────────────────

@Composable
private fun TodaysWorkoutCard(
    activePlan: com.fityatra.app.data.entities.WorkoutPlan?,
    todaysExercises: List<com.fityatra.app.data.entities.WorkoutPlanExercise>,
    exerciseNames: Map<Long, String>,
    onStartWorkout: () -> Unit,
    onCreatePlan: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Today's Workout",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (activePlan != null && todaysExercises.isNotEmpty()) {
                    FilledTonalButton(
                        onClick = onStartWorkout,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Start", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            if (activePlan == null) {
                Text(
                    "No active workout plan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onCreatePlan, modifier = Modifier.fillMaxWidth()) {
                    Text("Create a Plan")
                }
            } else if (todaysExercises.isEmpty()) {
                Text(
                    "${activePlan.name} — Rest day today",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            } else {
                Text(
                    activePlan.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(todaysExercises.sortedBy { it.orderInDay }) { ex ->
                        ExerciseChip(
                            name = exerciseNames[ex.exerciseId] ?: "Ex #${ex.exerciseId}",
                            sets = ex.sets,
                            reps = ex.reps,
                            type = ex.exerciseType
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseChip(name: String, sets: Int, reps: Int, type: String) {
    val containerColor = when (type) {
        "warmup" -> MaterialTheme.colorScheme.tertiaryContainer
        "cooldown" -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        modifier = Modifier.wrapContentSize()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
            Text("${sets}×$reps", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
    }
}

// ─── Wellness Banner ─────────────────────────────────────────────────────────

@Composable
private fun WellnessBanner(wellnessData: WellnessData, onRefresh: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Today's Wellness",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    wellnessData.hrv?.let {
                        WellnessMetric("HRV", "${it.toInt()} ms", interpretHrv(it))
                    }
                    wellnessData.restingHeartRate?.let {
                        WellnessMetric("Resting HR", "${it.toInt()} bpm", null)
                    }
                    wellnessData.sleepEfficiency?.let {
                        WellnessMetric("Sleep", "${it.toInt()}%", interpretSleep(it))
                    }
                    wellnessData.vo2Max?.let {
                        WellnessMetric("VO2 Max", String.format("%.1f", it), null)
                    }
                }
            }
            IconButton(onClick = onRefresh, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun WellnessMetric(label: String, value: String, status: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f))
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onTertiaryContainer)
        status?.let {
            Text(it, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary)
        }
    }
}

private fun interpretHrv(hrv: Float): String = when {
    hrv >= 60 -> "Good"
    hrv >= 40 -> "OK"
    else -> "Low"
}

private fun interpretSleep(efficiency: Float): String = when {
    efficiency >= 85 -> "Good"
    efficiency >= 75 -> "OK"
    else -> "Poor"
}

// ─── Chat Bubbles ────────────────────────────────────────────────────────────

@Composable
private fun WelcomeMessage(userName: String, hasPlan: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Hi $userName! I'm your AI Coach.",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                if (hasPlan) "Your workout plan is ready! You can ask me anything — adjust today's workout, check form tips, or ask about nutrition."
                else "Let's get started! You can ask me to create a workout plan, adjust your exercises, or answer any fitness questions.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ChatBubble(message: AiCoachMessage) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text("AI", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = if (isUser) 16.dp else 4.dp,
                topEnd = if (isUser) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            color = if (isUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (isUser) Spacer(Modifier.width(8.dp))
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text("AI", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    )
                }
            }
        }
    }
}

// ─── Message Input ────────────────────────────────────────────────────────────

@Composable
private fun MessageInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    isSending: Boolean,
    onSend: () -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Ask your AI coach...") },
                modifier = Modifier.weight(1f),
                maxLines = 4,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (text.isNotBlank() && !isSending) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    IconButton(onClick = onSend, enabled = text.isNotBlank()) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (text.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ─── Bottom Navigation ────────────────────────────────────────────────────────

@Composable
fun AiCoachBottomBar(navController: NavHostController, currentRoute: String) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "ai_coach",
            onClick = { if (currentRoute != "ai_coach") navController.navigate("ai_coach") },
            icon = { Icon(Icons.Default.SmartToy, contentDescription = null) },
            label = { Text("Coach") }
        )
        NavigationBarItem(
            selected = currentRoute == "workout_session",
            onClick = { navController.navigate("workout_session") },
            icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
            label = { Text("Workout") }
        )
        NavigationBarItem(
            selected = currentRoute == "workout_plans",
            onClick = { navController.navigate("workout_plans") },
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
            label = { Text("Plans") }
        )
        NavigationBarItem(
            selected = currentRoute == "progress",
            onClick = { navController.navigate("progress") },
            icon = { Icon(Icons.Default.TrendingUp, contentDescription = null) },
            label = { Text("Progress") }
        )
    }
}
