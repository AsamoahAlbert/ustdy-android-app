package com.example.ustdytake2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ustdytake2.model.GamificationData
import com.example.ustdytake2.repository.GamificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GamificationViewModel(
    private val repo: GamificationRepository = GamificationRepository()
) : ViewModel() {

    private val _gamification = MutableStateFlow(GamificationData())
    val gamification: StateFlow<GamificationData> = _gamification

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadGamification(userId: String) {
        viewModelScope.launch {
            val result = repo.getGamification(userId)
            result.fold(
                onSuccess = { _gamification.value = it },
                onFailure = {
                    _gamification.value = GamificationData()
                    _error.value = it.message
                }
            )
        }
    }
}
