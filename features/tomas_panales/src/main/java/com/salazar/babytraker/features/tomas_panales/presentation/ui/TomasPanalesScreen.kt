package com.salazar.babytraker.features.tomas_panales.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.babytraker.core.domain.model.TipoAlimentacion
import com.salazar.babytraker.core.domain.model.TipoPanal
import com.salazar.babytraker.core.ui.components.BabyAvatar
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
    val scrollState = rememberScrollState()
    
    // Cálculo de la animación basado en el scroll
    val density = LocalDensity.current
    val scrollThresholdPx = with(density) { 120.dp.toPx() }
    val scrollProgress = (scrollState.value.toFloat() / scrollThresholdPx).coerceIn(0f, 1f)

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Contenido scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                // Espacio superior para que el contenido no empiece debajo del header fijo
                .padding(top = 80.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // SELECTOR DE BEBÉ (Sincronizado globalmente)
            if (state.babies.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Bebé Seleccionado",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Gray
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.babies) { baby ->
                            BabyAvatar(
                                baby = baby,
                                isSelected = state.selectedBaby?.id == baby.id,
                                onClick = { viewModel.onIntent(TomasPanalesIntent.SelectBaby(baby)) }
                            )
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFF5F5F5))
            }

            if (state.selectedBaby == null) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Por favor, selecciona un bebé para registrar datos.", color = Color.Red)
                }
            } else {
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
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7))
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
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7))
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

        // HEADER PERSISTENTE ANIMADO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = if (scrollProgress > 0.8f) 0.95f else 0f))
                .statusBarsPadding()
                .height(72.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = BiasAlignment(
                horizontalBias = -1f + scrollProgress, // De -1 (Start) a 0 (Center)
                verticalBias = 0f
            )
        ) {
            Text(
                text = "Registrar Actividad",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = lerp(
                        MaterialTheme.typography.headlineMedium.fontSize,
                        MaterialTheme.typography.titleLarge.fontSize,
                        scrollProgress
                    )
                ),
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
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
            containerColor = Color(0xFFF8FDFF)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFF4FC3F7))
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
