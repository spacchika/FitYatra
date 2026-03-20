package com.fityatra.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to FitYatra!", style = MaterialTheme.typography.headlineMedium)
        Text("Your Fitness Journey Starts Here", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(32.dp))
        
        Button(
            onClick = { navController.navigate("workout_session") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Workout")
        }
        Spacer(Modifier.height(12.dp))
        
        Button(
            onClick = { navController.navigate("workout_plans") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Workout Plans")
        }
        Spacer(Modifier.height(12.dp))
        
        Button(
            onClick = { navController.navigate("exercises") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Exercise Library")
        }
        Spacer(Modifier.height(12.dp))
        
        Button(
            onClick = { navController.navigate("calendar") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calendar")
        }
        Spacer(Modifier.height(12.dp))
        
        Button(
            onClick = { navController.navigate("progress") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Progress & Stats")
        }
        Spacer(Modifier.height(12.dp))
        
        OutlinedButton(
            onClick = { navController.navigate("settings") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Settings")
        }
    }
}
