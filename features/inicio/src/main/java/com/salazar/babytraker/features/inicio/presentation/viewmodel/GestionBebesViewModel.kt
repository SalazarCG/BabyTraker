package com.salazar.babytraker.features.inicio.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.babytraker.core.domain.repository.BabyRepository
import com.salazar.babytraker.features.inicio.domain.usecase.DeleteBabyUseCase
import com.salazar.babytraker.features.inicio.presentation.mvi.GestionBebesEffect
import com.salazar.babytraker.features.inicio.presentation.mvi.GestionBebesIntent
import com.salazar.babytraker.features.inicio.presentation.mvi.GestionBebesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestionBebesViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val deleteBabyUseCase: DeleteBabyUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GestionBebesState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<GestionBebesEffect>()
    val effect = _effect.asSharedFlow()

    init {
        onIntent(GestionBebesIntent.LoadBabies)
    }

    fun onIntent(intent: GestionBebesIntent) {
        when (intent) {
            GestionBebesIntent.LoadBabies -> loadBabies()
            is GestionBebesIntent.DeleteBaby -> deleteBaby(intent.baby)
        }
    }

    private fun loadBabies() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            babyRepository.getAllBabies().collect { result ->
                result.fold(
                    onSuccess = { babies ->
                        _state.update { it.copy(babies = babies, isLoading = false) }
                    },
                    onFailure = { error ->
                        _state.update { it.copy(isLoading = false) }
                        _effect.emit(GestionBebesEffect.ShowError(error.message ?: "Error desconocido"))
                    }
                )
            }
        }
    }

    private fun deleteBaby(baby: com.salazar.babytraker.core.domain.model.Baby) {
        viewModelScope.launch {
            try {
                deleteBabyUseCase(baby)
                _effect.emit(GestionBebesEffect.BabyDeleted)
            } catch (e: Exception) {
                _effect.emit(GestionBebesEffect.ShowError("No se pudo eliminar: ${e.message}"))
            }
        }
    }
}
