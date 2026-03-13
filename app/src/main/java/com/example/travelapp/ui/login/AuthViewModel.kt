package com.example.travelapp.ui.login

import androidx.lifecycle.ViewModel
import com.example.travelapp.data.User
import com.example.travelapp.repository.UserRepository

class AuthViewModel : ViewModel() {
    private val repository = UserRepository()

    // Hàm để UI gọi khi muốn tạo user
    fun createNewUser(uid: String, email: String, name: String) {
        val newUser = User(
            uid = uid,
            email = email,
            fullName = name,
            role = "USER",
            createdAt = System.currentTimeMillis()
        )

        repository.saveUser(newUser) { success ->
            if (success) {
                println("Lưu User thành công lên Firestore!")
            } else {
                println("Lỗi khi lưu User.")
            }
        }
    }
}