package com.example.pokeshop.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Composable para la barra de navegación superior
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokeTopBar(
    // Parámetros para la barra de navegación superior
    title: String,
    onMenuClick: () -> Unit,
    // Acciones adicionales para la barra de navegación superior
    actions: @Composable RowScope.() -> Unit = {}
) {
    // Contenido de la barra de navegación superior
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(50.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Título de la barra de navegación superior
                Text(title)
            }
        },
        // Botón para abrir el menú lateral
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menú lateral"
                )
            }
        },
        actions = actions
    )
}
