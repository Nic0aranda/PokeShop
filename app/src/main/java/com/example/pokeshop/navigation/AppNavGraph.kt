package com.example.pokeshop.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pokeshop.ui.screen.Registro
import com.example.pokeshop.ui.screen.HomePS
import com.example.pokeshop.ui.screen.Inicio
import com.example.pokeshop.ui.screen.LoginScreenVm
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
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.navigate(Route.Login.path)
                },
                onNavigateToRegister = {
                    // La navegación al registro ya está definida, perfecto.
                    navController.navigate(Route.Register.path)
                }
            )
        }

        composable(Route.Login.path) {
            LoginScreenVm(
                viewModel = viewModel,
                onLoginSuccess = { isAdmin ->
                    val destination = if (isAdmin) {
                        // Usamos la ruta definida en tu objeto Route
                        Route.AdminHome.path
                    } else {
                        Route.Home.path
                    }
                    navController.navigate(destination) {
                        // Limpiamos el backstack hasta la pantalla de inicio
                        // para que el usuario no pueda volver al login con el botón de atrás.
                        popUpTo(Route.Inicio.path) { inclusive = true }
                    }
                },
                onGoRegister = {
                    // Navega a registro y evita múltiples copias en el backstack
                    navController.navigate(Route.Register.path) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // --- AÑADIR LA RUTA PARA LA PANTALLA DE REGISTRO ---
        composable(Route.Register.path) {
            Registro(
                viewModel = viewModel,
                onRegisterSuccess = {
                    // Después de un registro exitoso, enviamos al usuario al Login
                    // para que inicie sesión con su nueva cuenta.
                    navController.navigate(Route.Login.path) {
                        // Limpiamos la pantalla de registro del backstack
                        popUpTo(Route.Register.path) { inclusive = true }
                        // Asegura que no se abra una nueva pantalla de login si ya existe una
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

    }
}
