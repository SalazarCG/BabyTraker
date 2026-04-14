package com.salazar.babytraker.features.tomas_panales.presentation.mvi

import com.salazar.babytraker.core.domain.model.Baby
import com.salazar.babytraker.core.domain.model.TipoAlimentacion
import com.salazar.babytraker.core.domain.model.TipoPanal

data class TomasPanalesState(
    val isLoading: Boolean = false,
    val babies: List<Baby> = emptyList(),
    val selectedBaby: Baby? = null,
    val babyId: Long? = null,
    val selectedTipoAlimentacion: TipoAlimentacion = TipoAlimentacion.PECHO,
    val selectedTipoPanal: TipoPanal = TipoPanal.PIPI,
    val cantidadMl: String = "",
    val notaAlimentacion: String = "",
    val notaPanal: String = "",
    val horaToma: Long? = null, // null significa "Ahora"
    val horaPanal: Long? = null, // null significa "Ahora"
    val fotoDelDia: String? = null,
    val isSaved: Boolean = false
)

sealed interface TomasPanalesIntent {
    data class Init(val babyId: Long) : TomasPanalesIntent
    data class SelectBaby(val baby: Baby) : TomasPanalesIntent
    data class UpdateTipoAlimentacion(val tipo: TipoAlimentacion) : TomasPanalesIntent
    data class UpdateTipoPanal(val tipo: TipoPanal) : TomasPanalesIntent
    data class UpdateCantidad(val cantidad: String) : TomasPanalesIntent
    data class UpdateNotaAlimentacion(val nota: String) : TomasPanalesIntent
    data class UpdateNotaPanal(val nota: String) : TomasPanalesIntent
    data class UpdateHoraToma(val timestamp: Long?) : TomasPanalesIntent
    data class UpdateHoraPanal(val timestamp: Long?) : TomasPanalesIntent
    data class UpdateFotoDelDia(val uri: String) : TomasPanalesIntent
    data object SaveToma : TomasPanalesIntent
    data object SavePanal : TomasPanalesIntent
    data object ResetState : TomasPanalesIntent
}

sealed interface TomasPanalesEffect {
    data object ShowSuccess : TomasPanalesEffect
    data class ShowError(val message: String) : TomasPanalesEffect
}
