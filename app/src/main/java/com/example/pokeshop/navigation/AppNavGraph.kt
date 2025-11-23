package com.example.pokeshop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pokeshop.ui.screen.CartScreen
import com.example.pokeshop.ui.screen.CatalogScreen
import com.example.pokeshop.ui.screen.CheckoutScreen
import com.example.pokeshop.ui.screen.HomePS
import com.example.pokeshop.ui.screen.Inicio
import com.example.pokeshop.ui.screen.LoginScreenVm
import com.example.pokeshop.ui.screen.ProductDetailScreen
import com.example.pokeshop.ui.screen.ProfileScreen
import com.example.pokeshop.ui.screen.Registro
import com.example.pokeshop.viewmodel.PokeShopViewModel
import androidx.compose.runtime.collectAsState
import com.example.pokeshop.ui.screen.AddProductScreen
import com.example.pokeshop.ui.screen.AdminHomeScreen
import com.example.pokeshop.ui.screen.CategoryEditScreen
import com.example.pokeshop.ui.screen.CategoryManagementScreenVm
import com.example.pokeshop.ui.screen.CreateCategoryScreen
import com.example.pokeshop.ui.screen.OrderSuccessScreen
import com.example.pokeshop.ui.screen.ProductManagementScreen


@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: PokeShopViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Route.Inicio.path
    ) {
        //composabl que define a donde llevaran los paths asociados al inicio de la app
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

        // composable que define a donde llevaran los paths asociados al login ademas de utilizar funciones para validar si un usuario es admin (vendedor)
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

        // composable que define a donde llevaran los paths asociados al registro
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

        // composable que define a donde llevaran los paths asociados al home
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
                },
                onNavigateToCart = {
                    navController.navigate(Route.Cart.path)
                }
            )
        }

        // composable que define a donde llevaran los paths asociados al admin home
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

        // composables que definen a donde llevaran los paths asociados a la gestion de categorias
        composable (Route.CategoryManagement.path) {
            CategoryManagementScreenVm(
                viewModel = viewModel,
                onBackPress = {
                    navController.popBackStack()
                },
                onGoToEditCategory = { categoryId ->
                    // Navega a la pantalla de edición pasando el ID de la categoría
                    navController.navigate(Route.EditCategory.createRoute(categoryId))
                },
                onGoToCreateCategory = {
                    navController.navigate(Route.CreateCategory.path)
                }
            )
        }

        // composables que definen a donde llevaran los paths asociados a la gestion de productos
        composable (Route.ProductManagement.path) {
            ProductManagementScreen(
                viewModel = viewModel,
                onBackPress = {
                    navController.popBackStack()
                },
                onAddProduct = {
                    navController.navigate(Route.CreateProduct.path)
                },
                onEditProduct = { productId ->
                    // Navega a la pantalla de edición pasando el ID del producto
                    navController.navigate(Route.EditProduct.createRoute(productId))
                }
            )
        }

        // composables que definen a donde llevaran los paths asociados a la creacion de productos
        composable(Route.CreateProduct.path) {
            AddProductScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // composables que definen a donde llevaran los paths asociados a la edicion de productos
        composable(route = Route.EditCategory.path,
            // El tipo de argumento debe ser Long para que coincida con la Entidad
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
                    // Navega hacia atrás si es necesario.
                    navController.popBackStack()
                },
                categoryId = categoryId?.toInt() ?: 0
            )
        }

        // composables que definen a donde llevaran los paths asociados a la creacion de categorias
        composable(Route.CreateCategory.path) {
            CreateCategoryScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    // Navega hacia atrás si es necesario.
                    navController.popBackStack()
                }
            )
        }

        // composables que definen a donde llevaran los paths asociados al catalogo
        composable(Route.Catalog.path) {
            CatalogScreen(
                viewModel = viewModel,
                onProductClick = { productId ->
                    // Navega a la pantalla de detalle pasando el ID del producto
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

        // composables que definen a donde llevaran los paths asociados al perfil
        composable(Route.Profile.path) {
            ProfileScreen(
                onLogout = {
                    // Navega al login y limpia el backstack en caso de logout
                    navController.navigate(Route.Login.path) {
                    }
                },
                onNavigateBack = {
                navController.popBackStack()
                },
                viewModel = viewModel
            )
        }

        // composables que definen a donde llevaran los paths asociados al carrito
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

        // composables que definen a donde llevaran los paths asociados al checkout
        composable("checkout") {
            CheckoutScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSuccess = {
                    // Al tener éxito, borramos el historial de navegación para que no pueda volver atrás al carrito lleno
                    navController.navigate("order_success") {
                        popUpTo("home") { inclusive = false } // Volver hasta Home limpiando pila
                    }
                }
            )
        }

        composable("order_success") {
            OrderSuccessScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo(0) // Limpia TODO y vuelve al inicio limpio
                    }
                }
            )
        }

        // composables que definen a donde llevaran los paths asociados al detalle de productos
        composable(
            route = Route.ProductDetail.path,
            // El tipo de argumento debe ser Long para que coincida con la Entidad
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            // obtenemos el argumento con un long y lo guardamos en una variable
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

