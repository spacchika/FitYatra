package com.fityatra.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fityatra.app.data.AppPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController, appPreferences: AppPreferences) {
    var deloadFrequency by remember { mutableStateOf(8) }
    var deloadWeightDrop by remember { mutableStateOf(50) }
    var defaultRestTimer by remember { mutableStateOf(90) }
    var apiKey by remember { mutableStateOf(appPreferences.claudeApiKey) }
    var apiKeySaved by remember { mutableStateOf(false) }
    
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // AI Coach Settings
        item {
            SettingsSection(title = "AI Coach") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Claude API Key",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "Required to enable AI coaching. Get your key at console.anthropic.com. Stored only on this device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = {
                            apiKey = it
                            apiKeySaved = false
                        },
                        label = { Text("API Key") },
                        placeholder = { Text("sk-ant-...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (apiKeySaved) {
                            Text(
                                "Saved!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        Button(
                            onClick = {
                                appPreferences.claudeApiKey = apiKey
                                apiKeySaved = true
                            }
                        ) {
                            Text("Save Key")
                        }
                    }
                }
            }
        }

        // Workout Settings
        item {
            SettingsSection(title = "Workout Settings") {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Deload Frequency
                    SettingSlider(
                        title = "Deload Frequency",
                        subtitle = "Every $deloadFrequency weeks",
                        value = deloadFrequency.toFloat(),
                        onValueChange = { deloadFrequency = it.toInt() },
                        valueRange = 6f..12f,
                        steps = 5
                    )
                    
                    // Deload Weight Drop
                    SettingSlider(
                        title = "Deload Weight Drop",
                        subtitle = "$deloadWeightDrop% reduction",
                        value = deloadWeightDrop.toFloat(),
                        onValueChange = { deloadWeightDrop = it.toInt() },
                        valueRange = 40f..70f,
                        steps = 5
                    )
                    
                    // Default Rest Timer
                    SettingSlider(
                        title = "Default Rest Timer",
                        subtitle = "$defaultRestTimer seconds",
                        value = defaultRestTimer.toFloat(),
                        onValueChange = { defaultRestTimer = it.toInt() },
                        valueRange = 30f..180f,
                        steps = 14
                    )
                }
            }
        }
        
        // Data Management
        item {
            SettingsSection(title = "Data Management") {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Export Data Button
                    OutlinedButton(
                        onClick = { showExportDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📤 Export Data to Local Storage")
                    }
                    
                    // Import Data Button
                    OutlinedButton(
                        onClick = { showImportDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📁 Import Data from Local Storage")
                    }

                }
            }
        }
        
        // App Info
        item {
            SettingsSection(title = "About") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "FitYatra v1.0.0",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Your personal fitness journey companion",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Back Button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }
    
    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Data") },
            text = { Text("Your workout data will be exported to Downloads folder as a JSON file.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // TODO: Implement export to local storage
                        showExportDialog = false
                    }
                ) {
                    Text("Export")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExportDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Data") },
            text = { Text("Select a JSON file from your device to import workout data. This will replace your current data.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // TODO: Implement import from local storage
                        showImportDialog = false
                    }
                ) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showImportDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun SettingSlider(
    title: String,
    subtitle: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps
        )
    }
}

@Composable
fun BackupDialog(
    onDismiss: () -> Unit,
    onBackup: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Backup Data") },
        text = {
            Column {
                Text("Choose backup location:")
                Spacer(modifier = Modifier.height(16.dp))
                
                listOf("Google Drive", "OneDrive", "Local Storage").forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = false,
                            onClick = { onBackup(option) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RestoreDialog(
    onDismiss: () -> Unit,
    onRestore: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restore Data") },
        text = {
            Column {
                Text("Choose restore source:")
                Spacer(modifier = Modifier.height(16.dp))
                
                listOf("Google Drive", "OneDrive", "Local Storage").forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = false,
                            onClick = { onRestore(option) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
