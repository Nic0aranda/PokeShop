package com.example.pokeshop.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

//Pantalla de inicio
@Composable
fun Inicio(
    // Navegación
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // Colores
    val bg = MaterialTheme.colorScheme.secondaryContainer

    // Contenido
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //boton de navegacion al login
        Button(onClick = onNavigateToLogin) {
            Text("Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        //boton de navegacion al registro
        Button(onClick = onNavigateToRegister) {
            Text("Crear Cuenta")
        }
    }
}