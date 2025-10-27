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
    // Estado del ViewModel.
    val uiState by viewModel.categoryCreateUiState.collectAsState()

    // Efecto que navega hacia atrás cuando la creación es exitosa.
    LaunchedEffect(uiState.createSuccess) {
        if (uiState.createSuccess) {
            onNavigateBack()
        }
    }

    // Efecto que limpia el estado cuando la pantalla se va.
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearCreateCategoryState()
        }
    }

    // Llamada al Composable que renderiza la vista.
    CreateCategoryView(
        uiState = uiState,
        onNameChange = viewModel::onNewCategoryNameChange,
        onCreateCategory = viewModel::createNewCategory,
        onNavigateBack = onNavigateBack
    )
}

// Composable que renderiza la vista de creación de categoría.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateCategoryView(
    uiState: CategoryCreateUiState, // Estado del ViewModel.
    onNameChange: (String) -> Unit, // Maneja cambios en el nombre.
    onCreateCategory: () -> Unit, // Crea la categoría.
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Categoría") }, // Título de la pantalla.
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
        //Formulario de creación de categoría.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Ingresa el nombre para la nueva categoría:",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Campo de texto para el nombre.
            OutlinedTextField(
                value = uiState.categoryName,
                onValueChange = onNameChange,
                label = { Text("Nombre de la categoría") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.nameError != null || uiState.errorMessage != null
            )

            // Muestra mensajes de error generales.
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón para crear la categoría.
            Button(
                onClick = onCreateCategory,
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

