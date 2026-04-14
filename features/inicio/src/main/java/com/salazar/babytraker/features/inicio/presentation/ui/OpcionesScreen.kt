package com.salazar.babytraker.features.inicio.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpcionesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToGestionBebes: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ajustes y Opciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            OpcionesSection(title = "General") {
                OpcionesItem(
                    icon = Icons.Default.ChildCare,
                    title = "Gestionar Bebés",
                    subtitle = "Añade, edita o elimina perfiles",
                    onClick = onNavigateToGestionBebes
                )
                OpcionesItem(
                    icon = Icons.Default.Notifications,
                    title = "Recordatorios",
                    subtitle = "Configura alertas de tomas y pañales",
                    onClick = { /* Implementar notificaciones */ }
                )
            }

            OpcionesSection(title = "Datos") {
                OpcionesItem(
                    icon = Icons.Default.Backup,
                    title = "Copia de Seguridad",
                    subtitle = "Exporta tus datos en formato local",
                    onClick = { /* Implementar exportación */ }
                )
                OpcionesItem(
                    icon = Icons.Default.DeleteForever,
                    title = "Borrar todo",
                    subtitle = "Elimina permanentemente todos los datos",
                    tint = Color.Red,
                    onClick = { /* Implementar borrado */ }
                )
            }

            OpcionesSection(title = "App") {
                OpcionesItem(
                    icon = Icons.Default.Info,
                    title = "Acerca de BabyTraker",
                    subtitle = "Versión 1.0.0",
                    onClick = { }
                )
                OpcionesItem(
                    icon = Icons.Default.Star,
                    title = "Valorar App",
                    subtitle = "Apóyanos con tu reseña",
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = "BabyTraker es una herramienta local y privada.",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun OpcionesSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF4FC3F7),
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF8FDFF)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun OpcionesItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color = Color(0xFF0288D1),
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text(subtitle, color = Color.Gray, fontSize = 12.sp) },
        leadingContent = { 
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = tint,
                modifier = Modifier.size(24.dp)
            ) 
        },
        trailingContent = { Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
