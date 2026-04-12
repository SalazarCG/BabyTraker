package com.salazar.babytraker.features.medicos.di

import com.salazar.babytraker.features.medicos.data.repository.MedicosRepositoryImpl
import com.salazar.babytraker.features.medicos.domain.repository.MedicosRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MedicosModule {

    @Binds
    @Singleton
    abstract fun bindMedicosRepository(
        impl: MedicosRepositoryImpl
    ): MedicosRepository
}
