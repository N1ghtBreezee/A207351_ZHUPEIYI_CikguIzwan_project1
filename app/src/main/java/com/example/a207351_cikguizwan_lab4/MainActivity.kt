package com.example.a207351_cikguizwan_lab4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitnessAssistantTheme(darkTheme = false) {
                HealthAppWithNavigation()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthAppWithNavigation() {
    var selectedIndex by remember { mutableStateOf(0) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    val titles = listOf("Workout", "Challenges", "History", "Profile")

    // 获取 ViewModel
    val viewModel: UserViewModel = viewModel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Fitness Assistant",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                titles.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        label = {
                            Text(
                                text = title,
                                fontSize = 12.sp
                            )
                        },
                        icon = {
                            Text(
                                text = when (title) {
                                    "Workout" -> "💪"
                                    "Challenges" -> "🏆"
                                    "History" -> "📊"
                                    "Profile" -> "👤"
                                    else -> "•"
                                },
                                fontSize = 22.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedIndex) {
                0 -> TrainingScreen(viewModel)
                1 -> ChallengeScreen()
                2 -> HistoryScreen()
                3 -> ProfileScreen(viewModel)  // ⭐ 传入 viewModel
            }
        }
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Settings", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("⚙️ App Settings")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Notifications", fontSize = 14.sp)
                    Text("• Units", fontSize = 14.sp)
                    Text("• Privacy Policy", fontSize = 14.sp)
                    Text("• About", fontSize = 14.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun TrainingScreen(viewModel: UserViewModel) {
    // 从 ViewModel 读取数据
    val userData by viewModel.userData.collectAsState()

    var localUserName by remember { mutableStateOf(userData.userName) }
    var localFitnessGoal by remember { mutableStateOf(userData.fitnessGoal) }
    var greetingMessage by remember { mutableStateOf("") }
    var showGreeting by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "👋 Welcome to Fitness Assistant",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Matric No: A207351",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = localUserName,
                        onValueChange = { localUserName = it },
                        label = { Text("Enter your name") },
                        placeholder = { Text("e.g., John Doe") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = localFitnessGoal,
                        onValueChange = { localFitnessGoal = it },
                        label = { Text("Your Fitness Goal") },
                        placeholder = { Text("e.g., Lose weight, Gain muscle") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (localUserName.isNotBlank()) {
                                // ⭐ 保存到 ViewModel（跨屏幕共享）
                                viewModel.updateData(localUserName, localFitnessGoal)
                                greetingMessage = "Welcome back, $localUserName! 💪 Goal: $localFitnessGoal"
                                showGreeting = true
                            } else {
                                greetingMessage = "Please enter your name first! 😊"
                                showGreeting = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            "Save Profile",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (showGreeting && greetingMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.small,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = if (localUserName.isNotBlank()) "🎉" else "⚠️", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = greetingMessage,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // ⭐ 显示已保存的 Profile 信息
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("👤 Saved Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Name: ${userData.userName.ifEmpty { "Not set yet" }}", fontSize = 14.sp)
                    Text("Goal: ${userData.fitnessGoal.ifEmpty { "Not set yet" }}", fontSize = 14.sp)
                    Text("💡 Tip: Enter your name and goal above, then they will be saved across all screens!", fontSize = 12.sp)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Food Log", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Consumed 0 kcal", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NutritionItem("Protein (g)", "0")
                        NutritionItem("Carbs (g)", "0")
                        NutritionItem("Fat (g)", "0")
                        NutritionItem("Water (ml)", "0")
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MealButton(icon = "🍳", label = "Breakfast")
                MealButton(icon = "🍱", label = "Lunch")
                MealButton(icon = "🍲", label = "Dinner")
                MealButton(icon = "🍎", label = "Snack")
                MealButton(icon = "💧", label = "Water")
                MealButton(icon = "⚖️", label = "Log Weight")
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Text("Official Plans", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            ExpandableProgramsSection()
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Text("Shortcuts", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ShortcutCard("Men's Fat Loss\nShortcut", "Fix belly fat, small muscles, no shape!", MaterialTheme.colorScheme.primaryContainer)
                ShortcutCard("Women's Fat Loss\nShortcut", "Tighten full body lines, shape a balanced figure!", MaterialTheme.colorScheme.secondaryContainer)
            }
        }
    }
}

@Composable
fun ExpandableProgramsSection() {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📋 Training Plans (Click to Expand)",
                    fontWeight = FontWeight.Bold
                )
                Text(text = if (isExpanded) "▲" else "▼")
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProgramButton("Personal\nTemplate")
                    ProgramButton("Personal Plan\n(AI)")
                    ProgramButton("Fat Loss\nSculpt")
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProgramButton("Targeted\nAreas")
                    ProgramButton("Muscle Gain\n· Men")
                    ProgramButton("Muscle Gain\n· Women")
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProgramButton("Bodyweight\nTracking")
                    ProgramButton("Power-\nlifting")
                    ProgramButton("Old Plan")
                }
            }
        }
    }
}

@Composable
fun NutritionItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp)
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MealButton(icon: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Text(icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

@Composable
fun ProgramButton(label: String) {
    Card(
        modifier = Modifier.width(110.dp),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ShortcutCard(title: String, subtitle: String, containerColor: Color) {
    Card(
        modifier = Modifier.width(170.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(subtitle, fontSize = 11.sp)
        }
    }
}

@Composable
fun ChallengeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("🏆 Challenges", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🔥 Active Challenges", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("• 3 Workouts Per Week", fontSize = 14.sp)
                Text("• Drink 2L Water Daily", fontSize = 14.sp)
                Text("• Fat Loss Sprint Camp", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun HistoryScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("📊 History", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📅 Recent Workouts", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("2026-04-09  Chest Training  45 min", fontSize = 14.sp)
                Text("2026-04-08  Leg Training  50 min", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ProfileScreen(viewModel: UserViewModel) {
    // 从 ViewModel 读取用户数据
    val userData by viewModel.userData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text("👤", fontSize = 50.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ⭐ 显示从 ViewModel 读取的用户名
        Text(
            text = if (userData.userName.isNotEmpty()) userData.userName else "Fitness Enthusiast",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        // ⭐ 显示健身目标
        if (userData.fitnessGoal.isNotEmpty()) {
            Text(
                text = "Goal: ${userData.fitnessGoal}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Height: 175 cm", fontSize = 16.sp)
                Text("Weight: 72.5 kg", fontSize = 16.sp)

                // ⭐ 显示保存的用户信息
                if (userData.userName.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Saved Info:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Name: ${userData.userName}", fontSize = 14.sp)
                    Text("Goal: ${userData.fitnessGoal}", fontSize = 14.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    FitnessAssistantTheme(darkTheme = false) {
        HealthAppWithNavigation()
    }
}