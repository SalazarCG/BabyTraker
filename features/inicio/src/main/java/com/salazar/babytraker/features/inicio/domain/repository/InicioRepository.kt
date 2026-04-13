package com.salazar.babytraker.features.inicio.domain.repository

import com.salazar.babytraker.core.domain.model.Panal
import com.salazar.babytraker.core.domain.model.Toma
import kotlinx.coroutines.flow.Flow

interface InicioRepository {
    fun getDiasConActividad(): Flow<Result<List<Long>>>
    fun getTomasPorDia(fechaDia: Long): Flow<Result<List<Toma>>>
    fun getPanalesPorDia(fechaDia: Long): Flow<Result<List<Panal>>>
}
