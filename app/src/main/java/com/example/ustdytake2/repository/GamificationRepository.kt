package com.example.ustdytake2.repository

import com.example.ustdytake2.model.GamificationData
import com.example.ustdytake2.model.TaskItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class GamificationRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun getGamification(userId: String): Result<GamificationData> = runCatching {
        val snap = db.collection("users").document(userId).get().await()
        snap.toObject(GamificationData::class.java) ?: GamificationData()
    }

    /**
     * Called whenever a task is marked completed.
     * This is where streak + badge logic lives.
     */
    suspend fun onTaskCompleted(userId: String, task: TaskItem): Result<Unit> = runCatching {
        val userRef = db.collection("users").document(userId)

        db.runTransaction { tx ->
            val current = tx.get(userRef).toObject(GamificationData::class.java)
                ?: GamificationData()

            val updatedStreakData = updateStreak(current)
            val updatedBadges = updateBadges(updatedStreakData, current.badges, task)

            tx.update(
                userRef,
                mapOf(
                    "streak" to updatedStreakData.streak,
                    "lastStudyDate" to updatedStreakData.lastStudyDate,
                    "badges" to updatedBadges
                )
            )
        }.await()
    }

    // ---------- private helpers (streak + badges) ----------

    private fun updateStreak(current: GamificationData): GamificationData {
        val today = LocalDate.now()
        val lastDate = if (current.lastStudyDate == 0L) {
            null
        } else {
            Instant.ofEpochMilli(current.lastStudyDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }

        val newStreak = when {
            lastDate == null -> 1
            lastDate == today -> current.streak
            lastDate == today.minusDays(1) -> current.streak + 1
            else -> 1
        }

        return current.copy(
            streak = newStreak,
            lastStudyDate = System.currentTimeMillis()
        )
    }

    private fun updateBadges(
        updated: GamificationData,
        existingBadges: List<String>,
        task: TaskItem
    ): List<String> {
        val badges = existingBadges.toMutableList()

        // Early completion badge
        if (task.completedAt > 0L &&
            task.reminderDate > 0L &&
            task.completedAt < task.reminderDate &&
            "early_bird" !in badges
        ) {
            badges.add("early_bird")
        }

        // Streak badges
        if (updated.streak >= 7 && "week_streak_7" !in badges) {
            badges.add("week_streak_7")
        }
        if (updated.streak >= 30 && "week_streak_30" !in badges) {
            badges.add("week_streak_30")
        }

        return badges
    }
}
