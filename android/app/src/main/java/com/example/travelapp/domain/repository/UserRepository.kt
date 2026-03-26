package com.example.travelapp.domain.repository

import com.example.travelapp.domain.model.User

interface UserRepository {

    suspend fun loginUser(email: String, pass: String): Boolean

    suspend fun registerUser(email: String, pass: String): String?

    suspend fun saveUser(user: User): Boolean

    suspend fun getUserProfile(uid: String): User?
}