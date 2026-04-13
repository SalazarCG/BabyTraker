package com.salazar.babytraker.features.inicio.data.repository

import com.salazar.babytraker.core.data.local.dao.BabyDao
import com.salazar.babytraker.core.data.mapper.toDomain
import com.salazar.babytraker.core.data.mapper.toEntity
import com.salazar.babytraker.core.domain.model.Baby
import com.salazar.babytraker.core.domain.model.Panal
import com.salazar.babytraker.core.domain.model.Toma
import com.salazar.babytraker.features.inicio.domain.repository.InicioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InicioRepositoryImpl @Inject constructor(
    private val babyDao: BabyDao
) : InicioRepository {

    override fun getAllBabies(): Flow<Result<List<Baby>>> =
        babyDao.getAllBabies()
            .map { entities -> Result.success(entities.map { it.toDomain() }) }
            .catch { emit(Result.failure(it)) }

    override fun getDiasConActividad(babyId: Long): Flow<Result<List<Long>>> =
        babyDao.getDiasConActividad(babyId)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }

    override fun getTomasPorDia(fechaDia: Long, babyId: Long): Flow<Result<List<Toma>>> =
        babyDao.getTomasPorDia(fechaDia, babyId)
            .map { entities -> Result.success(entities.map { it.toDomain() }) }
            .catch { emit(Result.failure(it)) }

    override fun getPanalesPorDia(fechaDia: Long, babyId: Long): Flow<Result<List<Panal>>> =
        babyDao.getPanalesPorDia(fechaDia, babyId)
            .map { entities -> Result.success(entities.map { it.toDomain() }) }
            .catch { emit(Result.failure(it)) }

    override suspend fun addBaby(baby: Baby) {
        babyDao.insertBaby(baby.toEntity())
    }
}
