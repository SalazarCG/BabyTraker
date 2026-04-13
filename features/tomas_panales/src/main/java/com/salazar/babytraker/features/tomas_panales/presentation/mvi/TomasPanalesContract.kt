package com.salazar.babytraker.features.tomas_panales.presentation.mvi

import com.salazar.babytraker.core.domain.model.TipoAlimentacion
import com.salazar.babytraker.core.domain.model.TipoPanal

data class TomasPanalesState(
    val isLoading: Boolean = false,
    val babyId: Long? = null,
    val selectedTipoAlimentacion: TipoAlimentacion = TipoAlimentacion.PECHO,
    val selectedTipoPanal: TipoPanal = TipoPanal.PIPI,
    val cantidadMl: String = "",
    val nota: String = "",
    val isSaved: Boolean = false
)

sealed interface TomasPanalesIntent {
    data class Init(val babyId: Long) : TomasPanalesIntent
    data class UpdateTipoAlimentacion(val tipo: TipoAlimentacion) : TomasPanalesIntent
    data class UpdateTipoPanal(val tipo: TipoPanal) : TomasPanalesIntent
    data class UpdateCantidad(val cantidad: String) : TomasPanalesIntent
    data class UpdateNota(val nota: String) : TomasPanalesIntent
    data object SaveToma : TomasPanalesIntent
    data object SavePanal : TomasPanalesIntent
    data object ResetState : TomasPanalesIntent
}

sealed interface TomasPanalesEffect {
    data object ShowSuccess : TomasPanalesEffect
    data class ShowError(val message: String) : TomasPanalesEffect
}
