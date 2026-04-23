package com.example.ustdytake2.ui

import com.example.ustdytake2.model.ClassItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlannerStateLogicTest {

    private val localTask = PlannerTask(
        id = "local-1",
        title = "Reading",
        type = TaskType.READING_ASSIGNMENT,
        dueDateMillis = 1_000L,
        className = "ENG 1301"
    )

    private val remoteTask = PlannerTask(
        id = "remote-1",
        title = "Quiz",
        type = TaskType.QUIZ,
        dueDateMillis = 2_000L,
        className = "COSC 6355",
        classId = "class-1",
        isRemote = true
    )

    @Test
    fun `selectActiveClass returns null when no classes exist`() {
        assertNull(selectActiveClass(emptyList(), null))
    }

    @Test
    fun `selectActiveClass keeps current selection when still present`() {
        val classItem = ClassItem(id = "class-1", name = "COSC 6355")

        val selected = selectActiveClass(listOf(classItem), classItem)

        assertEquals(classItem, selected)
    }

    @Test
    fun `selectActiveClass falls back to first class when selection is stale`() {
        val classes = listOf(
            ClassItem(id = "class-1", name = "COSC 6355"),
            ClassItem(id = "class-2", name = "HIST 2321")
        )

        val selected = selectActiveClass(classes, ClassItem(id = "missing", name = "Old"))

        assertEquals("class-1", selected?.id)
    }

    @Test
    fun `mergePlannerTasks uses only local tasks in demo mode`() {
        val merged = mergePlannerTasks(
            isDemoMode = true,
            remoteTasks = listOf(remoteTask),
            localTasks = listOf(localTask)
        )

        assertEquals(listOf(localTask), merged)
    }

    @Test
    fun `mergePlannerTasks combines remote and local tasks outside demo mode`() {
        val merged = mergePlannerTasks(
            isDemoMode = false,
            remoteTasks = listOf(remoteTask),
            localTasks = listOf(localTask)
        )

        assertEquals(listOf(remoteTask, localTask), merged)
    }

    @Test
    fun `applyLocalTaskCompletion marks task complete and stores completion time`() {
        val updated = applyLocalTaskCompletion(
            localTasks = listOf(localTask),
            task = localTask,
            completed = true,
            completedAtMillis = 5_000L
        )

        assertTrue(updated.single().completed)
        assertEquals(5_000L, updated.single().completedAtMillis)
    }

    @Test
    fun `applyLocalTaskCompletion clears completion time when unchecking task`() {
        val completedTask = localTask.copy(completed = true, completedAtMillis = 5_000L)

        val updated = applyLocalTaskCompletion(
            localTasks = listOf(completedTask),
            task = completedTask,
            completed = false,
            completedAtMillis = 9_000L
        )

        assertEquals(false, updated.single().completed)
        assertEquals(0L, updated.single().completedAtMillis)
    }

    @Test
    fun `saveLocalTask adds new task with generated id`() {
        val savedTask = localTask.copy(id = "")

        val updated = saveLocalTask(
            localTasks = emptyList(),
            taskBeingEdited = null,
            savedTask = savedTask,
            generatedId = "generated-1"
        )

        assertEquals("generated-1", updated.single().id)
    }

    @Test
    fun `saveLocalTask updates existing task when editing`() {
        val updated = saveLocalTask(
            localTasks = listOf(localTask),
            taskBeingEdited = localTask,
            savedTask = localTask.copy(title = "Updated"),
            generatedId = "ignored"
        )

        assertEquals("Updated", updated.single().title)
        assertEquals("local-1", updated.single().id)
    }

    @Test
    fun `deleteLocalTask removes matching task`() {
        val updated = deleteLocalTask(listOf(localTask, remoteTask), "local-1")

        assertEquals(listOf(remoteTask), updated)
    }
}
