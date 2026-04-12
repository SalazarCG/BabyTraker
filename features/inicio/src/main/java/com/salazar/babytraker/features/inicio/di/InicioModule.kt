package com.salazar.babytraker.features.inicio.di

import com.salazar.babytraker.features.inicio.data.repository.InicioRepositoryImpl
import com.salazar.babytraker.features.inicio.domain.repository.InicioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InicioModule {

    @Binds
    @Singleton
    abstract fun bindInicioRepository(
        inicioRepositoryImpl: InicioRepositoryImpl
    ): InicioRepository
}
