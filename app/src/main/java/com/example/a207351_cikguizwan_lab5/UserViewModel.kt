package com.example.a207351_cikguizwan_lab5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a207351_cikguizwan_lab5.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getLatestUser().collect { entity ->
                _userData.value = UserData(
                    userName = entity?.userName ?: "",
                    fitnessGoal = entity?.fitnessGoal ?: ""
                )
            }
        }
    }

    fun updateData(name: String, goal: String) {
        viewModelScope.launch {
            repository.saveUser(name, goal)
            _userData.value = UserData(name, goal)
        }
    }
}