package com.example.a207351_cikguizwan_lab4

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {
    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData

    fun updateData(name: String, goal: String) {
        _userData.value = UserData(name, goal)
    }
}
