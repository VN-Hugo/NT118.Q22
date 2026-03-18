package com.example.travelapp.repository

import com.example.travelapp.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val usersCollection = db.collection("Users")

    // 1. Hàm Đăng nhập (Mới thêm để khớp với ViewModel)
    fun loginUser(email: String, pass: String, onComplete: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    // 2. Hàm Đăng ký tài khoản (Mới thêm để khớp với ViewModel)
    // Trả về success và UID của user mới tạo
    fun registerUser(email: String, pass: String, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, task.result?.user?.uid)
                } else {
                    onComplete(false, null)
                }
            }
    }

    // 3. Hàm lưu thông tin user mới lên Firestore
    fun saveUser(user: User, onComplete: (Boolean) -> Unit) {
        usersCollection.document(user.uid)
            .set(user.toMap())
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // 4. Hàm lấy thông tin user về (Giữ lại để dùng cho trang Profile)
    fun getUserProfile(uid: String, onSuccess: (User?) -> Unit) {
        usersCollection.document(uid).get()
            .addOnSuccessListener { document ->
                // Ép kiểu từ Document sang Data Class User
                val user = document.toObject(User::class.java)
                onSuccess(user)
            }
            .addOnFailureListener {
                onSuccess(null)
            }
    }
}