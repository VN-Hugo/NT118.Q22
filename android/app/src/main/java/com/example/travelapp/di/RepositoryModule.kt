package com.example.travelapp.di

import com.example.travelapp.data.repository.PropertyRepositoryImpl
import com.example.travelapp.data.repository.UserRepositoryImpl
import com.example.travelapp.domain.repository.PropertyRepository
import com.example.travelapp.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindPropertyRepository(
        propertyRepositoryImpl: PropertyRepositoryImpl
    ): PropertyRepository
}