package com.example.pokeshop.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pokeshop.viewmodel.CategoryEditUiState
import com.example.pokeshop.viewmodel.PokeShopViewModel

@Composable
fun CategoryEditScreen(
    viewModel: PokeShopViewModel,
    onNavigateBack: () -> Unit
) {
    // Observa el estado de la UI de edición desde el ViewModel.
    val uiState by viewModel.categoryEditUiState.collectAsState()

    // Efecto que se dispara cuando el guardado es exitoso para navegar hacia atrás.
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onNavigateBack()
        }
    }

    // Efecto que se dispara cuando la pantalla se va de la composición.
    // Se asegura de limpiar el estado para no mostrar datos viejos la próxima vez.
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearEditCategoryState()
        }
    }

    // Llamada al composable que renderiza la UI.
    CategoryEditView(
        uiState = uiState,
        onNameChange = viewModel::onCategoryNameChange,
        onSaveChanges = viewModel::saveCategoryChanges,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryEditView(
    uiState: CategoryEditUiState,
    onNameChange: (String) -> Unit,
    onSaveChanges: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (uiState.category != null) "Editar Categoría" else "Cargando...")
                },
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
            // Muestra el indicador de progreso mientras carga.
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                // Muestra el formulario de edición cuando la carga ha terminado.
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Modifica el nombre de la categoría:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Campo de texto para el nombre de la categoría.
                    OutlinedTextField(
                        value = uiState.categoryName,
                        onValueChange = onNameChange,
                        label = { Text("Nombre de la categoría") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = uiState.nameError != null || uiState.errorMessage != null
                    )

                    // Muestra el mensaje de error si existe.
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

                    // Botón para guardar los cambios.
                    Button(
                        onClick = onSaveChanges,
                        enabled = uiState.canBeSaved && !uiState.isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardando...")
                        } else {
                            Text("Guardar Cambios")
                        }
                    }
                }
            }
        }
    }
}

