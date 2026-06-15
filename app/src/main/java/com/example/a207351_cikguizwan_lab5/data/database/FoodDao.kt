package com.example.a207351_cikguizwan_lab5.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Insert
    suspend fun insertFood(food: FoodEntity)

    @Query("SELECT * FROM foods ORDER BY timestamp DESC")
    fun getAllFoods(): Flow<List<FoodEntity>>

    @Query("DELETE FROM foods")
    suspend fun deleteAll()
}