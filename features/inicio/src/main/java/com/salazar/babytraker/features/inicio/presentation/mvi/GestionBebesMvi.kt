package com.salazar.babytraker.features.inicio.presentation.mvi

import com.salazar.babytraker.core.domain.model.Baby

data class GestionBebesState(
    val babies: List<Baby> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface GestionBebesIntent {
    data object LoadBabies : GestionBebesIntent
    data class DeleteBaby(val baby: Baby) : GestionBebesIntent
}

sealed interface GestionBebesEffect {
    data class ShowError(val message: String) : GestionBebesEffect
    data object BabyDeleted : GestionBebesEffect
}
