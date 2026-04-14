package com.salazar.babytraker.features.inicio.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.babytraker.features.inicio.domain.repository.InicioRepository
import com.salazar.babytraker.features.inicio.presentation.mvi.DetalleDiaEffect
import com.salazar.babytraker.features.inicio.presentation.mvi.DetalleDiaIntent
import com.salazar.babytraker.features.inicio.presentation.mvi.DetalleDiaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalleDiaViewModel @Inject constructor(
    private val repository: InicioRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DetalleDiaState())
    val state: StateFlow<DetalleDiaState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DetalleDiaEffect>()
    val effect: SharedFlow<DetalleDiaEffect> = _effect.asSharedFlow()

    fun onIntent(intent: DetalleDiaIntent) {
        when (intent) {
            is DetalleDiaIntent.LoadDetalle -> loadData(intent.fechaDia, intent.babyId)
            DetalleDiaIntent.Refresh -> loadData(_state.value.fechaDia, _state.value.babyId)
        }
    }

    private fun loadData(fechaDia: Long, babyId: Long) {
        _state.update { it.copy(isLoading = true, fechaDia = fechaDia, babyId = babyId) }
        
        viewModelScope.launch {
            combine(
                repository.getTomasPorDia(fechaDia, babyId),
                repository.getPanalesPorDia(fechaDia, babyId)
            ) { tomasRes, panalesRes ->
                val tomas = tomasRes.getOrDefault(emptyList())
                val panales = panalesRes.getOrDefault(emptyList())
                
                _state.update { 
                    it.copy(
                        isLoading = false,
                        tomas = tomas.sortedByDescending { t -> t.timestamp },
                        panales = panales.sortedByDescending { p -> p.timestamp }
                    ) 
                }
            }.collect()
        }
    }
}
