package com.example.pokeshop.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pokeshop.data.entities.CategoryEntity
import com.example.pokeshop.viewmodel.PokeShopViewModel
import com.example.pokeshop.viewmodel.ProductEditUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditScreen(
    productId: Long,
    viewModel: PokeShopViewModel,
    onNavigateBack: () -> Unit
) {
    // Validación del productId
    if (productId == 0L) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
                Text("Error: ID de producto inválido")
                Button(onClick = onNavigateBack) {
                    Text("Volver")
                }
            }
        }
        return
    }

    // 1. CARGA INICIAL DE DATOS
    LaunchedEffect(productId) {
        Log.d("ProductEditScreen", "Iniciando carga para producto válido: $productId")
        viewModel.loadProductForEdit(productId)
    }

    // 2. Observación de Estados
    val state by viewModel.productEditUiState.collectAsStateWithLifecycle()
    val categories = viewModel.catalogUiState.collectAsStateWithLifecycle().value.categories
    val snackbarHostState = viewModel.snackbarHostState

    // Estado local para el dropdown
    var isCategoryExpanded by remember { mutableStateOf(false) }

    // Efecto para navegar al guardar/eliminar
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            Log.d("ProductEditScreen", "Guardado exitoso, navegando back")
            onNavigateBack()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isLoading) "Cargando Producto..."
                        else state.product?.name ?: "Editar Producto"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text("Cargando producto...")
                        }
                    }
                }

                state.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(state.errorMessage ?: "Error desconocido")
                            Button(onClick = { viewModel.loadProductForEdit(productId) }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }

                state.product != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. SELECTOR DE IMAGEN (Puedes dejarlo simple por ahora)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Imagen del producto",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // 2. CAMPOS EDITABLES
                        ProductEditFields(viewModel = viewModel, state = state)

                        // 3. SELECTOR DE CATEGORÍA
                        ProductCategoryDropdown(
                            categories = categories,
                            selectedCategory = state.category,
                            isCategoryExpanded = isCategoryExpanded,
                            onExpandedChange = { isCategoryExpanded = it },
                            onCategorySelected = viewModel::onEditProductCategoryChange
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // 4. BOTONES (Guardar y Borrar)
                        ProductEditActions(
                            onSave = viewModel::saveProductChanges,
                            onDelete = { viewModel.deleteProduct(onNavigateBack) },
                            isSaving = state.isSaving
                        )
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No se pudo cargar el producto")
                    }
                }
            }
        }
    }
}

// --- Componentes Auxiliares ---

@Composable
fun ProductEditFields(viewModel: PokeShopViewModel, state: ProductEditUiState) {
    OutlinedTextField(
        value = state.name,
        onValueChange = { viewModel.onEditProductDataChange("name", it) },
        label = { Text("Nombre del Producto") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    OutlinedTextField(
        value = state.description,
        onValueChange = { viewModel.onEditProductDataChange("description", it) },
        label = { Text("Descripción") },
        modifier = Modifier.fillMaxWidth()
    )
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = state.price,
            onValueChange = { viewModel.onEditProductDataChange("price", it) },
            label = { Text("Precio") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = state.stock,
            onValueChange = { viewModel.onEditProductDataChange("stock", it) },
            label = { Text("Stock") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCategoryDropdown(
    categories: List<CategoryEntity>,
    selectedCategory: CategoryEntity?,
    isCategoryExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCategorySelected: (CategoryEntity) -> Unit
) {
    ExposedDropdownMenuBox(expanded = isCategoryExpanded, onExpandedChange = onExpandedChange) {
        OutlinedTextField(
            value = selectedCategory?.name ?: "Selecciona una categoría",
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        ExposedDropdownMenu(expanded = isCategoryExpanded, onDismissRequest = { onExpandedChange(false) }) {
            categories.forEach { category ->
                DropdownMenuItem(text = { Text(category.name) }, onClick = { onCategorySelected(category) })
            }
        }
    }
}

@Composable
fun ProductEditActions(
    onSave: () -> Unit,
    onDelete: () -> Unit,
    isSaving: Boolean
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        // Botón Borrar
        OutlinedButton(
            onClick = onDelete,
            modifier = Modifier.weight(1f).height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Default.Delete, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Eliminar")
        }

        // Botón Guardar
        Button(
            onClick = onSave,
            enabled = !isSaving,
            modifier = Modifier.weight(1f).height(50.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
            } else {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar")
            }
        }
    }
}