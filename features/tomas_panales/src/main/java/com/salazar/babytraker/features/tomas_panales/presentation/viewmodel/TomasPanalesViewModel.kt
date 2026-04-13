package com.salazar.babytraker.features.tomas_panales.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.babytraker.core.domain.model.Panal
import com.salazar.babytraker.core.domain.model.Toma
import com.salazar.babytraker.features.tomas_panales.domain.usecase.SavePanalUseCase
import com.salazar.babytraker.features.tomas_panales.domain.usecase.SaveTomaUseCase
import com.salazar.babytraker.features.tomas_panales.presentation.mvi.TomasPanalesEffect
import com.salazar.babytraker.features.tomas_panales.presentation.mvi.TomasPanalesIntent
import com.salazar.babytraker.features.tomas_panales.presentation.mvi.TomasPanalesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TomasPanalesViewModel @Inject constructor(
    private val saveTomaUseCase: SaveTomaUseCase,
    private val savePanalUseCase: SavePanalUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TomasPanalesState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<TomasPanalesEffect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: TomasPanalesIntent) {
        when (intent) {
            is TomasPanalesIntent.Init -> _state.update { it.copy(babyId = intent.babyId) }
            is TomasPanalesIntent.UpdateTipoAlimentacion -> _state.update { it.copy(selectedTipoAlimentacion = intent.tipo) }
            is TomasPanalesIntent.UpdateTipoPanal -> _state.update { it.copy(selectedTipoPanal = intent.tipo) }
            is TomasPanalesIntent.UpdateCantidad -> _state.update { it.copy(cantidadMl = intent.cantidad) }
            is TomasPanalesIntent.UpdateNota -> _state.update { it.copy(nota = intent.nota) }
            TomasPanalesIntent.SaveToma -> saveToma()
            TomasPanalesIntent.SavePanal -> savePanal()
            TomasPanalesIntent.ResetState -> _state.update { 
                TomasPanalesState(babyId = _state.value.babyId) 
            }
        }
    }

    private fun saveToma() {
        val babyId = _state.value.babyId ?: return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val toma = Toma(
                babyId = babyId,
                timestamp = now,
                fechaDia = getNormalizedDate(now),
                tipo = _state.value.selectedTipoAlimentacion,
                cantidad = _state.value.cantidadMl.toIntOrNull(),
                nota = _state.value.nota
            )
            saveTomaUseCase(toma)
            _effect.emit(TomasPanalesEffect.ShowSuccess)
            onIntent(TomasPanalesIntent.ResetState)
        }
    }

    private fun savePanal() {
        val babyId = _state.value.babyId ?: return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val panal = Panal(
                babyId = babyId,
                timestamp = now,
                fechaDia = getNormalizedDate(now),
                tipo = _state.value.selectedTipoPanal,
                nota = _state.value.nota
            )
            savePanalUseCase(panal)
            _effect.emit(TomasPanalesEffect.ShowSuccess)
            onIntent(TomasPanalesIntent.ResetState)
        }
    }

    private fun getNormalizedDate(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
