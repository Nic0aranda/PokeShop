// HomePS.kt
package com.example.pokeshop.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokeshop.ui.components.DrawerItem
import com.example.pokeshop.ui.components.PokeDrawer
import com.example.pokeshop.ui.components.PokeTopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePS() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Definir los items del drawer
    val drawerItems = listOf(
        DrawerItem(
            title = "Inicio",
            icon = Icons.Default.Home,
            onClick = {
                // Navegar a Inicio
                // navController.navigate("inicio")
            }
        ),
        DrawerItem(
            title = "Perfil",
            icon = Icons.Default.AccountCircle,
            onClick = {
                // Navegar a Perfil
                // navController.navigate("perfil")
            }
        ),
        DrawerItem(
            title = "Catálogo",
            icon = Icons.Default.ShoppingCart,
            onClick = {
                // Navegar a Catálogo
                // navController.navigate("catalogo")
            }
        )
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            PokeDrawer(
                drawerState = drawerState,
                scope = scope,
                drawerItems = drawerItems
            )
        }
    ) {
        Scaffold(
            topBar = {
                PokeTopBar(
                    title = "Bienvenido a PokeShop",
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

            }
        }
    }
}