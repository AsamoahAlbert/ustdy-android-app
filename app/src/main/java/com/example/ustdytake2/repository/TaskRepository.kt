package com.example.ustdytake2.repository
// Creates, fetches, and updates tasks

import com.example.ustdytake2.model.TaskItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TaskRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val gamificationRepo: GamificationRepository = GamificationRepository()
) {

    suspend fun addTask(userId: String, classId: String, task: TaskItem): Result<String> {
        return try {
            val docRef = db.collection("users")
                .document(userId)
                .collection("classes")
                .document(classId)
                .collection("tasks")
                .add(task)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTasks(userId: String, classId: String): Result<List<TaskItem>> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("classes")
                .document(classId)
                .collection("tasks")
                .get()
                .await()

            val tasks = snapshot.documents.map { doc ->
                doc.toObject(TaskItem::class.java)!!.copy(id = doc.id)
            }

            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTaskCompletion(
        userId: String,
        classId: String,
        task: TaskItem,
        completed: Boolean
    ): Result<Unit> {
        return try {
            val taskRef = db.collection("users")
                .document(userId)
                .collection("classes")
                .document(classId)
                .collection("tasks")
                .document(task.id)

            val completedAt = if (completed) System.currentTimeMillis() else 0L

            // 1) Update task in Firestore
            taskRef.update(
                "completed", completed,
                "completedAt", completedAt
            ).await()

            // 2) Trigger gamification logic using the updated task data
            if (completed) {
                val updatedTask = task.copy(
                    completed = true,
                    completedAt = completedAt
                )
                gamificationRepo.onTaskCompleted(userId, updatedTask).getOrThrow()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
