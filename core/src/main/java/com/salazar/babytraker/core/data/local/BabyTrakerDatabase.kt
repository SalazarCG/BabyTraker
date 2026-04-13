package com.salazar.babytraker.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.salazar.babytraker.core.data.local.dao.BabyDao
import com.salazar.babytraker.core.data.local.entities.CitaMedicaEntity
import com.salazar.babytraker.core.data.local.entities.PanalEntity
import com.salazar.babytraker.core.data.local.entities.TomaEntity

@Database(
    entities = [
        TomaEntity::class,
        PanalEntity::class,
        CitaMedicaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BabyTrakerDatabase : RoomDatabase() {
    abstract fun babyDao(): BabyDao
}
