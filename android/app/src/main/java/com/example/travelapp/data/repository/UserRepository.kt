package com.example.travelapp.data.repository

import android.net.Uri
import com.example.travelapp.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun loginUser(email: String, pass: String): Boolean
    suspend fun signInWithGoogle(idToken: String): Boolean
    suspend fun registerUser(email: String, pass: String): String?
    suspend fun saveUser(user: User): Boolean
    suspend fun getUserProfile(uid: String): User?
    fun getUserFlow(uid: String): Flow<User?>
    suspend fun logout()
    fun getCurrentUserId(): String?
    suspend fun uploadAvatar(uid: String, uri: Uri): String?
    suspend fun uploadAvatarData(uid: String, data: ByteArray): String?
    suspend fun updateProfile(uid: String, updates: Map<String, Any>): Boolean
    
    // Wishlist methods
    suspend fun toggleFavorite(userId: String, propertyId: String): Boolean
}