package com.example.travelapp

import android.app.Application
import com.example.travelapp.utils.CloudinaryConfig
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Khởi tạo Cloudinary ngay khi ứng dụng bắt đầu
        CloudinaryConfig.init(this)
    }
}