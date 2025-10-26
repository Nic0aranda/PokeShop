package com.example.pokeshop.navigation

sealed class Route(val path: String) {
    object Inicio : Route("inicio")
    object Login : Route("login")
    object Register : Route("register")
    object Home : Route("home")
    object AdminHome : Route("admin_home")
    object Catalog : Route("catalog")
    object Profile : Route("profile")

    object ProductDetail : Route("product/{productId}") { // Ruta para el detalle con argumento
        fun createRoute(productId: Int) = "product/$productId"
    }

    object Cart : Route("cart")

    object Checkout : Route("checkout")

    object CategoryManagement : Route("category_management")

    object EditCategory : Route("edit_category/{categoryId}") {
        fun createRoute(categoryId: Int) = "edit_category/$categoryId"
    }
    object CreateCategory : Route("create_category")

    object ProductManagement : Route("product_management")
}
