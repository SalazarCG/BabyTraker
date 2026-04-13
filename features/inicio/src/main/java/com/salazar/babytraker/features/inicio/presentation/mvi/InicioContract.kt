package com.salazar.babytraker.features.inicio.presentation.mvi

import com.salazar.babytraker.core.domain.model.Baby
import com.salazar.babytraker.core.domain.model.ResumenDia

data class InicioState(
    val isLoading: Boolean = false,
    val babies: List<Baby> = emptyList(),
    val selectedBaby: Baby? = null,
    val diasConActividad: List<Long> = emptyList(),
    val resumenes: Map<Long, ResumenDia> = emptyMap(),
    val searchQuery: String = "",
    val error: String? = null
)

sealed interface InicioIntent {
    data object LoadData : InicioIntent
    data class SelectBaby(val baby: Baby) : InicioIntent
    data class Search(val query: String) : InicioIntent
    data object AddBaby : InicioIntent
}

sealed interface InicioEffect {
    data class ShowError(val message: String) : InicioEffect
    data object NavigateToAddBaby : InicioEffect
}
