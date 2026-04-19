package com.example.travelapp.utils

import android.content.Context
import com.cloudinary.android.MediaManager


object CloudinaryConfig {
    fun init(context: Context) {
        val config = mapOf(
            "cloud_name" to Secrets.CLOUDINARY_CLOUD_NAME,
            "api_key" to Secrets.CLOUDINARY_API_KEY,
            "api_secret" to Secrets.CLOUDINARY_API_SECRET
        )

        try {
            MediaManager.init(context, config)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}