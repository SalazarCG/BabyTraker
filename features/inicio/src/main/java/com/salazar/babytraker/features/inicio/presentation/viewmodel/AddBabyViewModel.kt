package com.salazar.babytraker.features.inicio.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.babytraker.core.domain.model.Baby
import com.salazar.babytraker.features.inicio.domain.usecase.SaveBabyUseCase
import com.salazar.babytraker.features.inicio.presentation.mvi.AddBabyEffect
import com.salazar.babytraker.features.inicio.presentation.mvi.AddBabyIntent
import com.salazar.babytraker.features.inicio.presentation.mvi.AddBabyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBabyViewModel @Inject constructor(
    private val saveBabyUseCase: SaveBabyUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddBabyState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AddBabyEffect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: AddBabyIntent) {
        when (intent) {
            is AddBabyIntent.UpdateNombre -> _state.update { it.copy(nombre = intent.nombre) }
            is AddBabyIntent.UpdateFechaNacimiento -> _state.update { it.copy(fechaNacimiento = intent.fecha) }
            is AddBabyIntent.UpdateFotoUri -> _state.update { it.copy(fotoUri = intent.uri) }
            AddBabyIntent.SaveBaby -> saveBaby()
        }
    }

    private fun saveBaby() {
        val nombre = _state.value.nombre
        val fecha = _state.value.fechaNacimiento
        
        if (nombre.isBlank() || fecha == null) {
            viewModelScope.launch { _effect.emit(AddBabyEffect.ShowError("Completa todos los campos")) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val baby = Baby(
                nombre = nombre,
                fechaNacimiento = fecha,
                fotoUri = _state.value.fotoUri
            )
            saveBabyUseCase(baby)
            _state.update { it.copy(isLoading = false, isSaved = true) }
            _effect.emit(AddBabyEffect.NavigateBack)
        }
    }
}
