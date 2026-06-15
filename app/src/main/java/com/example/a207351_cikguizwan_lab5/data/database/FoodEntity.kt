package com.example.a207351_cikguizwan_lab5.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val barcode: String,
    val productName: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val timestamp: Long = System.currentTimeMillis()
)