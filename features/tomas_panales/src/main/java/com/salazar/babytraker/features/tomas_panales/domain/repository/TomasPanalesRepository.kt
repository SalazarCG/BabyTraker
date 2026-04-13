package com.salazar.babytraker.features.tomas_panales.domain.repository

import com.salazar.babytraker.core.domain.model.Panal
import com.salazar.babytraker.core.domain.model.Toma

interface TomasPanalesRepository {
    suspend fun saveToma(toma: Toma)
    suspend fun savePanal(panal: Panal)
}
