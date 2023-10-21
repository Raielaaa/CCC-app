package com.example.ccc_library_app.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    @Named("FirebaseAuth.Instance")
    fun providesFirebaseAuthInstance() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    @Named("FirebaseFireStore.Instance")
    fun providesFirebaseFireStoreInstance() = FirebaseFirestore.getInstance()
}