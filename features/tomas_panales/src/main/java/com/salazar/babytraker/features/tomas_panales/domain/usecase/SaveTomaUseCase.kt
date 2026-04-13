package com.salazar.babytraker.features.tomas_panales.domain.usecase

import com.salazar.babytraker.core.domain.model.Toma
import com.salazar.babytraker.features.tomas_panales.domain.repository.TomasPanalesRepository
import javax.inject.Inject

class SaveTomaUseCase @Inject constructor(
    private val repository: TomasPanalesRepository
) {
    suspend operator fun invoke(toma: Toma) {
        repository.saveToma(toma)
    }
}
