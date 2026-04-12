package com.salazar.babytraker.features.tomas_panales.presentation.mvi

data class TomasPanalesState(val isLoading: Boolean = false)
sealed interface TomasPanalesIntent
sealed interface TomasPanalesEffect
