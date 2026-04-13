package com.salazar.babytraker.core.di

import com.salazar.babytraker.core.data.repository.BabyRepositoryImpl
import com.salazar.babytraker.core.domain.repository.BabyRepository
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
    abstract fun bindBabyRepository(
        babyRepositoryImpl: BabyRepositoryImpl
    ): BabyRepository
}
