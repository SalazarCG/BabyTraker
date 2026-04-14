package com.salazar.babytraker.features.inicio.presentation.mvi

import com.salazar.babytraker.core.domain.model.Panal
import com.salazar.babytraker.core.domain.model.Toma

data class DetalleDiaState(
    val isLoading: Boolean = false,
    val fechaDia: Long = 0,
    val babyId: Long = 0,
    val tomas: List<Toma> = emptyList(),
    val panales: List<Panal> = emptyList(),
    val error: String? = null
)

sealed interface DetalleDiaIntent {
    data class LoadDetalle(val fechaDia: Long, val babyId: Long) : DetalleDiaIntent
    data object Refresh : DetalleDiaIntent
}

sealed interface DetalleDiaEffect {
    data class ShowError(val message: String) : DetalleDiaEffect
}
