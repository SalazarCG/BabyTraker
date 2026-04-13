package com.salazar.babytraker.features.inicio.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.babytraker.core.domain.model.Baby
import com.salazar.babytraker.features.inicio.domain.repository.InicioRepository
import com.salazar.babytraker.features.inicio.domain.usecase.GetResumenDiarioUseCase
import com.salazar.babytraker.features.inicio.presentation.mvi.InicioEffect
import com.salazar.babytraker.features.inicio.presentation.mvi.InicioIntent
import com.salazar.babytraker.features.inicio.presentation.mvi.InicioState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InicioViewModel @Inject constructor(
    private val repository: InicioRepository,
    private val getResumenDiarioUseCase: GetResumenDiarioUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(InicioState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<InicioEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadBabies()
    }

    fun onIntent(intent: InicioIntent) {
        when (intent) {
            InicioIntent.LoadData -> loadBabies()
            is InicioIntent.SelectBaby -> selectBaby(intent.baby)
            is InicioIntent.Search -> _state.update { it.copy(searchQuery = intent.query) }
            InicioIntent.AddBaby -> {
                viewModelScope.launch { _effect.emit(InicioEffect.NavigateToAddBaby) }
            }
        }
    }

    private fun loadBabies() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getAllBabies().collect { result ->
                result.onSuccess { babies ->
                    _state.update { it.copy(babies = babies, isLoading = false) }
                    
                    // Recuperar el último bebé seleccionado de las preferencias
                    val activeBabyId = repository.getActiveBabyId()
                    val activeBaby = babies.find { it.id == activeBabyId } ?: babies.firstOrNull()
                    
                    activeBaby?.let { selectBaby(it) }
                }.onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
            }
        }
    }

    private fun selectBaby(baby: Baby) {
        repository.setActiveBabyId(baby.id)
        _state.update { it.copy(selectedBaby = baby, resumenes = emptyMap(), diasConActividad = emptyList()) }
        loadDashboardData(baby.id)
    }

    private fun loadDashboardData(babyId: Long) {
        viewModelScope.launch {
            repository.getDiasConActividad(babyId).collect { result ->
                result.onSuccess { dias ->
                    _state.update { it.copy(diasConActividad = dias) }
                    observeResumenes(dias, babyId)
                }
            }
        }
    }

    private fun observeResumenes(dias: List<Long>, babyId: Long) {
        dias.forEach { dia ->
            viewModelScope.launch {
                getResumenDiarioUseCase(dia, babyId).collect { result ->
                    result.onSuccess { resumen ->
                        _state.update { currentState ->
                            val newResumenes = currentState.resumenes.toMutableMap()
                            newResumenes[dia] = resumen
                            currentState.copy(resumenes = newResumenes)
                        }
                    }
                }
            }
        }
    }
}
