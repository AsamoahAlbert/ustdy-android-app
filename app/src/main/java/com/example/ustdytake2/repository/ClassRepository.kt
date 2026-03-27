package com.example.ustdytake2.repository
// Creates and fetches classes
import com.example.ustdytake2.model.ClassItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ClassRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun addClass(userId: String, classItem: ClassItem): Result<String> {
        return try {
            val docRef = db.collection("users")
                .document(userId)
                .collection("classes")
                .add(classItem)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClasses(userId: String): Result<List<ClassItem>> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("classes")
                .get()
                .await()

            val classes = snapshot.documents.map { doc ->
                doc.toObject(ClassItem::class.java)!!.copy(id = doc.id)
            }

            Result.success(classes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
