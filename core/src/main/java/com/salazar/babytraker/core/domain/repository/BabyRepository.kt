package com.salazar.babytraker.core.domain.repository

import com.salazar.babytraker.core.domain.model.Baby
import kotlinx.coroutines.flow.Flow

interface BabyRepository {
    fun getAllBabies(): Flow<Result<List<Baby>>>
    suspend fun addBaby(baby: Baby)
    suspend fun deleteBaby(baby: Baby)
    fun getActiveBabyId(): Long
    fun setActiveBabyId(id: Long)
}
