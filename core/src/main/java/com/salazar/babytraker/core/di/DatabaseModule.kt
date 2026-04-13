package com.salazar.babytraker.core.di

import android.content.Context
import androidx.room.Room
import com.salazar.babytraker.core.data.local.BabyTrakerDatabase
import com.salazar.babytraker.core.data.local.dao.BabyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): BabyTrakerDatabase {
        return Room.databaseBuilder(
            context,
            BabyTrakerDatabase::class.java,
            "babytraker_db"
        )
        .fallbackToDestructiveMigration() // Evita crashes al cambiar el esquema durante el desarrollo
        .build()
    }

    @Provides
    @Singleton
    fun provideBabyDao(database: BabyTrakerDatabase): BabyDao {
        return database.babyDao()
    }
}
