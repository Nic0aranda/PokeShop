package com.example.pokeshop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Clase para representar un elemento del Drawer
data class DrawerItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

// Composable para el Drawer
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokeDrawer(
    // Parámetros para el Drawer
    drawerState: DrawerState,
    scope: CoroutineScope,
    drawerItems: List<DrawerItem>,
    modifier: Modifier = Modifier
) {
    // Contenido del Drawer
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(200.dp)
            .background(Color(0xFF2E7D32))
    ) {
        // Header del Drawer
        Surface(
            color = Color(0xFF1B5E20),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "PokeShop",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Items del Drawer
        drawerItems.forEach { item ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = Color.White
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = Color.White
                    )
                },
                selected = false,
                onClick = {
                    item.onClick()
                    scope.launch {
                        drawerState.close()
                    }
                },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    selectedContainerColor = Color(0xFF388E3C),
                    unselectedTextColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.White,
                    selectedIconColor = Color.White
                )
            )
        }
    }
}