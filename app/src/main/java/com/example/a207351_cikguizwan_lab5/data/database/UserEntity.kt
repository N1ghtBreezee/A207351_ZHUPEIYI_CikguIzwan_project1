package com.example.a207351_cikguizwan_lab5.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userName: String,
    val fitnessGoal: String
)