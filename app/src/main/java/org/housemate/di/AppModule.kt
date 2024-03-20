package org.housemate.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.housemate.data.AuthRepositoryImpl
import org.housemate.domain.repositories.AuthRepository
import org.housemate.domain.use_cases.ValidateLoginInputUseCase
import org.housemate.domain.use_cases.ValidateRegisterInputUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.housemate.data.ExpenseRepositoryImpl
import org.housemate.domain.repositories.ExpenseRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideValidateLoginInputUseCase():ValidateLoginInputUseCase{
        return ValidateLoginInputUseCase()
    }

    @Provides
    @Singleton
    fun provideValidateRegisterInputUseCase():ValidateRegisterInputUseCase{
        return ValidateRegisterInputUseCase()
    }

    @Provides
    @Singleton
    fun provideAuthRepository():AuthRepository{
        return AuthRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
    @Provides
    @Singleton
    fun provideExpenseRepository(
        firestore : FirebaseFirestore,
        auth: FirebaseAuth
    ): ExpenseRepository{
        return ExpenseRepositoryImpl(firestore, auth)
    }

}