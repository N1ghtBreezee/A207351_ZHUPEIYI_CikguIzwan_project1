package com.example.a207351_cikguizwan_lab5.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users ORDER BY id DESC LIMIT 1")
    fun getLatestUser(): Flow<UserEntity?>

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}