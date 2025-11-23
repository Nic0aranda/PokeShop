package com.example.pokeshop.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pokeshop.viewmodel.CategoryCreateUiState
import com.example.pokeshop.viewmodel.PokeShopViewModel

// Pantalla para crear una nueva categoría
@Composable
fun CreateCategoryScreen(
    viewModel: PokeShopViewModel,
    onNavigateBack: () -> Unit
) {
    // 1. Observamos el estado de creación
    val uiState by viewModel.categoryCreateUiState.collectAsState()

    // 2. Si la creación es exitosa, volvemos atrás automáticamente
    LaunchedEffect(uiState.createSuccess) {
        if (uiState.createSuccess) {
            onNavigateBack()
        }
    }

    // 3. Limpiamos el formulario al salir para que no queden datos viejos
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearCreateCategoryState()
        }
    }

    CreateCategoryView(
        uiState = uiState,
        onNameChange = viewModel::onNewCategoryNameChange,
        onCreateCategory = viewModel::createNewCategory,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateCategoryView(
    uiState: CategoryCreateUiState,
    onNameChange: (String) -> Unit,
    onCreateCategory: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Categoría") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ingresa el nombre para la nueva categoría:",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campo de texto
                OutlinedTextField(
                    value = uiState.categoryName,
                    onValueChange = onNameChange,
                    label = { Text("Nombre de la categoría") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    // Mostramos error si la validación falla o si la API devuelve error
                    isError = uiState.nameError != null || uiState.errorMessage != null
                )

                // Mensajes de error
                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón Crear
                Button(
                    onClick = onCreateCategory,
                    // Se deshabilita si el nombre está vacío o si ya se está enviando
                    enabled = uiState.canBeCreated && !uiState.isCreating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (uiState.isCreating) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Creando...")
                    } else {
                        Text("Crear Categoría")
                    }
                }
            }
        }
    }
}