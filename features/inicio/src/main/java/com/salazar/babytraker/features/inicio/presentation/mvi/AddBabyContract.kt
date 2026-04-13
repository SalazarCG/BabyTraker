package com.salazar.babytraker.features.inicio.presentation.mvi

data class AddBabyState(
    val nombre: String = "",
    val fechaNacimiento: Long? = null,
    val fotoUri: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)

sealed interface AddBabyIntent {
    data class UpdateNombre(val nombre: String) : AddBabyIntent
    data class UpdateFechaNacimiento(val fecha: Long) : AddBabyIntent
    data class UpdateFotoUri(val uri: String) : AddBabyIntent
    data object SaveBaby : AddBabyIntent
}

sealed interface AddBabyEffect {
    data object NavigateBack : AddBabyEffect
    data class ShowError(val message: String) : AddBabyEffect
}
