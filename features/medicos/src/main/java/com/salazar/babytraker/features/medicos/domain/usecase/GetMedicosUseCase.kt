package com.salazar.babytraker.features.medicos.domain.usecase

import com.salazar.babytraker.features.medicos.domain.repository.MedicosRepository
import javax.inject.Inject

class GetMedicosUseCase @Inject constructor(
    private val repository: MedicosRepository
) {
}
