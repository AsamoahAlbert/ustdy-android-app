package com.example.ustdytake2.model
// Task item model
data class TaskItem(
    val id: String = "",
    val title: String = "",
    val type: String = "",
    val dueDate: Long = 0L,
    val completed: Boolean = false,
    val reminderDate: Long = 0L,
    val completedAt: Long = 0L
)
