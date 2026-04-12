package com.salazar.babytraker.features.inicio.presentation.mvi

import com.salazar.babytraker.features.inicio.domain.model.InicioData

data class InicioState(
    val isLoading: Boolean = false,
    val data: InicioData? = null,
    val error: String? = null
)

sealed interface InicioIntent {
    data object LoadData : InicioIntent
}

sealed interface InicioEffect {
    data class ShowToast(val message: String) : InicioEffect
}
