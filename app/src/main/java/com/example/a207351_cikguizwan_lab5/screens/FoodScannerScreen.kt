package com.example.a207351_cikguizwan_lab5.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a207351_cikguizwan_lab5.data.api.RetrofitInstance
import com.example.a207351_cikguizwan_lab5.data.api.ProductResponse
import com.example.a207351_cikguizwan_lab5.data.database.FoodEntity
import com.example.a207351_cikguizwan_lab5.data.database.UserDatabase
import com.example.a207351_cikguizwan_lab5.utils.BarcodeScannerView
import com.google.accompanist.permissions.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FoodScannerScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { UserDatabase.getDatabase(context) }
    val foodDao = remember { database.foodDao() }

    var scannedBarcode by remember { mutableStateOf<String?>(null) }
    var scannedProduct by remember { mutableStateOf<ProductResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var savedFoods by remember { mutableStateOf<List<FoodEntity>>(emptyList()) }
    var showScanner by remember { mutableStateOf(true) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        foodDao.getAllFoods().collect { foods ->
            savedFoods = foods
        }
    }

    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
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
                Text("📷 Food Scanner", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        when {
            !cameraPermissionState.status.isGranted -> {
                PermissionDeniedScreen { cameraPermissionState.launchPermissionRequest() }
            }
            showScanner && scannedBarcode == null -> {
                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    BarcodeScannerView { barcode ->
                        scannedBarcode = barcode
                        showScanner = false
                        isLoading = true
                        scope.launch {
                            try {
                                val response = RetrofitInstance.api.getProduct(barcode)
                                scannedProduct = response
                                if (response.product == null) {
                                    Toast.makeText(context, "Product not found", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "API Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                    Text(
                        text = "📸 Position barcode in center",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(12.dp),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
            isLoading -> {
                Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                    Text("Fetching product data from API...", modifier = Modifier.padding(top = 80.dp))
                }
            }
            scannedProduct != null -> {
                ProductDetailScreen(
                    product = scannedProduct!!,
                    onSave = {
                        scope.launch {
                            val product = scannedProduct!!.product
                            val food = FoodEntity(
                                barcode = scannedBarcode ?: "",
                                productName = product?.product_name ?: "Unknown",
                                calories = product?.nutriments?.energy_kcal_100g?.toInt() ?: 0,
                                protein = product?.nutriments?.proteins_100g ?: 0.0,
                                carbs = product?.nutriments?.carbohydrates_100g ?: 0.0,
                                fat = product?.nutriments?.fat_100g ?: 0.0
                            )
                            foodDao.insertFood(food)
                            Toast.makeText(context, "Food saved to your pantry!", Toast.LENGTH_SHORT).show()
                            scannedBarcode = null
                            scannedProduct = null
                            showScanner = true
                        }
                    },
                    onRescan = {
                        scannedBarcode = null
                        scannedProduct = null
                        showScanner = true
                    }
                )
            }
        }

        if (savedFoods.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "📦 My Pantry (${savedFoods.size} items)",
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                LazyColumn {
                    items(savedFoods) { food ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(food.productName, fontWeight = FontWeight.Bold)
                                    Text("${food.calories} kcal | P:${food.protein}g C:${food.carbs}g F:${food.fat}g", fontSize = 12.sp)
                                }
                                Text("📱", fontSize = 20.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionDeniedScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📷 Camera permission required", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Grant Permission") }
    }
}

@Composable
fun ProductDetailScreen(
    product: ProductResponse,
    onSave: () -> Unit,
    onRescan: () -> Unit
) {
    val productInfo = product.product
    val nutriments = productInfo?.nutriments

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("🔍 Product Found!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(productInfo?.product_name ?: "Unknown Product", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Barcode: ${product.code}", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Text("📊 Nutrition Facts (per 100g)", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("🔥 Calories:"); Text("${nutriments?.energy_kcal_100g?.toInt() ?: 0} kcal", fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("🥩 Protein:"); Text("${nutriments?.proteins_100g ?: 0}g")
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("🍚 Carbs:"); Text("${nutriments?.carbohydrates_100g ?: 0}g")
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("🧈 Fat:"); Text("${nutriments?.fat_100g ?: 0}g")
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onRescan, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                Text("🔄 Scan Again")
            }
            Button(onClick = onSave, modifier = Modifier.weight(1f)) {
                Text("💾 Save to Pantry")
            }
        }
    }
}