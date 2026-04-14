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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.salazar.babytraker.core.data.local.preferences.BabyPreferences
import com.salazar.babytraker.features.inicio.presentation.ui.*
import com.salazar.babytraker.features.medicos.presentation.ui.MedicosScreen
import com.salazar.babytraker.features.tomas_panales.presentation.ui.TomasPanalesScreen
import com.salazar.babytraker.navigation.Screen
import com.salazar.babytraker.navigation.bottomNavigationItems
import com.salazar.babytraker.ui.theme.BabyTrakerTheme
import com.salazar.babytraker.ui.theme.BlueSkyPrimary
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var babyPreferences: BabyPreferences

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
                            navController = navController,
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
                        composable<Screen.Inicio> { 
                            InicioScreen(
                                onNavigateToAddBaby = {
                                    navController.navigate(Screen.AddBaby)
                                },
                                onNavigateToDetalle = { fecha, babyId ->
                                    navController.navigate(Screen.DetalleDia(fecha, babyId))
                                },
                                onNavigateToOpciones = {
                                    navController.navigate(Screen.Opciones)
                                }
                            ) 
                        }
                        composable<Screen.TomasPanales> { TomasPanalesScreen() }
                        composable<Screen.Medicos> { MedicosScreen() }
                        composable<Screen.AddBaby> { 
                            AddBabyScreen(
                                babyPreferences = babyPreferences,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable<Screen.DetalleDia> { backStackEntry ->
                            val detalle = backStackEntry.toRoute<Screen.DetalleDia>()
                            DetalleDiaScreen(
                                fechaDia = detalle.fechaDia,
                                babyId = detalle.babyId,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable<Screen.Opciones> {
                            OpcionesScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onNavigateToGestionBebes = {
                                    navController.navigate(Screen.GestionBebes)
                                }
                            )
                        }
                        composable<Screen.GestionBebes> {
                            GestionBebesScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onNavigateToAddBaby = {
                                    navController.navigate(Screen.AddBaby)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BabyTrakerBottomBar(
    navController: NavHostController,
    onNavigate: (Screen) -> Unit
) {
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
                    indicatorColor = Color(0xFFE0F6FF),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}
