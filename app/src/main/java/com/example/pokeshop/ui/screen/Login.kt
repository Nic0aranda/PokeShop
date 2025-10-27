package com.example.pokeshop.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pokeshop.viewmodel.PokeShopViewModel

//logica de login
@Composable
fun LoginScreenVm(
    viewModel: PokeShopViewModel,
    // Navegación
    onLoginSuccess: (isAdmin: Boolean) -> Unit,
    onGoRegister: () -> Unit
) {
    // Obtenemos el estado de la UI directamente del ViewModel.
    val state = viewModel.uiStateLogin

    // Navega y luego limpia el estado.
    LaunchedEffect(state.success) {
        if (state.success) {
            //La funcion loginUser verifica si el usuario es un vendedor
            // La información de si es admin o no, se guarda en `errorMsg` temporalmente.
            onLoginSuccess(state.errorMsg == "vendedor")
        }
    }

    // Limpia el estado del login cuando el composable se va de la pantalla.
    // Esto es importante para que al volver, el formulario esté vacío.
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearLoginState()
        }
    }

    LoginScreen(
        email = state.email,
        pass = state.pass,
        emailError = state.emailError,
        passError = state.passError,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = if (state.errorMsg != "vendedor") state.errorMsg else null, // No mostramos "vendedor" como error
        onEmailChange = viewModel::updateLoginEmail,
        onPassChange = viewModel::updateLoginPassword,
        onSubmit = { viewModel.loginUser { onLoginSuccess(it == "admin_home") } },
        onGoRegister = onGoRegister
    )
}

// Composición de la pantalla de login.
@Composable
private fun LoginScreen(
    // Datos del formulario.
    email: String,
    pass: String,
    emailError: String?,
    passError: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?,
    onEmailChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoRegister: () -> Unit
) {
    val bg = MaterialTheme.colorScheme.secondaryContainer
    // Estado para controlar la visibilidad de la contraseña.
    var showPass by remember { mutableStateOf(false) }

    //Contenido
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text( // Titulo
                text = "PokeShop Login",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(12.dp))

            Text(
                text = "Bienvenido a tu tienda de cartas Pokémon",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(20.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                singleLine = true,
                isError = emailError != null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError != null) {
                Text(
                    emailError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(8.dp))

            // Contraseña
            OutlinedTextField(
                value = pass,
                onValueChange = onPassChange,
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(
                        onClick = { showPass = !showPass },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = if (showPass) "Ocultar" else "Mostrar",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                isError = passError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (passError != null) {
                Text(
                    passError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(16.dp))

            // boton confirmar
            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Validando...")
                } else {
                    Text("Entrar")
                }
            }

            if (errorMsg != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(12.dp))

            // boton registro
            OutlinedButton(
                onClick = onGoRegister,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear cuenta")
            }
        }
    }
}
