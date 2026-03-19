package com.example.bookinglite.user.dto
import java.util.UUID
import java.time.Instant
data class UserDTO(
    val userId: UUID?,
    val username: String,
    val role: String,
    val email: String,
    val avatarUrl: String?,
    val phone: String?,
    val createdAt: Instant?
)
