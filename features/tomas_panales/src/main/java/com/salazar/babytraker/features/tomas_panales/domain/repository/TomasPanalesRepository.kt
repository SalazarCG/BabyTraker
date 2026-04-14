package com.salazar.babytraker.features.tomas_panales.domain.repository

import com.salazar.babytraker.core.domain.model.Panal
import com.salazar.babytraker.core.domain.model.Toma
import kotlinx.coroutines.flow.Flow

interface TomasPanalesRepository {
    suspend fun saveToma(toma: Toma)
    suspend fun savePanal(panal: Panal)
    fun getFotoDelDia(babyId: Long, fechaDia: Long): Flow<String?>
    suspend fun updateFotoDelDia(babyId: Long, fechaDia: Long, fotoUri: String)
}
