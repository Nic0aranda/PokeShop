package com.example.pokeshop.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.pokeshop.data.entities.CategoryEntity
import com.example.pokeshop.viewmodel.PokeShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: PokeShopViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val catalogState by viewModel.catalogUiState.collectAsStateWithLifecycle()
    val categories = catalogState.categories
    val isSaving = viewModel.isSavingProduct

    // --- ESTADOS DE LA UI ---
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var isCategoryExpanded by remember { mutableStateOf(false) }

    // --- GESTIÓN DE IMAGEN ---
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    // Si el guardado fue exitoso, volvemos atrás
    LaunchedEffect(isSaving) {
        if (!isSaving && name.isBlank() && price.isBlank() && stock.isBlank() && selectedCategory == null) {
            // Asumimos que si todos los campos se vaciaron y no está cargando, la operación fue exitosa
            // Nota: Podrías usar un estado de 'saveSuccess' en el VM si prefieres
            // onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Nuevo Producto") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- 1. SELECTOR DE IMAGEN ---
            ImageSelector(imageUri) { galleryLauncher.launch("image/*") }

            // --- 2. CAMPOS DE TEXTO ---
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ), modifier = Modifier.weight(1f))
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
            }

            // --- 3. SELECTOR DE CATEGORÍA ---
            CategoryDropdown(
                categories = categories,
                selectedCategory = selectedCategory,
                isCategoryExpanded = isCategoryExpanded,
                onExpandedChange = { isCategoryExpanded = it },
                onCategorySelected = {
                    selectedCategory = it
                    isCategoryExpanded = false
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // --- 4. BOTÓN GUARDAR ---
            Button(
                onClick = {
                    val priceDouble = price.toDoubleOrNull()
                    val stockInt = stock.toIntOrNull()
                    val categoryId = selectedCategory?.id

                    if (name.isBlank() || priceDouble == null || stockInt == null || categoryId == null) {
                        Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                    } else {
                        // LLAMADA CORREGIDA a la función de orquestación
                        viewModel.createProductAndUploadImage( // <- Nombre de función corregido
                            context = context, // <- Pasamos Context para la conversión de imagen
                            name = name,
                            description = description,
                            price = priceDouble,
                            stock = stockInt,
                            categoryId = categoryId,
                            imageUri = imageUri
                        )

                        // Limpiar campos después de iniciar la operación
                        name = ""; description = ""; price = ""; stock = ""; selectedCategory = null
                        onNavigateBack() // Volver atrás después de la operación
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardando...")
                } else {
                    Text("Guardar Producto")
                }
            }
        }
    }
}

// --- Componentes Reutilizables ---

@Composable
fun ImageSelector(imageUri: Uri?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Imagen seleccionada",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            if (imageUri == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Añadir Foto", modifier = Modifier.size(40.dp))
                    Text("Toca para añadir foto", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<CategoryEntity>,
    selectedCategory: CategoryEntity?,
    isCategoryExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCategorySelected: (CategoryEntity) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = isCategoryExpanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = selectedCategory?.name ?: "Selecciona una categoría",
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = isCategoryExpanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}