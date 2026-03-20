package com.fityatra.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fityatra.app.data.entities.WorkoutSession
import com.fityatra.app.viewmodel.WorkoutSessionViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavHostController,
    workoutSessionViewModel: WorkoutSessionViewModel
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showWorkoutDialog by remember { mutableStateOf(false) }
    var showDeloadDialog by remember { mutableStateOf(false) }
    
    // Fetch actual workout sessions from database
    val startOfMonth = remember(currentMonth) { 
        Date.from(currentMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant())
    }
    val endOfMonth = remember(currentMonth) { 
        Date.from(currentMonth.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant())
    }
    
    val workoutSessions by workoutSessionViewModel.getSessionsBetweenDates(startOfMonth, endOfMonth)
        .collectAsState(initial = emptyList())
    
    // Convert workout sessions to LocalDate set for calendar display
    val workoutDays = remember(workoutSessions) {
        workoutSessions.map { session ->
            session.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        }.toSet()
    }
    
    // Track deload days from sessions
    val deloadDays = remember(workoutSessions) {
        workoutSessions.filter { it.isDeload }.map { session ->
            session.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        }.toSet()
    }
    
    // Use actual deload days from database instead of generated ones
    val deloadWeeks = deloadDays
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Workout Calendar",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Month Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { currentMonth = currentMonth.minusMonths(1) }
            ) {
                Text("◀")
            }
            
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium
            )
            
            IconButton(
                onClick = { currentMonth = currentMonth.plusMonths(1) }
            ) {
                Text("▶")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar Grid
        CalendarGrid(
            currentMonth = currentMonth,
            workoutDays = workoutDays,
            deloadWeeks = deloadWeeks,
            selectedDate = selectedDate,
            onDateClick = { date ->
                selectedDate = date
                if (isDeloadWeek(date, deloadWeeks)) {
                    showDeloadDialog = true
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Legend
        CalendarLegend()
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { /* TODO: Mark current week as deload */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Mark Deload Week")
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }
        }
    }
    
    // Deload Week Dialog
    if (showDeloadDialog) {
        AlertDialog(
            onDismissRequest = { showDeloadDialog = false },
            title = { Text("Deload Week") },
            text = { 
                Text("This is a deload week. Reduce weights by 50-60% and focus on form and recovery.")
            },
            confirmButton = {
                TextButton(onClick = { showDeloadDialog = false }) {
                    Text("Got it")
                }
            }
        )
    }
}

@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    workoutDays: Set<LocalDate>,
    deloadWeeks: Set<LocalDate>,
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val lastDayOfMonth = currentMonth.atEndOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()
    
    // Create list of dates including padding days
    val calendarDates = mutableListOf<LocalDate?>()
    
    // Add padding days from previous month
    repeat(firstDayOfWeek) {
        calendarDates.add(null)
    }
    
    // Add days of current month
    for (day in 1..daysInMonth) {
        calendarDates.add(currentMonth.atDay(day))
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(calendarDates) { date ->
            CalendarDay(
                date = date,
                isWorkoutDay = date?.let { workoutDays.contains(it) } ?: false,
                isDeloadWeek = date?.let { isDeloadWeek(it, deloadWeeks) } ?: false,
                isSelected = date == selectedDate,
                onClick = { date?.let { onDateClick(it) } }
            )
        }
    }
}

@Composable
fun CalendarDay(
    date: LocalDate?,
    isWorkoutDay: Boolean,
    isDeloadWeek: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isDeloadWeek -> MaterialTheme.colorScheme.tertiary
        isWorkoutDay -> MaterialTheme.colorScheme.secondary
        else -> Color.Transparent
    }
    
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isDeloadWeek -> MaterialTheme.colorScheme.onTertiary
        isWorkoutDay -> MaterialTheme.colorScheme.onSecondary
        date == null -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(enabled = date != null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date?.dayOfMonth?.toString() ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun CalendarLegend() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Legend",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Workout Day", style = MaterialTheme.typography.bodySmall)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Deload Week", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// Helper functions
fun generateWorkoutDays(month: YearMonth): Set<LocalDate> {
    val workoutDays = mutableSetOf<LocalDate>()
    val startDate = month.atDay(1)
    val endDate = month.atEndOfMonth()
    
    // Generate workout days (Mon, Wed, Fri pattern)
    var current = startDate
    while (!current.isAfter(endDate)) {
        if (current.dayOfWeek.value in listOf(1, 3, 5)) { // Monday, Wednesday, Friday
            workoutDays.add(current)
        }
        current = current.plusDays(1)
    }
    
    return workoutDays
}

fun generateDeloadWeeks(month: YearMonth): Set<LocalDate> {
    val deloadDays = mutableSetOf<LocalDate>()
    val startDate = month.atDay(1)
    val endDate = month.atEndOfMonth()
    
    // Simulate deload weeks (every 8 weeks, roughly once every 2 months)
    val weeksSinceStart = ChronoUnit.WEEKS.between(LocalDate.of(2024, 1, 1), startDate)
    if (weeksSinceStart % 8L == 0L) {
        // First week of month is deload
        var current = startDate
        while (current.dayOfMonth <= 7 && !current.isAfter(endDate)) {
            deloadDays.add(current)
            current = current.plusDays(1)
        }
    }
    
    return deloadDays
}

fun isDeloadWeek(date: LocalDate, deloadWeeks: Set<LocalDate>): Boolean {
    return deloadWeeks.contains(date)
}
