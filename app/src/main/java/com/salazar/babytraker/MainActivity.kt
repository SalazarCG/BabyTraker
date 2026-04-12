package com.salazar.babytraker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.salazar.babytraker.features.inicio.presentation.ui.InicioScreen
import com.salazar.babytraker.features.medicos.presentation.ui.MedicosScreen
import com.salazar.babytraker.features.tomas_panales.presentation.ui.TomasPanalesScreen
import com.salazar.babytraker.navigation.Screen
import com.salazar.babytraker.navigation.bottomNavigationItems
import com.salazar.babytraker.ui.theme.BabyTrakerTheme
import com.salazar.babytraker.ui.theme.BlueSkyPrimary
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BabyTrakerTheme {
                val navController = rememberNavController()
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BabyTrakerBottomBar(
                            onNavigate = { screen ->
                                navController.navigate(screen) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Inicio,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<Screen.Inicio> { InicioScreen() }
                        composable<Screen.TomasPanales> { TomasPanalesScreen() }
                        composable<Screen.Medicos> { MedicosScreen() }
                    }
                }
            }
        }
    }
}

@Composable
fun BabyTrakerBottomBar(
    onNavigate: (Screen) -> Unit
) {
    val navController = rememberNavController() // En una app real, usar el mismo controller
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        bottomNavigationItems.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(item.screen::class) } == true
            
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.screen) },
                label = { Text(text = item.label) },
                icon = { 
                    Icon(
                        imageVector = item.icon, 
                        contentDescription = item.label
                    ) 
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BlueSkyPrimary,
                    selectedTextColor = BlueSkyPrimary,
                    indicatorColor = Color(0xFFE0F6FF), // Azul cielo muy suave para el fondo del icono seleccionado
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}
