package com.example.a207351_cikguizwan_lab5.utils

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BarcodeScannerView(
    onBarcodeDetected: (String) -> Unit
) {
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(false) }

    // 模拟条码扫描（演示用）
    // 实际项目中可以使用 ZXing 或其他库
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📷 Camera Scanner",
            fontSize = 32.sp,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "For demo purposes, please enter a barcode number:",
            fontSize = 14.sp,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center
        )

        // 演示用的条码输入
        var inputBarcode by remember { mutableStateOf("") }

        androidx.compose.material3.OutlinedTextField(
            value = inputBarcode,
            onValueChange = { inputBarcode = it },
            label = { Text("Enter Barcode Number") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )

        Button(
            onClick = {
                if (inputBarcode.isNotEmpty()) {
                    onBarcodeDetected(inputBarcode)
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Scan Barcode (Demo)")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "💡 Note: Full camera integration requires additional setup.\nIn the complete app, scanning a real barcode will fetch product data from OpenFoodFacts API.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}