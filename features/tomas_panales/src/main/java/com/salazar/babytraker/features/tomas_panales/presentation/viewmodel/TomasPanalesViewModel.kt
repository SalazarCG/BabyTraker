package com.salazar.babytraker.features.tomas_panales.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.babytraker.core.data.local.preferences.BabyPreferences
import com.salazar.babytraker.core.domain.model.Panal
import com.salazar.babytraker.core.domain.model.Toma
import com.salazar.babytraker.core.domain.repository.BabyRepository
import com.salazar.babytraker.core.utils.ImageStorageManager
import com.salazar.babytraker.features.tomas_panales.domain.repository.TomasPanalesRepository
import com.salazar.babytraker.features.tomas_panales.domain.usecase.SavePanalUseCase
import com.salazar.babytraker.features.tomas_panales.domain.usecase.SaveTomaUseCase
import com.salazar.babytraker.features.tomas_panales.presentation.mvi.TomasPanalesEffect
import com.salazar.babytraker.features.tomas_panales.presentation.mvi.TomasPanalesIntent
import com.salazar.babytraker.features.tomas_panales.presentation.mvi.TomasPanalesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TomasPanalesViewModel @Inject constructor(
    private val saveTomaUseCase: SaveTomaUseCase,
    private val savePanalUseCase: SavePanalUseCase,
    private val tomasPanalesRepository: TomasPanalesRepository,
    private val babyPreferences: BabyPreferences,
    private val babyRepository: BabyRepository,
    private val imageStorageManager: ImageStorageManager
) : ViewModel() {

    private val _userIntent = MutableSharedFlow<TomasPanalesIntent>()
    
    private val _babiesFlow = babyRepository.getAllBabies()

    val state: StateFlow<TomasPanalesState> = combine(
        _babiesFlow,
        babyPreferences.activeBabyIdFlow,
        _userIntent.onStart { emit(TomasPanalesIntent.ResetState) }
            .scan(TomasPanalesState()) { currentState, intent ->
                reduce(currentState, intent)
            }
    ) { babiesResult, activeId, currentState ->
        val babies = babiesResult.getOrNull() ?: emptyList()
        val selected = babies.find { it.id == activeId }
        
        // Cargar foto del día si hay un bebé seleccionado
        val fotoDia = selected?.let { 
            tomasPanalesRepository.getFotoDelDia(it.id, getNormalizedDate(System.currentTimeMillis())).firstOrNull() 
        }

        currentState.copy(
            babies = babies,
            selectedBaby = selected,
            babyId = selected?.id,
            fotoDelDia = currentState.fotoDelDia ?: fotoDia
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TomasPanalesState(isLoading = true)
    )

    private val _effect = MutableSharedFlow<TomasPanalesEffect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: TomasPanalesIntent) {
        viewModelScope.launch {
            when (intent) {
                TomasPanalesIntent.SaveToma -> saveToma()
                TomasPanalesIntent.SavePanal -> savePanal()
                is TomasPanalesIntent.SelectBaby -> {
                    babyRepository.setActiveBabyId(intent.baby.id)
                }
                is TomasPanalesIntent.UpdateFotoDelDia -> {
                    val babyId = babyPreferences.activeBabyId
                    if (babyId != -1L) {
                        // PERSISTENCIA PERMANENTE: Guardamos la imagen en almacenamiento interno
                        val permanentUri = imageStorageManager.saveImageToInternalStorage(Uri.parse(intent.uri))
                        
                        if (permanentUri != null) {
                            tomasPanalesRepository.updateFotoDelDia(
                                babyId, 
                                getNormalizedDate(System.currentTimeMillis()), 
                                permanentUri
                            )
                            _userIntent.emit(intent.copy(uri = permanentUri))
                        } else {
                            _effect.emit(TomasPanalesEffect.ShowError("Error al procesar la imagen"))
                        }
                    }
                }
                else -> _userIntent.emit(intent)
            }
        }
    }

    private fun reduce(currentState: TomasPanalesState, intent: TomasPanalesIntent): TomasPanalesState {
        return when (intent) {
            is TomasPanalesIntent.UpdateTipoAlimentacion -> currentState.copy(selectedTipoAlimentacion = intent.tipo)
            is TomasPanalesIntent.UpdateTipoPanal -> currentState.copy(selectedTipoPanal = intent.tipo)
            is TomasPanalesIntent.UpdateCantidad -> currentState.copy(cantidadMl = intent.cantidad)
            is TomasPanalesIntent.UpdateNotaAlimentacion -> currentState.copy(notaAlimentacion = intent.nota)
            is TomasPanalesIntent.UpdateNotaPanal -> currentState.copy(notaPanal = intent.nota)
            is TomasPanalesIntent.UpdateFotoDelDia -> currentState.copy(fotoDelDia = intent.uri)
            is TomasPanalesIntent.UpdateHoraToma -> currentState.copy(horaToma = intent.timestamp)
            is TomasPanalesIntent.UpdateHoraPanal -> currentState.copy(horaPanal = intent.timestamp)
            TomasPanalesIntent.ResetState -> currentState.copy(
                cantidadMl = "",
                notaAlimentacion = "",
                notaPanal = "",
                horaToma = null,
                horaPanal = null,
                isSaved = false
            )
            else -> currentState
        }
    }

    private fun saveToma() {
        val babyId = babyPreferences.activeBabyId
        if (babyId == -1L) {
            viewModelScope.launch { _effect.emit(TomasPanalesEffect.ShowError("Selecciona un bebé primero")) }
            return
        }

        viewModelScope.launch {
            try {
                val current = state.value
                val timestamp = current.horaToma ?: System.currentTimeMillis()
                val toma = Toma(
                    babyId = babyId,
                    timestamp = timestamp,
                    fechaDia = getNormalizedDate(timestamp),
                    tipo = current.selectedTipoAlimentacion,
                    cantidad = current.cantidadMl.toIntOrNull(),
                    nota = current.notaAlimentacion
                )
                saveTomaUseCase(toma)
                _effect.emit(TomasPanalesEffect.ShowSuccess)
                onIntent(TomasPanalesIntent.ResetState)
            } catch (e: Exception) {
                _effect.emit(TomasPanalesEffect.ShowError("Error: ${e.message}"))
            }
        }
    }

    private fun savePanal() {
        val babyId = babyPreferences.activeBabyId
        if (babyId == -1L) return

        viewModelScope.launch {
            try {
                val current = state.value
                val timestamp = current.horaPanal ?: System.currentTimeMillis()
                val panal = Panal(
                    babyId = babyId,
                    timestamp = timestamp,
                    fechaDia = getNormalizedDate(timestamp),
                    tipo = current.selectedTipoPanal,
                    nota = current.notaPanal
                )
                savePanalUseCase(panal)
                _effect.emit(TomasPanalesEffect.ShowSuccess)
                onIntent(TomasPanalesIntent.ResetState)
            } catch (e: Exception) {
                _effect.emit(TomasPanalesEffect.ShowError("Error: ${e.message}"))
            }
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
