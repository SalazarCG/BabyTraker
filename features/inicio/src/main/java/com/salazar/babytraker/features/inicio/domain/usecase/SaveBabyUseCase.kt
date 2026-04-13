package com.salazar.babytraker.features.inicio.domain.usecase

import com.salazar.babytraker.core.domain.model.Baby
import com.salazar.babytraker.features.inicio.domain.repository.InicioRepository
import javax.inject.Inject

class SaveBabyUseCase @Inject constructor(
    private val repository: InicioRepository
) {
    suspend operator fun invoke(baby: Baby) {
        repository.addBaby(baby)
    }
}
