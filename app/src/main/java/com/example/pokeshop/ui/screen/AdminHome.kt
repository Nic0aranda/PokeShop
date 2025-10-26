package com.example.pokeshop.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pokeshop.ui.theme.PokeShopTheme

/**
 * Pantalla principal para el administrador.
 * Ofrece acceso a la gestión de productos, categorías y la opción de cerrar sesión.
 *
 * @param onGoToCategories Callback para navegar a la pantalla de gestión de categorías.
 * @param onGoToProducts Callback para navegar a la pantalla de gestión de productos.
 * @param onLogout Callback para ejecutar la lógica de cierre de sesión y navegar al login.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onGoToCategories: () -> Unit,
    onGoToProducts: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administrador") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp), // Padding adicional para el contenido
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido, Administrador",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Selecciona una opción para comenzar a gestionar la tienda.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(48.dp))

            // Botón para ir a Categorías
            AdminMenuButton(
                text = "Gestionar Categorías",
                icon = Icons.Default.Category,
                onClick = onGoToCategories
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para ir a Productos
            AdminMenuButton(
                text = "Gestionar Productos",
                icon = Icons.Default.Inventory,
                onClick = onGoToProducts
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Botón para Cerrar Sesión
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Logout ,
                    contentDescription = "Cerrar Sesión",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
private fun AdminMenuButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

// --- PREVIEW ---
// Permite visualizar el diseño de la pantalla en Android Studio sin ejecutar la app.
@Preview(showBackground = true)
@Composable
fun AdminHomeScreenPreview() {
    PokeShopTheme {
        AdminHomeScreen(
            onGoToCategories = { /* No hace nada en la preview */ },
            onGoToProducts = { /* No hace nada en la preview */ },
            onLogout = { /* No hace nada en la preview */ }
        )
    }
}
