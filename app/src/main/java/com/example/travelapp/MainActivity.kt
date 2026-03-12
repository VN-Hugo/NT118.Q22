package com.example.travelapp

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.travelapp.ui.login.LoginScreen
import com.example.travelapp.ui.theme.TravelAppTheme
import com.google.firebase.database.FirebaseDatabase


class MainActivity : ComponentActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("test_connection")

        myRef.setValue("Chào Firebase, tui là Bảo nè! Đang dùng BoM 34.10.0")
            .addOnSuccessListener {
                // Nếu hiện log này là đã thông lên mây thành công!
                println("Firebase: Gửi dữ liệu thành công rồi Bảo ơi!")
            }
            .addOnFailureListener { e ->
                println("Firebase: Lỗi rồi: ${e.message}")
            }
        setContent {
            TravelAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen()
                }
            }
        }



    }


}