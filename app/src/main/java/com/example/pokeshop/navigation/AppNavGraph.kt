package com.example.pokeshop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pokeshop.ui.screen.CartScreen      // Asegúrate de importar esta pantalla
import com.example.pokeshop.ui.screen.CatalogScreen
import com.example.pokeshop.ui.screen.CheckoutScreen
import com.example.pokeshop.ui.screen.HomePS
import com.example.pokeshop.ui.screen.Inicio
import com.example.pokeshop.ui.screen.LoginScreenVm
import com.example.pokeshop.ui.screen.ProductDetailScreen // Asegúrate de importar esta pantalla
import com.example.pokeshop.ui.screen.ProfileScreen
import com.example.pokeshop.ui.screen.Registro
import com.example.pokeshop.viewmodel.PokeShopViewModel
import androidx.compose.runtime.collectAsState
import com.example.pokeshop.ui.screen.AdminHomeScreen
import com.example.pokeshop.ui.screen.CategoryEditScreen
import com.example.pokeshop.ui.screen.CategoryManagementScreenVm
import com.example.pokeshop.ui.screen.CreateCategoryScreen


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
                },
                onNavigateToProductDetail = { productId ->
                    navController.navigate(Route.ProductDetail.createRoute(productId))
                }
            )
        }

        composable(Route.AdminHome.path) {
            AdminHomeScreen(
                onGoToCategories = {
                    navController.navigate(Route.CategoryManagement.path)
                },
                onGoToProducts = {
                    navController.navigate(Route.ProductManagement.path)
                },
                onLogout = {
                    navController.navigate(Route.Login.path) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable (Route.CategoryManagement.path) {
            CategoryManagementScreenVm(
                viewModel = viewModel,
                onBackPress = {
                    navController.popBackStack()
                },
                onGoToEditCategory = { categoryId ->
                    navController.navigate(Route.EditCategory.createRoute(categoryId))
                },
                onGoToCreateCategory = {
                    navController.navigate(Route.CreateCategory.path)
                }
            )
        }

        composable(route = Route.EditCategory.path,
            arguments = listOf(navArgument("categoryId") { type = NavType.LongType })
        ) { backStackEntry ->
            // Se obtiene el argumento de la ruta de forma segura.
            val categoryId = backStackEntry.arguments?.getLong("categoryId")

            // Carga los datos solo una vez o si el ID cambia.
            LaunchedEffect(categoryId) {
                // Asegurarse de que el ID no es nulo y es válido.
                if (categoryId != null && categoryId != 0L) {
                    viewModel.loadCategoryForEdit(categoryId)
                }
            }
            CategoryEditScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Route.CreateCategory.path) {
            CreateCategoryScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }


        composable(Route.Catalog.path) {
            CatalogScreen(
                viewModel = viewModel,
                onProductClick = { productId ->
                    navController.navigate(Route.ProductDetail.createRoute(productId))
                },
                onNavigateToHome = {
                    navController.navigate(Route.Home.path)
                },
                onNavigateToCart = {
                    navController.navigate(Route.Cart.path)
                },
                onNavigateToProfile = {
                    navController.navigate(Route.Profile.path)
                }
            )
        }

        composable(Route.Profile.path) {
            ProfileScreen(
                username = viewModel.userState.collectAsState().value.username,
                email = viewModel.userState.collectAsState().value.email,
                onLogout = {
                    navController.navigate(Route.Login.path) {
                    }
                },
                onNavigateBack = {
                navController.popBackStack()
            }
            )
        }

        composable(Route.Cart.path) {
            CartScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCheckout = {
                    navController.navigate(Route.Checkout.path)
                }
            )
        }

        composable(Route.Checkout.path) {
            CheckoutScreen(
                onNavigateToHome = {
                    navController.navigate(Route.Home.path)
                }
            )
        }

        composable(
            route = Route.ProductDetail.path,
            // El tipo de argumento debe ser Long para que coincida con la Entidad
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            // Es más seguro usar getLong y manejar la posible excepción o nulidad.
            val productId = backStackEntry.arguments?.getLong("productId")

            // LaunchedEffect asegura que la carga de datos se llama solo una vez
            // cuando el productId cambia.
            LaunchedEffect(productId) {
                if (productId != null) {
                    viewModel.getProductById(productId)
                }
            }

            ProductDetailScreen(
                viewModel = viewModel,
                productId = productId?.toInt() ?: 0,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

