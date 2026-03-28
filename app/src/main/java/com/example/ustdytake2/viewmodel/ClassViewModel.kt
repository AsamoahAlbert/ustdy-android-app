package com.example.ustdytake2.viewmodel
// Handles adding a class, fetching classes, and exposing list state
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ustdytake2.model.ClassItem
import com.example.ustdytake2.repository.ClassRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClassViewModel(
    private val repo: ClassRepository = ClassRepository()
) : ViewModel() {

    private val _classes = MutableStateFlow<List<ClassItem>>(emptyList())
    val classes: StateFlow<List<ClassItem>> = _classes

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadClasses(userId: String) {
        viewModelScope.launch {
            val result = repo.getClasses(userId)
            result.fold(
                onSuccess = { _classes.value = it },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun addClass(userId: String, classItem: ClassItem) {
        viewModelScope.launch {
            val result = repo.addClass(userId, classItem)
            result.fold(
                onSuccess = { loadClasses(userId) },
                onFailure = { _error.value = it.message }
            )
        }
    }
}
