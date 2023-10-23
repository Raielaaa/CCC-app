package com.example.ccc_library_app.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Singleton
    @Provides
    @Named("GoogleSignInClient")
    fun providesGoogleSignInClient(
        @ApplicationContext
        appContext: Context
    ) = GoogleSignIn.getClient(appContext, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("319826207112-g1nvf9fpcb09pe26bqsrhtid937hkt09.apps.googleusercontent.com")
        .requestEmail()
        .build())
}