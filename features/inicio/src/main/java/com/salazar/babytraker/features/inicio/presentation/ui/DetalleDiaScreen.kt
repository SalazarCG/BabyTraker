package com.salazar.babytraker.features.inicio.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BabyChangingStation
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.babytraker.core.domain.model.Panal
import com.salazar.babytraker.core.domain.model.Toma
import com.salazar.babytraker.features.inicio.presentation.mvi.DetalleDiaIntent
import com.salazar.babytraker.features.inicio.presentation.viewmodel.DetalleDiaViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DetalleDiaScreen(
    fechaDia: Long,
    babyId: Long,
    onNavigateBack: () -> Unit,
    viewModel: DetalleDiaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    
    val scrollProgress = if (scrollState.maxValue > 0) {
        (scrollState.value.toFloat() / 250f).coerceIn(0f, 1f)
    } else 0f

    LaunchedEffect(fechaDia, babyId) {
        viewModel.onIntent(DetalleDiaIntent.LoadDetalle(fechaDia, babyId))
    }

    val dateStr = remember(fechaDia) {
        SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES")).format(Date(fechaDia))
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(top = 110.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = dateStr,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4FC3F7))
                }
            } else {
                // SECCIÓN TOMAS
                DetalleSectionCard(title = "Tomas del día", icon = Icons.Default.Restaurant) {
                    if (state.tomas.isEmpty()) {
                        Text("No hay tomas registradas", color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))
                    } else {
                        Column {
                            state.tomas.forEach { toma ->
                                ItemActividadDetalle(
                                    hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(toma.timestamp)),
                                    titulo = "Toma - ${toma.tipo.name}",
                                    subtitulo = toma.cantidad?.let { "$it ml" } ?: "Pecho",
                                    nota = toma.nota,
                                    icon = Icons.Default.Restaurant,
                                    color = Color(0xFFE3F2FD)
                                )
                                if (toma != state.tomas.last()) HorizontalDivider(color = Color(0xFFF5F5F5), modifier = Modifier.padding(start = 56.dp))
                            }
                        }
                    }
                }

                // SECCIÓN PAÑALES
                DetalleSectionCard(title = "Pañales e Higiene", icon = Icons.Default.BabyChangingStation) {
                    if (state.panales.isEmpty()) {
                        Text("No hay pañales registrados", color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))
                    } else {
                        Column {
                            state.panales.forEach { panal ->
                                ItemActividadDetalle(
                                    hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(panal.timestamp)),
                                    titulo = "Pañal - ${panal.tipo.name}",
                                    subtitulo = "",
                                    nota = panal.nota,
                                    icon = Icons.Default.BabyChangingStation,
                                    color = Color(0xFFF1F8E9)
                                )
                                if (panal != state.panales.last()) HorizontalDivider(color = Color(0xFFF5F5F5), modifier = Modifier.padding(start = 56.dp))
                            }
                        }
                    }
                }
            }
        }

        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = if (scrollProgress > 0.8f) 0.95f else 1f))
                .statusBarsPadding()
                .height(80.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.Black)
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = BiasAlignment(horizontalBias = -1f + scrollProgress, verticalBias = 0f)
                ) {
                    Text(
                        text = "Detalles",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = lerp(32.sp, 22.sp, scrollProgress)
                        ),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun DetalleSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFFF8FDFF)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFF4FC3F7), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun ItemActividadDetalle(
    hora: String,
    titulo: String,
    subtitulo: String,
    nota: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    var isExpanded by remember { mutableStateOf(false) }
    val hasNote = !nota.isNullOrBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = hasNote) { isExpanded = !isExpanded }
            .padding(vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = hora,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.width(52.dp)
            )
            
            Surface(
                color = color,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.DarkGray)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = titulo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                if (subtitulo.isNotBlank()) {
                    Text(text = subtitulo, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            if (hasNote) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = null,
                    tint = if (isExpanded) Color(0xFF4FC3F7) else Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 104.dp, top = 8.dp, bottom = 4.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Nota:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Text(
                    text = nota ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}
