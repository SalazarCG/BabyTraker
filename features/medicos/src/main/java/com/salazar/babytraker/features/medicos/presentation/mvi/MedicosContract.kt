package com.salazar.babytraker.features.medicos.presentation.mvi

data class MedicosState(val isLoading: Boolean = false)
sealed interface MedicosIntent
sealed interface MedicosEffect
