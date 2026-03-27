package com.example.travelapp.data.mapper

import com.example.travelapp.data.remote.dto.UserDTO
import com.example.travelapp.domain.model.User

fun UserDTO.toDomain(): User {
    return User(
        uid,
        fullName,
        email,
        phoneNumber,
        avatarUrl,
        role,
        favoriteIds,
        fcmToken,
        isActive,
        createdAt
    )
}

fun User.toDTO(): UserDTO {
    return UserDTO(
        uid,
        fullName,
        email,
        phoneNumber,
        avatarUrl,
        role,
        favoriteIds,
        fcmToken,
        isActive,
        createdAt
    )
}