package com.salazar.babytraker.features.inicio.domain.usecase

import com.salazar.babytraker.features.inicio.domain.model.InicioData
import com.salazar.babytraker.features.inicio.domain.repository.InicioRepository
import javax.inject.Inject

class GetInicioDataUseCase @Inject constructor(
    private val repository: InicioRepository
) {
    operator fun invoke(): Result<InicioData> {
        return Result.success(InicioData())
    }
}
