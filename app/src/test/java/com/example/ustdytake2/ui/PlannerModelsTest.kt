package com.example.ustdytake2.ui

import com.example.ustdytake2.model.TaskItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlannerModelsTest {

    @Test
    fun `toPlannerTask preserves backend reminder and completion fields`() {
        val taskItem = TaskItem(
            id = "task-1",
            title = "Midterm",
            type = "Exam",
            dueDate = 1_000L,
            completed = true,
            reminderDate = 800L,
            completedAt = 900L
        )

        val plannerTask = taskItem.toPlannerTask(classId = "class-1", className = "COSC 6355")

        assertEquals("task-1", plannerTask.id)
        assertEquals(TaskType.EXAM, plannerTask.type)
        assertEquals(800L, plannerTask.reminderDateMillis)
        assertEquals(900L, plannerTask.completedAtMillis)
        assertEquals("class-1", plannerTask.classId)
        assertTrue(plannerTask.isRemote)
    }

    @Test
    fun `toPlannerTask maps unknown backend discussion reply label`() {
        val plannerTask = TaskItem(
            id = "task-2",
            title = "Forum",
            type = "Discussion Reply",
            dueDate = 1_000L
        ).toPlannerTask(classId = null, className = "HIST 2321")

        assertEquals(TaskType.DISCUSSION_REPLIES, plannerTask.type)
    }

    @Test
    fun `toTaskItem writes reminder and completedAt values back to backend model`() {
        val plannerTask = PlannerTask(
            id = "task-3",
            title = "Project",
            type = TaskType.LARGE_PROJECT,
            dueDateMillis = 2_000L,
            className = "COSC 6355",
            reminderDateMillis = 1_500L,
            completed = true,
            completedAtMillis = 1_800L
        )

        val taskItem = plannerTask.toTaskItem()

        assertEquals("task-3", taskItem.id)
        assertEquals("Large Project", taskItem.type)
        assertEquals(1_500L, taskItem.reminderDate)
        assertEquals(1_800L, taskItem.completedAt)
        assertTrue(taskItem.completed)
    }
}
