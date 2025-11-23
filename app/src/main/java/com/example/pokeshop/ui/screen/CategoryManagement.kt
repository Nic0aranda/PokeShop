package com.example.pokeshop.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pokeshop.ui.components.ManagementTopAppBar
import com.example.pokeshop.viewmodel.Category
import com.example.pokeshop.viewmodel.PokeShopViewModel

@Composable
fun CategoryManagementScreenVm(
    viewModel: PokeShopViewModel,
    onBackPress: () -> Unit,
    onGoToEditCategory: (categoryId: Int) -> Unit, // ID para editar
    onGoToCreateCategory: () -> Unit
) {
    // 1. Cargar datos frescos de la API al entrar
    LaunchedEffect(Unit) {
        viewModel.loadManagedCategories()
    }

    // 2. Observar el estado completo (Loading + Lista)
    val state by viewModel.categoryManagementUiState.collectAsState()

    CategoryManagementScreen(
        isLoading = state.isLoading, // Pasamos el estado de carga
        categories = state.categories,
        onBackPress = onBackPress,
        onGoToEditCategory = onGoToEditCategory,
        onGoToCreateCategory = onGoToCreateCategory
    )
}

@Composable
private fun CategoryManagementScreen(
    isLoading: Boolean,
    categories: List<Category>,
    onBackPress: () -> Unit,
    onGoToEditCategory: (categoryId: Int) -> Unit,
    onGoToCreateCategory: () -> Unit
) {
    Scaffold(
        topBar = {
            ManagementTopAppBar(
                title = "Gestionar Categorías",
                onBackPress = onBackPress,
                onAddPress = onGoToCreateCategory
            )
        }
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // CASO 1: Cargando (Esperando API)
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            // CASO 2: Lista Vacía (La API respondió [], no hay error pero no hay datos)
            else if (categories.isEmpty()) {
                Text(
                    text = "No hay categorías creadas.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // CASO 3: Mostrar Grilla
            else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categories, key = { it.id }) { category ->
                        CategoryGridItem(
                            category = category,
                            onClick = { onGoToEditCategory(category.id.toInt()) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryGridItem(
    category: Category,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f), // Cuadrado perfecto
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
    }
}