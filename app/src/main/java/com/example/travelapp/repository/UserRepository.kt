package com.example.travelapp.repository


import com.example.travelapp.data.User
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("Users")

    // Hàm lưu thông tin user mới
    fun saveUser(user: User, onComplete: (Boolean) -> Unit) {
        usersCollection.document(user.uid)
            .set(user.toMap()) // Chuyển từ data class sang Map để Firebase hiểu
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Hàm lấy thông tin user về (ví dụ để hiện lên trang Profile)
    fun getUserProfile(uid: String, onSuccess: (User?) -> Unit) {
        usersCollection.document(uid).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                onSuccess(user)
            }
    }
}