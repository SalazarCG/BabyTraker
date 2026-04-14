package com.salazar.babytraker.features.tomas_panales.data.repository

import com.salazar.babytraker.core.data.local.dao.BabyDao
import com.salazar.babytraker.core.data.local.entities.DailyJournalEntity
import com.salazar.babytraker.core.data.mapper.toEntity
import com.salazar.babytraker.core.domain.model.Panal
import com.salazar.babytraker.core.domain.model.Toma
import com.salazar.babytraker.features.tomas_panales.domain.repository.TomasPanalesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override fun getFotoDelDia(babyId: Long, fechaDia: Long): Flow<String?> =
        babyDao.getJournalByDay(babyId, fechaDia).map { it?.fotoUri }

    override suspend fun updateFotoDelDia(babyId: Long, fechaDia: Long, fotoUri: String) {
        val existing = babyDao.getJournalByDay(babyId, fechaDia)
        // Como Room devuelve Flow, para una operación suspendida necesitamos obtener el valor actual
        // Pero para simplificar y dado que es un @Insert con REPLACE, podemos insertar directamente
        // buscando si hay uno previo o simplemente confiando en el unique index de Room
        babyDao.insertJournal(
            DailyJournalEntity(
                babyId = babyId,
                fechaDia = fechaDia,
                fotoUri = fotoUri
            )
        )
    }
}
