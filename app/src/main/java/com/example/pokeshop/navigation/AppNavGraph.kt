package com.example.pokeshop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pokeshop.ui.screen.HomePS
import com.example.pokeshop.ui.screen.Inicio
import com.example.pokeshop.ui.screen.LoginScreenVm
import com.example.pokeshop.ui.screen.ProfileScreen
import com.example.pokeshop.ui.screen.Registro
import com.example.pokeshop.viewmodel.PokeShopViewModel


@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: PokeShopViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Route.Inicio.path
    ) {
        composable(Route.Inicio.path) {
            Inicio(
                onNavigateToLogin = {
                    navController.navigate(Route.Login.path)
                },
                onNavigateToRegister = {
                    navController.navigate(Route.Register.path)
                }
            )
        }

        composable(Route.Login.path) {
            LoginScreenVm(
                viewModel = viewModel,
                onLoginSuccess = { isAdmin ->
                    val destination = if (isAdmin) {
                        Route.AdminHome.path
                    } else {
                        Route.Home.path
                    }
                    navController.navigate(destination) {
                        // Limpiamos el backstack hasta el inicio para que no pueda volver al login.
                        popUpTo(Route.Inicio.path) { inclusive = true }
                    }
                },
                onGoRegister = {
                    // Navega a registro y evita múltiples copias en el backstack.
                    navController.navigate(Route.Register.path) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Route.Register.path) {
            Registro(
                viewModel = viewModel,
                onRegisterSuccess = {
                    // Después de un registro exitoso, enviamos al usuario al Login.
                    navController.navigate(Route.Login.path) {
                        // Limpiamos la pantalla de registro del backstack.
                        popUpTo(Route.Register.path) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToLogin = {
                    // Si el usuario ya tiene cuenta, lo llevamos al Login.
                    navController.navigate(Route.Login.path) {
                        popUpTo(Route.Register.path) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Route.Home.path) {
            HomePS(
                viewModel = viewModel,
                onNavigateToCatalog = {
                    navController.navigate(Route.Catalog.path)
                },
                onNavigateToProfile = {
                    navController.navigate(Route.Profile.path)
                }
            )
        }

        // --- AÑADE AQUÍ EL COMPOSABLE PARA ADMINHOME ---
        composable(Route.AdminHome.path) {
            // Ejemplo: AdminHomeScreen(viewModel = viewModel)
        }

        // --- AÑADE AQUÍ EL COMPOSABLE PARA CATALOG ---
        composable(Route.Catalog.path) {
            // Ejemplo: CatalogScreen(viewModel = viewModel)
        }

        composable(Route.Profile.path) { // Usar la ruta del objeto Route
            // Recolecta el estado del usuario de forma segura con el ciclo de vida
            val userState by viewModel.userState.collectAsStateWithLifecycle()

            ProfileScreen(
                username = userState.username, // Pasa el nombre
                email = userState.email,       // Pasa el email
                onLogout = {
                    // Lógica para cerrar sesión
                    viewModel.logout()
                    navController.navigate(Route.Login.path) { // Usar la ruta del objeto Route
                        // Limpia la pila de navegación para que el usuario no pueda volver atrás.
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true // Evita apilar pantallas de login.
                    }
                }
            )
        }
    }
}
