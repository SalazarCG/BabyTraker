package com.salazar.babytraker.features.inicio.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.babytraker.core.domain.model.Baby
import com.salazar.babytraker.core.ui.components.BabyAvatar
import com.salazar.babytraker.features.inicio.presentation.mvi.GestionBebesEffect
import com.salazar.babytraker.features.inicio.presentation.mvi.GestionBebesIntent
import com.salazar.babytraker.features.inicio.presentation.viewmodel.GestionBebesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionBebesScreen(
    viewModel: GestionBebesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAddBaby: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var babyToDelete by remember { mutableStateOf<Baby?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                GestionBebesEffect.BabyDeleted -> {
                    babyToDelete = null
                }
                is GestionBebesEffect.ShowError -> {
                    // Aquí podrías mostrar un snackbar
                }
            }
        }
    }

    if (babyToDelete != null) {
        AlertDialog(
            onDismissRequest = { babyToDelete = null },
            title = { Text("Eliminar Perfil") },
            text = { Text("¿Estás seguro de que deseas eliminar a ${babyToDelete?.nombre}? Se borrarán todos sus registros de tomas y pañales de forma permanente.") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        babyToDelete?.let { viewModel.onIntent(GestionBebesIntent.DeleteBaby(it)) }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { babyToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Gestionar Bebés", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToAddBaby) {
                        Icon(Icons.Default.PersonAdd, "Añadir Bebé", tint = Color(0xFF4FC3F7))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4FC3F7))
            }
        } else if (state.babies.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Text(
                    "No hay bebés registrados aún.\nUsa el botón superior para añadir uno.",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
            ) {
                items(state.babies, key = { it.id }) { baby ->
                    BabyListItem(
                        baby = baby,
                        onDeleteClick = { babyToDelete = baby }
                    )
                }
            }
        }
    }
}

@Composable
fun BabyListItem(
    baby: Baby,
    onDeleteClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF8FDFF)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BabyAvatar(
                baby = baby,
                isSelected = false,
                onClick = {}
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = baby.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ID: ${baby.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red.copy(alpha = 0.6f)
                )
            }
        }
    }
}
