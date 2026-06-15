package com.example.a207351_cikguizwan_lab5.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a207351_cikguizwan_lab5.data.firebase.CommunityWorkout
import com.example.a207351_cikguizwan_lab5.data.firebase.FirestoreService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CommunityScreen(
    userName: String,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var workouts by remember { mutableStateOf<List<CommunityWorkout>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isSharing by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var selectedWorkoutType by remember { mutableStateOf("Cardio") }
    var duration by remember { mutableStateOf("30") }
    var calories by remember { mutableStateOf("200") }

    val workoutTypes = listOf("Cardio", "Strength", "Yoga", "HIIT", "Running", "Cycling")

    fun loadWorkouts() {
        scope.launch {
            isLoading = true
            val result = FirestoreService.getAllWorkouts()
            result.onSuccess { workouts = it }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadWorkouts() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) { Text("←", fontSize = 24.sp) }
                Text("🌍 Community Feed", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Button(
                    onClick = { showShareDialog = true },
                    shape = RoundedCornerShape(20.dp)
                ) { Text("➕ Share") }
            }
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                    Text("Loading from Firebase...", modifier = Modifier.padding(top = 80.dp))
                }
            }
            workouts.isEmpty() -> {
                Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🌍", fontSize = 64.sp)
                        Text("No workouts shared yet", fontSize = 16.sp)
                        Text("Be the first to share!", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(workouts) { workout ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = when (workout.workoutType) {
                                                "Cardio" -> "🏃"; "Strength" -> "💪"; "Yoga" -> "🧘"
                                                "HIIT" -> "⚡"; "Running" -> "🏃‍♂️"; "Cycling" -> "🚴"
                                                else -> "🏋️"
                                            },
                                            fontSize = 24.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(workout.userName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("${workout.workoutType} • ${workout.durationMinutes} min", fontSize = 14.sp)
                                    Text("🔥 ${workout.caloriesBurned} kcal burned", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(formatTimestamp(workout.timestamp), fontSize = 10.sp, color = Color.Gray)
                                    Text("☁️", fontSize = 20.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showShareDialog) {
        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            title = { Text("Share Your Workout", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("🏋️ Share your achievement!")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Workout Type", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        workoutTypes.forEach { option ->
                            FilterChip(
                                selected = selectedWorkoutType == option,
                                onClick = { selectedWorkoutType = option },
                                label = { Text(option, fontSize = 12.sp) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { if (it.all { c -> c.isDigit() }) duration = it },
                        label = { Text("Duration (minutes)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = calories,
                        onValueChange = { if (it.all { c -> c.isDigit() }) calories = it },
                        label = { Text("Calories Burned") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val workout = CommunityWorkout(
                            userName = userName.ifEmpty { "Anonymous" },
                            workoutType = selectedWorkoutType,
                            durationMinutes = duration.toIntOrNull() ?: 30,
                            caloriesBurned = calories.toIntOrNull() ?: 200
                        )
                        scope.launch {
                            isSharing = true
                            FirestoreService.addWorkout(workout)
                            isSharing = false
                            showShareDialog = false
                            loadWorkouts()
                            duration = "30"
                            calories = "200"
                        }
                    },
                    enabled = !isSharing
                ) {
                    if (isSharing) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    else Text("Share to Cloud ☁️")
                }
            },
            dismissButton = {
                TextButton(onClick = { showShareDialog = false }) { Text("Cancel") }
            }
        )
    }
}

fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    return format.format(date)
}