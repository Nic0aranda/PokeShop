package com.example.pokeshop.navigation

sealed class Route(val path: String) {
    object Inicio : Route("inicio")
    object Login : Route("login")
    object Register : Route("register")
    object Home : Route("home")
    object AdminHome : Route("admin_home")
    object Catalog : Route("catalog")
    object Profile : Route("profile")
}