package com.example.ustdytake2.repository
// Creates backend for login and signup
// Gives Signup, Login, Logout, and UserID
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    suspend fun signUp(email: String, password: String): Result<String> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(auth.currentUser?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(auth.currentUser?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun currentUserId(): String? = auth.currentUser?.uid
}
