package com.salazar.babytraker.features.inicio.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.babytraker.core.data.local.preferences.BabyPreferences
import com.salazar.babytraker.core.domain.model.Baby
import com.salazar.babytraker.core.domain.model.ResumenDia
import com.salazar.babytraker.core.domain.repository.BabyRepository
import com.salazar.babytraker.features.inicio.domain.repository.InicioRepository
import com.salazar.babytraker.features.inicio.domain.usecase.GetResumenDiarioUseCase
import com.salazar.babytraker.features.inicio.presentation.mvi.InicioEffect
import com.salazar.babytraker.features.inicio.presentation.mvi.InicioIntent
import com.salazar.babytraker.features.inicio.presentation.mvi.InicioState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InicioViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val inicioRepository: InicioRepository,
    private val babyPreferences: BabyPreferences,
    private val getResumenDiarioUseCase: GetResumenDiarioUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    // 1. Observar la lista de bebés
    private val _babiesFlow = babyRepository.getAllBabies().map { it.getOrDefault(emptyList()) }

    // 2. Observar el ID del bebé activo desde preferencias
    private val _activeBabyIdFlow = babyPreferences.activeBabyIdFlow

    // 3. Flujo del bebé seleccionado actual
    private val _selectedBabyFlow = combine(_babiesFlow, _activeBabyIdFlow) { babies, activeId ->
        val selected = babies.find { it.id == activeId } ?: babies.firstOrNull()
        // Si no hay ID activo pero hay bebés, marcamos el primero como activo
        if (activeId == -1L && selected != null) {
            babyRepository.setActiveBabyId(selected.id)
        }
        selected
    }

    // 4. Flujo de resumenes (Estadísticas) reactivo
    private val _resumenesFlow = _selectedBabyFlow.flatMapLatest { baby ->
        if (baby == null) return@flatMapLatest flowOf(emptyList<ResumenDia>())
        
        inicioRepository.getDiasConActividad(baby.id).flatMapLatest { result ->
            val dias = result.getOrNull() ?: emptyList()
            if (dias.isEmpty()) return@flatMapLatest flowOf(emptyList())

            val flows = dias.map { dia ->
                getResumenDiarioUseCase(dia, baby.id).map { it.getOrNull() }
            }
            combine(flows) { it.filterNotNull().toList() }
        }
    }

    // 5. Estado Único y Atómico
    val state: StateFlow<InicioState> = combine(
        _babiesFlow,
        _selectedBabyFlow,
        _resumenesFlow,
        _searchQuery
    ) { babies, selected, resumenes, query ->
        InicioState(
            babies = babies,
            selectedBaby = selected,
            diasConActividad = resumenes.map { it.fechaDia },
            resumenes = resumenes.associateBy { it.fechaDia },
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InicioState(isLoading = true)
    )

    private val _effect = MutableSharedFlow<InicioEffect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: InicioIntent) {
        when (intent) {
            is InicioIntent.SelectBaby -> {
                babyRepository.setActiveBabyId(intent.baby.id)
            }
            is InicioIntent.Search -> _searchQuery.value = intent.query
            InicioIntent.AddBaby -> {
                viewModelScope.launch { _effect.emit(InicioEffect.NavigateToAddBaby) }
            }
            InicioIntent.LoadData -> { /* Reactivo */ }
        }
    }
}
