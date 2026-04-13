package com.salazar.babytraker.core.data.local.dao

import androidx.room.*
import com.salazar.babytraker.core.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyDao {

    // --- BEBES ---
    @Query("SELECT * FROM bebes")
    fun getAllBabies(): Flow<List<BabyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaby(baby: BabyEntity)

    // --- TOMAS ---
    @Query("SELECT * FROM tomas WHERE fechaDia = :fechaDia AND babyId = :babyId ORDER BY timestamp DESC")
    fun getTomasPorDia(fechaDia: Long, babyId: Long): Flow<List<TomaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToma(toma: TomaEntity)

    // --- PAÑALES ---
    @Query("SELECT * FROM panales WHERE fechaDia = :fechaDia AND babyId = :babyId ORDER BY timestamp DESC")
    fun getPanalesPorDia(fechaDia: Long, babyId: Long): Flow<List<PanalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPanal(panal: PanalEntity)

    // --- DIARIO / COMENTARIOS ---
    @Query("SELECT * FROM diarios_diarios WHERE babyId = :babyId")
    fun getJournalsForBaby(babyId: Long): Flow<List<DailyJournalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: DailyJournalEntity)

    // --- ACTIVIDAD GLOBAL ---
    @Query("""
        SELECT DISTINCT fechaDia FROM (
            SELECT fechaDia FROM tomas WHERE babyId = :babyId
            UNION 
            SELECT fechaDia FROM panales WHERE babyId = :babyId
        ) ORDER BY fechaDia DESC
    """)
    fun getDiasConActividad(babyId: Long): Flow<List<Long>>

    @Delete
    suspend fun deleteToma(toma: TomaEntity)

    @Delete
    suspend fun deletePanal(panal: PanalEntity)
}
