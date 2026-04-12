package com.salazar.babytraker.features.inicio.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.salazar.babytraker.features.inicio.presentation.mvi.InicioEffect
import com.salazar.babytraker.features.inicio.presentation.mvi.InicioIntent
import com.salazar.babytraker.features.inicio.presentation.mvi.InicioState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class InicioViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(InicioState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<InicioEffect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: InicioIntent) {
        when (intent) {
            InicioIntent.LoadData -> { /* Lógica de carga */ }
        }
    }
}
