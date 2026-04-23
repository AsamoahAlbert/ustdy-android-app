package com.example.ustdytake2.repository

import com.example.ustdytake2.viewmodel.GamificationData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GamificationRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun getGamification(userId: String): Result<GamificationData> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .get()
                .await()

            val badges = snapshot.get("badges") as? List<*>
            Result.success(
                mapGamificationData(
                    streak = snapshot.getLong("streak"),
                    lastStudyDate = snapshot.getLong("lastStudyDate"),
                    badges = badges
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

internal fun mapGamificationData(
    streak: Long?,
    lastStudyDate: Long?,
    badges: List<*>?
): GamificationData {
    return GamificationData(
        streak = (streak ?: 0L).toInt(),
        lastStudyDate = lastStudyDate ?: 0L,
        badges = badges?.mapNotNull { it as? String } ?: emptyList()
    )
}
