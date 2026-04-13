package com.salazar.babytraker.features.tomas_panales.data.repository

import com.salazar.babytraker.core.data.local.dao.BabyDao
import com.salazar.babytraker.core.data.mapper.toEntity
import com.salazar.babytraker.core.domain.model.Panal
import com.salazar.babytraker.core.domain.model.Toma
import com.salazar.babytraker.features.tomas_panales.domain.repository.TomasPanalesRepository
import javax.inject.Inject

class TomasPanalesRepositoryImpl @Inject constructor(
    private val babyDao: BabyDao
) : TomasPanalesRepository {
    override suspend fun saveToma(toma: Toma) {
        babyDao.insertToma(toma.toEntity())
    }

    override suspend fun savePanal(panal: Panal) {
        babyDao.insertPanal(panal.toEntity())
    }
}
