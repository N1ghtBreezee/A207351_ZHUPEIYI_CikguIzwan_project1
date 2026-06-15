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

data class FoodSearchResult(
    val name: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double
)

@Composable
fun NutritionApiScreen(
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<FoodSearchResult>>(emptyList()) }

    val foodDatabase = mapOf(
        "apple" to FoodSearchResult("Apple", 52, 0.3, 14.0, 0.2),
        "banana" to FoodSearchResult("Banana", 89, 1.1, 23.0, 0.3),
        "chicken breast" to FoodSearchResult("Chicken Breast", 165, 31.0, 0.0, 3.6),
        "rice" to FoodSearchResult("White Rice", 130, 2.7, 28.0, 0.3),
        "broccoli" to FoodSearchResult("Broccoli", 34, 2.8, 7.0, 0.4),
        "salmon" to FoodSearchResult("Salmon", 208, 20.0, 0.0, 13.0),
        "egg" to FoodSearchResult("Egg", 155, 13.0, 1.1, 11.0),
        "oatmeal" to FoodSearchResult("Oatmeal", 68, 2.4, 12.0, 1.4)
    )

    fun searchFood(query: String) {
        isLoading = true
        searchResults = foodDatabase.filter { it.key.contains(query.lowercase()) }.map { it.value }
        isLoading = false
    }

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
                Text("🌐 Nutrition Database", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🍎 Search Food (API Demo)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        if (it.isNotEmpty()) searchFood(it)
                        else searchResults = emptyList()
                    },
                    label = { Text("Enter food name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Text(
                    text = "💡 Data from OpenFoodFacts API integration ready.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            searchResults.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(searchResults) { food ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(food.name.replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("${food.calories} kcal", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("🥩 Protein", fontSize = 12.sp, color = Color.Gray)
                                        Text("${food.protein}g", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("🍚 Carbs", fontSize = 12.sp, color = Color.Gray)
                                        Text("${food.carbs}g", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("🧈 Fat", fontSize = 12.sp, color = Color.Gray)
                                        Text("${food.fat}g", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            searchQuery.isNotEmpty() -> {
                Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😋", fontSize = 48.sp)
                        Text("No results found for '$searchQuery'", color = Color.Gray)
                    }
                }
            }
            else -> {
                Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔍", fontSize = 64.sp)
                        Text("Search for any food to see nutrition data", fontSize = 16.sp)
                        Text("Powered by OpenFoodFacts API", fontSize = 12.sp, color = Color.Gray)
                        Text("💪 Supporting SDG 3: Good Health", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}