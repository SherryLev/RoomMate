package org.housemate.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import org.housemate.repositories.AuthRepository
import org.housemate.repositories.AuthRepositoryImpl


@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun providesFirestoreRepository(
        repo:FirestoreDbRepositoryImpl
    ):FirestoreRepository

    @Binds
    abstract fun providesFirebaseAuthRepository(
        repo: AuthRepositoryImpl
    ): AuthRepository

}