package com.example.ustdytake2.repository
// Creates, fetches, and updates tasks

import com.example.ustdytake2.model.TaskItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TaskRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
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
        taskId: String,
        completed: Boolean
    ): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .collection("classes")
                .document(classId)
                .collection("tasks")
                .document(taskId)
                .update("completed", completed)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
