package com.salazar.babytraker.features.inicio.domain.usecase

import com.salazar.babytraker.core.domain.model.Baby
import com.salazar.babytraker.core.domain.repository.BabyRepository
import javax.inject.Inject

class DeleteBabyUseCase @Inject constructor(
    private val babyRepository: BabyRepository
) {
    suspend operator fun invoke(baby: Baby) {
        babyRepository.deleteBaby(baby)
    }
}
