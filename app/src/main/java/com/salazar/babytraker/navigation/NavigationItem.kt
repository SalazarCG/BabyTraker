package com.salazar.babytraker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BabyChangingStation
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Inicio : Screen
    @Serializable
    data object TomasPanales : Screen
    @Serializable
    data object Medicos : Screen
    @Serializable
    data object AddBaby : Screen
}

data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val screen: Screen
)

val bottomNavigationItems = listOf(
    NavigationItem("Inicio", Icons.Default.Home, Screen.Inicio),
    NavigationItem("Tomas", Icons.Default.BabyChangingStation, Screen.TomasPanales),
    NavigationItem("Médicos", Icons.Default.MedicalServices, Screen.Medicos)
)
