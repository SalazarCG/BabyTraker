package com.salazar.babytraker.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.salazar.babytraker.core.data.local.dao.BabyDao
import com.salazar.babytraker.core.data.local.entities.*

@Database(
    entities = [
        BabyEntity::class,
        TomaEntity::class,
        PanalEntity::class,
        CitaMedicaEntity::class,
        DailyJournalEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class BabyTrakerDatabase : RoomDatabase() {
    abstract fun babyDao(): BabyDao
}
