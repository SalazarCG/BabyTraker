package com.salazar.babytraker.features.tomas_panales.domain.usecase

import com.salazar.babytraker.core.domain.model.Panal
import com.salazar.babytraker.features.tomas_panales.domain.repository.TomasPanalesRepository
import javax.inject.Inject

class SavePanalUseCase @Inject constructor(
    private val repository: TomasPanalesRepository
) {
    suspend operator fun invoke(panal: Panal) {
        repository.savePanal(panal)
    }
}
