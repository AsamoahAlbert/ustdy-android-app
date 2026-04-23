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
                mapTaskItem(
                    id = doc.id,
                    title = doc.getString("title"),
                    type = doc.getString("type"),
                    dueDate = doc.getLong("dueDate"),
                    completed = doc.getBoolean("completed"),
                    reminderDate = doc.getLong("reminderDate"),
                    completedAt = doc.getLong("completedAt")
                )
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
            db.collection("users")
                .document(userId)
                .collection("classes")
                .document(classId)
                .collection("tasks")
                .document(task.id)
                .update("completed", completed)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(userId: String, classId: String, task: TaskItem): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .collection("classes")
                .document(classId)
                .collection("tasks")
                .document(task.id)
                .set(task.copy(id = ""))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(userId: String, classId: String, taskId: String): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .collection("classes")
                .document(classId)
                .collection("tasks")
                .document(taskId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

internal fun mapTaskItem(
    id: String,
    title: String?,
    type: String?,
    dueDate: Long?,
    completed: Boolean?,
    reminderDate: Long?,
    completedAt: Long?
): TaskItem {
    return TaskItem(
        id = id,
        title = title.orEmpty(),
        type = type.orEmpty(),
        dueDate = dueDate ?: 0L,
        completed = completed ?: false,
        reminderDate = reminderDate ?: 0L,
        completedAt = completedAt ?: 0L
    )
}
