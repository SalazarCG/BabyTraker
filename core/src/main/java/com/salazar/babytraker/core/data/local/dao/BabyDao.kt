package com.salazar.babytraker.core.data.local.dao

import androidx.room.*
import com.salazar.babytraker.core.data.local.entities.PanalEntity
import com.salazar.babytraker.core.data.local.entities.TomaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyDao {

    // --- TOMAS ---
    @Query("SELECT * FROM tomas WHERE fechaDia = :fechaDia ORDER BY timestamp DESC")
    fun getTomasPorDia(fechaDia: Long): Flow<List<TomaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToma(toma: TomaEntity)

    // --- PAÑALES ---
    @Query("SELECT * FROM panales WHERE fechaDia = :fechaDia ORDER BY timestamp DESC")
    fun getPanalesPorDia(fechaDia: Long): Flow<List<PanalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPanal(panal: PanalEntity)

    // --- ACTIVIDAD GLOBAL ---
    /**
     * Obtiene todos los días únicos donde hay al menos una actividad registrada.
     * Útil para calendarios o listas de historial en Inicio.
     */
    @Query("""
        SELECT DISTINCT fechaDia FROM tomas 
        UNION 
        SELECT DISTINCT fechaDia FROM panales 
        ORDER BY fechaDia DESC
    """)
    fun getDiasConActividad(): Flow<List<Long>>

    @Delete
    suspend fun deleteToma(toma: TomaEntity)

    @Delete
    suspend fun deletePanal(panal: PanalEntity)
}
