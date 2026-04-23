package com.example.ustdytake2.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackendMappersTest {

    @Test
    fun `mapTaskItem uses safe defaults for missing firestore fields`() {
        val task = mapTaskItem(
            id = "task-1",
            title = null,
            type = null,
            dueDate = null,
            completed = null,
            reminderDate = null,
            completedAt = null
        )

        assertEquals("task-1", task.id)
        assertEquals("", task.title)
        assertEquals("", task.type)
        assertEquals(0L, task.dueDate)
        assertEquals(false, task.completed)
        assertEquals(0L, task.reminderDate)
        assertEquals(0L, task.completedAt)
    }

    @Test
    fun `mapGamificationData filters invalid badges and defaults missing values`() {
        val gamification = mapGamificationData(
            streak = null,
            lastStudyDate = null,
            badges = listOf("Starter", 42, null, "Consistent")
        )

        assertEquals(0, gamification.streak)
        assertEquals(0L, gamification.lastStudyDate)
        assertEquals(listOf("Starter", "Consistent"), gamification.badges)
    }

    @Test
    fun `mapGamificationData keeps valid backend values`() {
        val gamification = mapGamificationData(
            streak = 5L,
            lastStudyDate = 123456L,
            badges = listOf("Starter")
        )

        assertEquals(5, gamification.streak)
        assertEquals(123456L, gamification.lastStudyDate)
        assertTrue(gamification.badges.contains("Starter"))
    }
}
