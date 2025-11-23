package com.example.pokeshop.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pokeshop.viewmodel.CategoryEditUiState
import com.example.pokeshop.viewmodel.PokeShopViewModel

@Composable
fun CategoryEditScreen(
    categoryId: Int,
    viewModel: PokeShopViewModel,
    onNavigateBack: () -> Unit
) {
    // 1. CARGAR DATOS
    LaunchedEffect(categoryId) {
        viewModel.loadCategoryForEdit(categoryId.toLong())
    }

    val uiState by viewModel.categoryEditUiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) } // Estado del diálogo de confirmación

    // 2. NAVEGACIÓN: Si se guardó con éxito, volvemos atrás
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.clearEditCategoryState()
            onNavigateBack()
        }
    }

    // 3. LIMPIEZA
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearEditCategoryState()
        }
    }

    // Lógica para el borrado (requiere el ID de la categoría)
    val currentCategoryId = uiState.category?.id

    CategoryEditView(
        uiState = uiState,
        onNameChange = viewModel::onCategoryNameChange,
        onSaveChanges = viewModel::saveCategoryChanges,
        onNavigateBack = onNavigateBack,
        onDeletePress = { showDeleteDialog = true } // Mostrar diálogo al presionar borrar
    )

    // 4. Diálogo de confirmación de borrado
    if (showDeleteDialog && currentCategoryId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar la categoría '${uiState.category?.name}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCategory(currentCategoryId, onNavigateBack) // Llamada a la nueva función
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryEditView(
    uiState: CategoryEditUiState,
    onNameChange: (String) -> Unit,
    onSaveChanges: () -> Unit,
    onNavigateBack: () -> Unit,
    onDeletePress: () -> Unit // Nuevo callback para el botón borrar
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = uiState.category?.name?.let { "Editar: $it" } ?: "Cargando..."
                    Text(titleText)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
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

                    OutlinedTextField(
                        value = uiState.categoryName,
                        onValueChange = onNameChange,
                        label = { Text("Nombre de la categoría") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = uiState.errorMessage != null
                    )

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

                    // --- BOTÓN GUARDAR (Primario) ---
                    Button(
                        onClick = onSaveChanges,
                        enabled = uiState.canBeSaved && !uiState.isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardando...")
                        } else {
                            Text("Guardar Cambios")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- BOTÓN BORRAR (Secundario/Destructivo) ---
                    OutlinedButton(
                        onClick = onDeletePress,
                        // El botón de borrar siempre está habilitado si no está guardando
                        enabled = !uiState.isSaving && uiState.category != null,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Borrar Categoría")
                    }
                }
            }
        }
    }
}