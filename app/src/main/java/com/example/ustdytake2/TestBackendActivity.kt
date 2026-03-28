package com.example.ustdytake2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ustdytake2.model.ClassItem
import com.example.ustdytake2.model.TaskItem
import com.example.ustdytake2.viewmodel.AuthViewModel
import com.example.ustdytake2.viewmodel.ClassViewModel
import com.example.ustdytake2.viewmodel.TaskViewModel
import com.example.ustdytake2.viewmodel.AuthState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TestBackendActivity : ComponentActivity() {

    private val authVM: AuthViewModel by viewModels()
    private val classVM: ClassViewModel by viewModels()
    private val taskVM: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("=== BACKEND TEST START ===")

        lifecycleScope.launchWhenStarted {
            authVM.authState.collect { state ->
                println("AuthState: $state")

                when (state) {

                    is AuthState.Idle -> {
                        // Create a fresh test user
                        authVM.signUp("test3@example.com", "password123")
                    }

                    is AuthState.Success -> {
                        val userId = state.userId
                        println("Signed in as: $userId")

                        // Run the rest of the backend test inside a coroutine
                        lifecycleScope.launch {

                            // 1. Add a class
                            val classItem = ClassItem(
                                id = "",
                                name = "Biology",
                                color = "Green"
                            )
                            classVM.addClass(userId, classItem)

                            // 2. Load classes
                            classVM.loadClasses(userId)

                            // Wait for the first non-empty class list
                            val classes = classVM.classes.first { it.isNotEmpty() }
                            println("Classes: $classes")

                            val classId = classes.first().id

                            // 3. Create a task
                            val task = TaskItem(
                                id = "",
                                title = "Read Chapter 3",
                                type = "Homework",
                                completed = false
                            )
                            taskVM.addTask(userId, classId, task)

                            // Wait for Firestore to finish writing
                            kotlinx.coroutines.delay(500)

                            // 4. Load tasks
                            taskVM.loadTasks(userId, classId)

                            // Wait for tasks to load
                            val tasks = taskVM.tasks.first()
                            println("Tasks: $tasks")

                            // 5. Mark the first task complete
                            if (tasks.isNotEmpty()) {
                                val taskId = tasks.first().id
                                taskVM.markTaskComplete(userId, classId, taskId, true)
                                println("Marked task complete")
                            }
                        }
                    }

                    is AuthState.Error -> {
                        println("Auth error: ${state.message}")
                    }

                    else -> Unit
                }
            }
        }
    }
}
