package com.example.ustdytake2.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlannerDateUtilsTest {

    @Test
    fun `isDueThisWeek returns true for a task inside current week`() {
        val task = PlannerTask(
            id = "1",
            title = "Quiz",
            type = TaskType.QUIZ,
            dueDateMillis = weekDays()[2],
            className = "COSC 1"
        )

        assertTrue(task.isDueThisWeek())
    }

    @Test
    fun `isDueThisWeek returns false for a task outside current week`() {
        val task = PlannerTask(
            id = "1",
            title = "Quiz",
            type = TaskType.QUIZ,
            dueDateMillis = weekDays().last() + (7L * 24 * 60 * 60 * 1000),
            className = "COSC 1"
        )

        assertFalse(task.isDueThisWeek())
    }

    @Test
    fun `tasksForDay returns only tasks for requested day sorted by time`() {
        val day = weekDays()[1]
        val tasks = listOf(
            PlannerTask("2", "Later", TaskType.EXAM, day + 2_000L, "COSC"),
            PlannerTask("3", "Other Day", TaskType.EXAM, weekDays()[2], "COSC"),
            PlannerTask("1", "Sooner", TaskType.EXAM, day + 1_000L, "COSC")
        )

        val result = tasksForDay(tasks, day)

        assertEquals(listOf("1", "2"), result.map { it.id })
    }

    @Test
    fun `parseDate parses valid editor date and rejects invalid one`() {
        assertNotNull(parseDate("04/22/2026"))
        assertEquals(null, parseDate("not-a-date"))
    }
}
