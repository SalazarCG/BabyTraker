package com.salazar.babytraker.core.data.repository

import com.salazar.babytraker.core.data.local.dao.BabyDao
import com.salazar.babytraker.core.data.local.preferences.BabyPreferences
import com.salazar.babytraker.core.data.mapper.toDomain
import com.salazar.babytraker.core.data.mapper.toEntity
import com.salazar.babytraker.core.domain.model.Baby
import com.salazar.babytraker.core.domain.repository.BabyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BabyRepositoryImpl @Inject constructor(
    private val babyDao: BabyDao,
    private val babyPreferences: BabyPreferences
) : BabyRepository {

    override fun getAllBabies(): Flow<Result<List<Baby>>> =
        babyDao.getAllBabies()
            .map { entities -> Result.success(entities.map { it.toDomain() }) }
            .catch { emit(Result.failure(it)) }

    override suspend fun addBaby(baby: Baby) {
        babyDao.insertBaby(baby.toEntity())
    }

    override suspend fun deleteBaby(baby: Baby) {
        babyDao.deleteBabyAndData(baby.toEntity())
        // Si borramos el bebé activo, resetear preferencia
        if (babyPreferences.activeBabyId == baby.id) {
            babyPreferences.activeBabyId = -1L
        }
    }

    override fun getActiveBabyId(): Long = babyPreferences.activeBabyId

    override fun setActiveBabyId(id: Long) {
        babyPreferences.activeBabyId = id
    }
}
