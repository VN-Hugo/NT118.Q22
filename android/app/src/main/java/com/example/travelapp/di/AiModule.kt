package com.example.travelapp.di

import com.example.travelapp.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    @Provides
    @Singleton
    @Named("interpreter")
    fun provideInterpreterModel(): GenerativeModel {
        // Pass 1: Plain text output for destination interpretation
        return GenerativeModel(
            modelName = "gemini-3-flash-preview",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    @Provides
    @Singleton
    @Named("planner")
    fun providePlannerModel(): GenerativeModel {
        // Pass 2: JSON mode for structured plan generation
        return GenerativeModel(
            modelName = "gemini-3-flash-preview",
            apiKey = BuildConfig.GEMINI_API_KEY,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
            }
        )
    }
}
