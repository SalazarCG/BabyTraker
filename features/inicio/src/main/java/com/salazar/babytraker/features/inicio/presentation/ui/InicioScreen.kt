package com.salazar.babytraker.features.inicio.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.salazar.babytraker.core.domain.model.ResumenDia
import com.salazar.babytraker.core.ui.components.BabyAvatar
import com.salazar.babytraker.features.inicio.presentation.mvi.InicioEffect
import com.salazar.babytraker.features.inicio.presentation.mvi.InicioIntent
import com.salazar.babytraker.features.inicio.presentation.viewmodel.InicioViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InicioScreen(
    viewModel: InicioViewModel = hiltViewModel(),
    onNavigateToAddBaby: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is InicioEffect.NavigateToAddBaby -> onNavigateToAddBaby()
                is InicioEffect.ShowError -> { }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menú",
            modifier = Modifier.size(24.dp),
            tint = Color.LightGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Estadísticas Diarias",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Button(
                onClick = { viewModel.onIntent(InicioIntent.AddBaby) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE0F2F1),
                    contentColor = Color(0xFF4FC3F7)
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de Bebés
        if (state.babies.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(state.babies, key = { it.id }) { baby ->
                    BabyAvatar(
                        baby = baby,
                        isSelected = state.selectedBaby?.id == baby.id,
                        onClick = { viewModel.onIntent(InicioIntent.SelectBaby(baby)) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.onIntent(InicioIntent.Search(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)),
            placeholder = { Text("Buscar", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (state.isLoading && state.diasConActividad.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4FC3F7))
            }
        } else if (state.diasConActividad.isEmpty()) {
            EmptyState(hasBabies = state.babies.isNotEmpty())
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(state.diasConActividad, key = { it }) { dia ->
                    val resumen = state.resumenes[dia]
                    // Si el resumen no ha cargado aún para este día, pasamos null pero la UI no rompe
                    StatCard(dia = dia, resumen = resumen, babyProfilePhoto = state.selectedBaby?.fotoUri)
                }
            }
        }
    }
}

@Composable
fun StatCard(dia: Long, resumen: ResumenDia?, babyProfilePhoto: String?) {
    val dateStr = remember(dia) {
        SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "ES")).format(Date(dia))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navegar al detalle */ }
    ) {
        // IMAGEN: Prioridad -> Foto del Diario (capturada ese día) | Fallback -> Foto de Perfil del Bebé
        AsyncImage(
            model = resumen?.fotoUri ?: babyProfilePhoto ?: "https://via.placeholder.com/150",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        val totalPanales = (resumen?.totalPipis ?: 0) + (resumen?.totalCacas ?: 0) + (resumen?.totalMixtos ?: 0)
        Text(
            text = "TOMAS: ${resumen?.totalTomas ?: 0},00 | PAÑALES: $totalPanales,00",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFB3E5FC),
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = dateStr,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = resumen?.comentario ?: "Sin comentarios hoy",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            maxLines = 2
        )
    }
}

@Composable
fun EmptyState(hasBabies: Boolean) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = if (hasBabies) 
                "No hay actividad registrada para este bebé aún." 
                else "No hay bebés registrados.\n¡Pulsa 'Add' para empezar!",
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}
