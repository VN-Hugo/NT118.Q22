package com.example.travelapp.domain.repository

import android.net.Uri
import com.example.travelapp.domain.model.User

interface UserRepository {

    suspend fun loginUser(email: String, pass: String): Boolean

    suspend fun signInWithGoogle(idToken: String): Boolean

    suspend fun registerUser(email: String, pass: String): String?

    suspend fun saveUser(user: User): Boolean

    suspend fun getUserProfile(uid: String): User?

    suspend fun logout()

    fun getCurrentUserId(): String?

    suspend fun uploadAvatar(uid: String, uri: Uri): String?

    suspend fun updateProfile(uid: String, updates: Map<String, Any>): Boolean
}