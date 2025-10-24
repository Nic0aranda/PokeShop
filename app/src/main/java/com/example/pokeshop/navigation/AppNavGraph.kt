// navigation/AppNavGraph.kt
package com.example.pokeshop.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
                    navController.navigate(Route.Register.path)
                }
            )
        }

        composable(Route.Login.path) {
            LoginScreenVm(
                viewModel = viewModel,
                onLoginSuccess = { isAdmin ->
                    if (isAdmin) {
                        // Navegar al home de administrador
                        navController.navigate("admin_home") {
                            popUpTo(Route.Inicio.path) { inclusive = true }
                        }
                    } else {
                        // Navegar al home normal
                        navController.navigate(Route.Home.path) {
                            popUpTo(Route.Inicio.path) { inclusive = true }
                        }
                    }
                },
                onGoRegister = {
                    navController.navigate(Route.Register.path)
                }
            )
        }

        composable(Route.Home.path) {
            HomePS(
                viewModel = viewModel,
                onNavigateToCatalog = {
                    navController.navigate("catalog")
                }
            )
        }

    }
}