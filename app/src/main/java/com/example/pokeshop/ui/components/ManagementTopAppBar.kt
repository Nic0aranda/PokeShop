package com.example.pokeshop.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

// Composable para la barra de navegación superior en la gestión
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementTopAppBar(
    // Parámetros para la barra de navegación superior en la gestión
    title: String,
    onBackPress: () -> Unit,
    onAddPress: () -> Unit
) {
    // Contenido de la barra de navegación superior en la gestión
    TopAppBar(
        title = {
            // Título de la barra de navegación superior en la gestión
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        // Botón para volver atrás
        navigationIcon = {
            IconButton(onClick = onBackPress) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver atrás"
                )
            }
        },
        // Botón para añadir un nuevo elemento
        actions = {
            IconButton(onClick = onAddPress) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir nuevo"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}
