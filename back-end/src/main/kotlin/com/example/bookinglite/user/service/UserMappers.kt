package com.example.bookinglite.user.service

import com.example.bookinglite.user.dto.UserDTO
import com.example.bookinglite.user.entity.UserEntity
import java.util.UUID

// 1. Chuyển từ Entity (DB) sang DTO (Postman)
fun UserEntity.toDTO() = UserDTO(
    userId = this.userId,
    username = this.username,
    role = this.role,
    email = this.email,
    avatarUrl = this.avatarUrl,
    phone = this.phone,
    createdAt = this.createdAt
)

// 2. Chuyển từ DTO (Dữ liệu người dùng gửi lên) sang Entity để lưu vào DB
fun UserDTO.toEntity(passwordHash: String = "") = UserEntity(
    userId = this.userId, // Thường là null khi tạo mới
    username = this.username,
    role = this.role,
    email = this.email,
    avatarUrl = this.avatarUrl,
    phone = this.phone,
    passwordHash = passwordHash // Mật khẩu được xử lý riêng ở Service
)