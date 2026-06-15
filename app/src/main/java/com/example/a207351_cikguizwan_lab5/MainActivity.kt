package com.example.a207351_cikguizwan_lab5

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a207351_cikguizwan_lab5.data.database.UserDatabase
import com.example.a207351_cikguizwan_lab5.data.repository.UserRepository
import com.example.a207351_cikguizwan_lab5.ui.screens.*
import com.example.a207351_cikguizwan_lab5.ui.theme.A207351_cikguizwan_lab5Theme  // ← 注意这里是 ui.theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            A207351_cikguizwan_lab5Theme(darkTheme = false) {
                HealthAppWithNavigation()
            }
        }
    }
}

// ... 其余代码保持不变

// 屏幕导航枚举
sealed class AppScreen {
    object MainTabs : AppScreen()
    object FoodScanner : AppScreen()
    object NutritionApi : AppScreen()
    object Community : AppScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthAppWithNavigation() {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.MainTabs) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val titles = listOf("Workout", "Challenges", "History", "Profile", "Scanner", "Nutrition", "Community")

    val context = LocalContext.current
    val database = remember { UserDatabase.getDatabase(context) }
    val repository = remember { UserRepository(database.userDao()) }
    val viewModel: UserViewModel = viewModel(factory = UserViewModelFactory(repository))

    val userData by viewModel.userData.collectAsState()

    when (currentScreen) {
        is AppScreen.MainTabs -> {
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
                            .height(75.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    ) {
                        titles.forEachIndexed { index, title ->
                            NavigationBarItem(
                                selected = selectedTabIndex == index,
                                onClick = {
                                    when (index) {
                                        4 -> currentScreen = AppScreen.FoodScanner
                                        5 -> currentScreen = AppScreen.NutritionApi
                                        6 -> currentScreen = AppScreen.Community
                                        else -> {
                                            selectedTabIndex = index
                                            currentScreen = AppScreen.MainTabs
                                        }
                                    }
                                },
                                label = {
                                    Text(
                                        text = title,
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                },
                                icon = {
                                    Text(
                                        text = when (title) {
                                            "Workout" -> "💪"
                                            "Challenges" -> "🏆"
                                            "History" -> "📊"
                                            "Profile" -> "👤"
                                            "Scanner" -> "📷"
                                            "Nutrition" -> "🌐"
                                            "Community" -> "🌍"
                                            else -> "•"
                                        },
                                        fontSize = 22.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent,
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary
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
                    when (selectedTabIndex) {
                        0 -> TrainingScreen(viewModel)
                        1 -> ChallengeScreen()
                        2 -> HistoryScreen()
                        3 -> ProfileScreen(viewModel)
                        else -> TrainingScreen(viewModel)
                    }
                }
            }
        }

        is AppScreen.FoodScanner -> {
            FoodScannerScreen(
                onNavigateBack = { currentScreen = AppScreen.MainTabs }
            )
        }

        is AppScreen.NutritionApi -> {
            NutritionApiScreen(
                onNavigateBack = { currentScreen = AppScreen.MainTabs }
            )
        }

        is AppScreen.Community -> {
            CommunityScreen(
                userName = userData.userName,
                onNavigateBack = { currentScreen = AppScreen.MainTabs }
            )
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
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🤖 Features Added:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("• Camera Barcode Scanner", fontSize = 11.sp)
                    Text("• OpenFoodFacts API Integration", fontSize = 11.sp)
                    Text("• Firebase Cloud Storage", fontSize = 11.sp)
                    Text("• Room Local Database", fontSize = 11.sp)
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
                    Text("👋 Welcome to Fitness Assistant", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Matric No: A207351", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = localUserName,
                        onValueChange = { localUserName = it },
                        label = { Text("Enter your name") },
                        placeholder = { Text("e.g., John Doe") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = localFitnessGoal,
                        onValueChange = { localFitnessGoal = it },
                        label = { Text("Your Fitness Goal") },
                        placeholder = { Text("e.g., Lose weight, Gain muscle") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (localUserName.isNotBlank()) {
                                viewModel.updateData(localUserName, localFitnessGoal)
                                greetingMessage = "Welcome back, $localUserName! 💪 Goal: $localFitnessGoal"
                                showGreeting = true
                            } else {
                                greetingMessage = "Please enter your name first! 😊"
                                showGreeting = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    if (showGreeting && greetingMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = if (localUserName.isNotBlank()) "🎉" else "⚠️", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = greetingMessage, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
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
                    Text("👤 Saved Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Name: ${userData.userName.ifEmpty { "Not set yet" }}", fontSize = 14.sp)
                    Text("Goal: ${userData.fitnessGoal.ifEmpty { "Not set yet" }}", fontSize = 14.sp)
                    Text("💡 Tip: Enter your name and goal above, then they will be saved across all screens and survive app restarts!", fontSize = 12.sp)
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
                        Text("🥗 Today's Nutrition", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Track your food intake", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NutritionItem("🔥 kcal", "0")
                        NutritionItem("🥩 Protein", "0g")
                        NutritionItem("🍚 Carbs", "0g")
                        NutritionItem("🧈 Fat", "0g")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "✨ Tip: Use 'Scanner' tab to scan food barcodes and track nutrition!",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MealButton("🍳", "Breakfast")
                MealButton("🍱", "Lunch")
                MealButton("🍲", "Dinner")
                MealButton("🍎", "Snack")
                MealButton("💧", "Water")
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { Text("Official Plans", fontSize = 16.sp, fontWeight = FontWeight.Medium) }
        item { Spacer(modifier = Modifier.height(12.dp)) }
        item { ExpandableProgramsSection() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { Text("Shortcuts", fontSize = 16.sp, fontWeight = FontWeight.Medium) }
        item { Spacer(modifier = Modifier.height(12.dp)) }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ShortcutCard("Men's Fat Loss\nShortcut", "Fix belly fat, small muscles, no shape!", MaterialTheme.colorScheme.primaryContainer)
                ShortcutCard("Women's Fat Loss\nShortcut", "Tighten full body lines, shape a balanced figure!", MaterialTheme.colorScheme.secondaryContainer)
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📱 App Features Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("✅ Room Database - Profile saved locally", fontSize = 11.sp)
                    Text("✅ Camera Scanner - Scan food barcodes", fontSize = 11.sp)
                    Text("✅ OpenFoodFacts API - Get nutrition data", fontSize = 11.sp)
                    Text("✅ Firebase Firestore - Share community workouts", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun ExpandableProgramsSection() {
    var isExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📋 Training Plans (Click to Expand)", fontWeight = FontWeight.Bold)
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
        Box(modifier = Modifier.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
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
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("🏆 Challenges", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🔥 Active Challenges", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("• 3 Workouts Per Week", fontSize = 14.sp)
                Text("• Drink 2L Water Daily", fontSize = 14.sp)
                Text("• Scan 5 Food Items", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                Text("• Share 1 Workout on Community", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun HistoryScreen() {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("📊 History", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📅 Recent Workouts", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("2026-06-14  Chest Training  45 min", fontSize = 14.sp)
                Text("2026-06-13  Leg Training  50 min", fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📷 Scanned Foods", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Use 'Scanner' tab to scan barcodes and save foods!", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun ProfileScreen(viewModel: UserViewModel) {
    val userData by viewModel.userData.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Box(
            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(50.dp)).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) { Text("👤", fontSize = 50.sp) }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = if (userData.userName.isNotEmpty()) userData.userName else "Fitness Enthusiast", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        if (userData.fitnessGoal.isNotEmpty()) {
            Text(text = "Goal: ${userData.fitnessGoal}", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Height: 175 cm", fontSize = 16.sp)
                Text("Weight: 72.5 kg", fontSize = 16.sp)
                if (userData.userName.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Saved Info:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Name: ${userData.userName}", fontSize = 14.sp)
                    Text("Goal: ${userData.fitnessGoal}", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                Text("🏆 Achievements", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("• Saved Profile to Room DB", fontSize = 12.sp)
                Text("• Ready to Scan Barcodes", fontSize = 12.sp)
                Text("• Ready to Share Workouts", fontSize = 12.sp)
            }
        }
    }
}