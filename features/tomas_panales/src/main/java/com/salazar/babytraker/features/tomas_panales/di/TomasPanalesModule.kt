package com.salazar.babytraker.features.tomas_panales.di

import com.salazar.babytraker.features.tomas_panales.data.repository.TomasPanalesRepositoryImpl
import com.salazar.babytraker.features.tomas_panales.domain.repository.TomasPanalesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TomasPanalesModule {

    @Binds
    @Singleton
    abstract fun bindTomasPanalesRepository(
        impl: TomasPanalesRepositoryImpl
    ): TomasPanalesRepository
}
