package com.salazar.babytraker.features.tomas_panales.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BabyChangingStation
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.babytraker.core.domain.model.TipoAlimentacion
import com.salazar.babytraker.core.domain.model.TipoPanal
import com.salazar.babytraker.features.tomas_panales.presentation.mvi.TomasPanalesEffect
import com.salazar.babytraker.features.tomas_panales.presentation.mvi.TomasPanalesIntent
import com.salazar.babytraker.features.tomas_panales.presentation.viewmodel.TomasPanalesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TomasPanalesScreen(
    viewModel: TomasPanalesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TomasPanalesEffect.ShowSuccess -> {
                    Toast.makeText(context, "Registro guardado correctamente", Toast.LENGTH_SHORT).show()
                }
                is TomasPanalesEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Registrar Actividad",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // SECCIÓN TOMAS
        SectionCard(title = "Alimentación", icon = Icons.Default.Restaurant) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    TipoAlimentacion.entries.forEachIndexed { index, tipo ->
                        SegmentedButton(
                            selected = state.selectedTipoAlimentacion == tipo,
                            onClick = { viewModel.onIntent(TomasPanalesIntent.UpdateTipoAlimentacion(tipo)) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = TipoAlimentacion.entries.size)
                        ) {
                            Text(tipo.name)
                        }
                    }
                }

                if (state.selectedTipoAlimentacion == TipoAlimentacion.BIBERON) {
                    OutlinedTextField(
                        value = state.cantidadMl,
                        onValueChange = { viewModel.onIntent(TomasPanalesIntent.UpdateCantidad(it)) },
                        label = { Text("Cantidad (ml)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                Button(
                    onClick = { viewModel.onIntent(TomasPanalesIntent.SaveToma) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar Toma")
                }
            }
        }

        // SECCIÓN PAÑALES
        SectionCard(title = "Pañal e Higiene", icon = Icons.Default.BabyChangingStation) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    TipoPanal.entries.forEachIndexed { index, tipo ->
                        SegmentedButton(
                            selected = state.selectedTipoPanal == tipo,
                            onClick = { viewModel.onIntent(TomasPanalesIntent.UpdateTipoPanal(tipo)) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = TipoPanal.entries.size)
                        ) {
                            Text(tipo.name)
                        }
                    }
                }

                Button(
                    onClick = { viewModel.onIntent(TomasPanalesIntent.SavePanal) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar Pañal")
                }
            }
        }

        // NOTAS COMUNES
        OutlinedTextField(
            value = state.nota,
            onValueChange = { viewModel.onIntent(TomasPanalesIntent.UpdateNota(it)) },
            label = { Text("Notas adicionales") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
    }
}

@Composable
fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}
