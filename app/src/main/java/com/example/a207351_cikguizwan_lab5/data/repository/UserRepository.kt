package com.example.a207351_cikguizwan_lab5.data.repository

import com.example.a207351_cikguizwan_lab5.data.database.UserDao
import com.example.a207351_cikguizwan_lab5.data.database.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    fun getLatestUser(): Flow<UserEntity?> = userDao.getLatestUser()

    suspend fun saveUser(userName: String, fitnessGoal: String) {
        userDao.insertUser(UserEntity(userName = userName, fitnessGoal = fitnessGoal))
    }

    suspend fun deleteAll() = userDao.deleteAll()
}