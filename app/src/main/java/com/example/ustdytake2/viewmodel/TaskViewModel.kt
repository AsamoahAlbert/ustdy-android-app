package com.example.ustdytake2.viewmodel
// Handles adding tasks, fetching tasks, and marking tasks as complete
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ustdytake2.model.TaskItem
import com.example.ustdytake2.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repo: TaskRepository = TaskRepository()
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: StateFlow<List<TaskItem>> = _tasks

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadTasks(userId: String, classId: String) {
        viewModelScope.launch {
            val result = repo.getTasks(userId, classId)
            result.fold(
                onSuccess = { _tasks.value = it },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun addTask(userId: String, classId: String, task: TaskItem) {
        viewModelScope.launch {
            val result = repo.addTask(userId, classId, task)
            result.fold(
                onSuccess = { loadTasks(userId, classId) },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun markTaskComplete(
        userId: String,
        classId: String,
        task: TaskItem,
        completed: Boolean
    ) {
        viewModelScope.launch {
            val result = repo.updateTaskCompletion(userId, classId, task, completed)
            result.fold(
                onSuccess = { loadTasks(userId, classId) },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun updateTask(userId: String, classId: String, task: TaskItem) {
        viewModelScope.launch {
            val result = repo.updateTask(userId, classId, task)
            result.fold(
                onSuccess = { loadTasks(userId, classId) },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun deleteTask(userId: String, classId: String, taskId: String) {
        viewModelScope.launch {
            val result = repo.deleteTask(userId, classId, taskId)
            result.fold(
                onSuccess = { loadTasks(userId, classId) },
                onFailure = { _error.value = it.message }
            )
        }
    }
}
