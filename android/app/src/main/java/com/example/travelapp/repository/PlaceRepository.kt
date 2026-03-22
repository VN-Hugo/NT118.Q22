package com.example.travelapp.repository

import android.util.Log
import com.example.travelapp.model.Hotel
import com.google.firebase.firestore.FirebaseFirestore

class PlaceRepository {
    private val db = FirebaseFirestore.getInstance()

    // Trỏ tới 1 cái Collection trên Firestore tên là "Hotels"
    private val hotelsCollection = db.collection("Hotels")

    // Hàm kéo toàn bộ danh sách khách sạn về
    fun getAllHotels(onSuccess: (List<Hotel>) -> Unit, onFailure: (Exception) -> Unit) {
        hotelsCollection.get()
            .addOnSuccessListener { snapshot ->
                // Biến đổi đống dữ liệu tải về thành 1 List<Hotel>
                val hotelList = snapshot.documents.mapNotNull { document ->
                    document.toObject(Hotel::class.java)
                }
                Log.d("Firestore", "Tải thành công ${hotelList.size} khách sạn")
                onSuccess(hotelList) // Gửi danh sách này lên giao diện
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Lỗi khi tải khách sạn: ${exception.message}")
                onFailure(exception)
            }
    }
}