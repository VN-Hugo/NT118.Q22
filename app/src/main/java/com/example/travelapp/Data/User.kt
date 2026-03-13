package com.example.travelapp.data // Nhớ để đúng package của bạn nhé

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties // Giúp app không bị crash nếu Firebase có thêm dữ liệu lạ
data class User(
    val uid: String = "",           // ID duy nhất từ Firebase Authentication
    val fullName: String = "",      // Tên đầy đủ của người dùng
    val email: String = "",         // Email đăng ký
    val phoneNumber: String = "",   // Số điện thoại (dùng cho tính năng OTP của bạn)
    val avatarUrl: String = "",     // Link ảnh đại diện (sau này lưu trên Firebase Storage)
    val createdAt: Long = 0L        // Thời gian tạo tài khoản (dùng System.currentTimeMillis())
) {
    // Hàm này giúp bạn dễ dàng chuyển đổi dữ liệu thành Map để lưu lên Firebase
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "fullName" to fullName,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "avatarUrl" to avatarUrl,
            "createdAt" to createdAt
        )
    }
}