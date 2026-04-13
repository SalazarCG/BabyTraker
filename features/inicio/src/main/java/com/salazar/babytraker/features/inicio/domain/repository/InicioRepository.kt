package com.salazar.babytraker.features.inicio.domain.repository

import com.salazar.babytraker.core.domain.model.Baby
import com.salazar.babytraker.core.domain.model.Panal
import com.salazar.babytraker.core.domain.model.Toma
import kotlinx.coroutines.flow.Flow

interface InicioRepository {
    fun getAllBabies(): Flow<Result<List<Baby>>>
    fun getDiasConActividad(babyId: Long): Flow<Result<List<Long>>>
    fun getTomasPorDia(fechaDia: Long, babyId: Long): Flow<Result<List<Toma>>>
    fun getPanalesPorDia(fechaDia: Long, babyId: Long): Flow<Result<List<Panal>>>
    suspend fun addBaby(baby: Baby)
    fun getActiveBabyId(): Long
    fun setActiveBabyId(id: Long)
}
