package com.salazar.babytraker.features.tomas_panales.domain.usecase

import com.salazar.babytraker.features.tomas_panales.domain.repository.TomasPanalesRepository
import javax.inject.Inject

class GetTomasPanalesUseCase @Inject constructor(
    private val repository: TomasPanalesRepository
) {
}
