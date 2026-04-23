package com.example.ustdytake2.ui

import com.example.ustdytake2.model.ClassItem

internal fun selectActiveClass(
    classes: List<ClassItem>,
    selectedClass: ClassItem?
): ClassItem? {
    if (classes.isEmpty()) return null
    return selectedClass?.takeIf { existing ->
        classes.any { it.id == existing.id }
    } ?: classes.first()
}

internal fun mergePlannerTasks(
    isDemoMode: Boolean,
    remoteTasks: List<PlannerTask>,
    localTasks: List<PlannerTask>
): List<PlannerTask> {
    return if (isDemoMode) localTasks else remoteTasks + localTasks
}

internal fun applyLocalTaskCompletion(
    localTasks: List<PlannerTask>,
    task: PlannerTask,
    completed: Boolean,
    completedAtMillis: Long
): List<PlannerTask> {
    return localTasks.map { existing ->
        if (existing.id == task.id) {
            existing.copy(
                completed = completed,
                completedAtMillis = if (completed) completedAtMillis else 0L
            )
        } else {
            existing
        }
    }
}

internal fun saveLocalTask(
    localTasks: List<PlannerTask>,
    taskBeingEdited: PlannerTask?,
    savedTask: PlannerTask,
    generatedId: String
): List<PlannerTask> {
    return if (taskBeingEdited == null) {
        localTasks + savedTask.copy(id = generatedId)
    } else {
        localTasks.map { task ->
            if (task.id == savedTask.id) savedTask else task
        }
    }
}

internal fun deleteLocalTask(
    localTasks: List<PlannerTask>,
    taskId: String
): List<PlannerTask> {
    return localTasks.filterNot { it.id == taskId }
}
