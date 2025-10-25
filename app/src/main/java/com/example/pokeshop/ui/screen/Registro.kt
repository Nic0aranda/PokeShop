package com.example.pokeshop.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pokeshop.viewmodel.PokeShopViewModel

@Composable
fun Registro(
    viewModel: PokeShopViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val bg = MaterialTheme.colorScheme.secondaryContainer
    // Obtenemos el estado de la UI directamente desde el ViewModel
    val uiState = viewModel.uiStateRegister

    // Efecto para navegar cuando el registro sea exitoso
    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            viewModel.clearRegisterState() // Limpia el estado antes de navegar
            onRegisterSuccess()
        }
    }

    // Limpiar el estado cuando la pantalla se destruye
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearRegisterState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear una cuenta", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Campo de Nombre de Usuario
        OutlinedTextField(
            value = uiState.username,
            onValueChange = { viewModel.updateRegisterUsername(it) },
            label = { Text("Nombre Completo") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username Icon") },
            isError = uiState.usernameError != null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        uiState.usernameError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Correo Electrónico
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { viewModel.updateRegisterEmail(it) },
            label = { Text("Correo Electrónico") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
            isError = uiState.emailError != null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        uiState.emailError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Contraseña
        OutlinedTextField(
            value = uiState.pass,
            onValueChange = { viewModel.updateRegisterPassword(it) },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
            isError = uiState.passError != null,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        uiState.passError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Confirmar Contraseña
        OutlinedTextField(
            value = uiState.confirmPass,
            onValueChange = { viewModel.updateRegisterConfirmPassword(it) },
            label = { Text("Confirmar Contraseña") },
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Confirm Password Icon"
                )
            },
            isError = uiState.confirmPassError != null,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        uiState.confirmPassError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Mensaje de error general
        uiState.errorMsg?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de Registro
        Button(
            onClick = { viewModel.registerUser() },
            enabled = uiState.canSubmit && !uiState.isSubmitting, // Habilitado si se puede enviar y no está cargando
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Registrarse")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para ir a Login
        TextButton(onClick = onNavigateToLogin) {
            Text("¿Ya tienes una cuenta? Inicia sesión")
        }
    }
}