package com.example.pokeshop.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pokeshop.ui.components.ManagementTopAppBar
import com.example.pokeshop.viewmodel.Category
import com.example.pokeshop.viewmodel.PokeShopViewModel

@Composable
fun CategoryManagementScreenVm(
    viewModel: PokeShopViewModel,
    // Navegación
    onBackPress: () -> Unit,
    onGoToEditCategory: (categoryId: Int) -> Unit, // Pasar el ID de la categoría
    onGoToCreateCategory: () -> Unit
) {

    // Cargamos las categorías del ViewModel cuando se crea la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadManagedCategories()
    }

    // Recolectamos el estado de las categorías desde el ViewModel como un State de Compose
    val categories by viewModel.categoryManagementUiState.collectAsState()

    CategoryManagementScreen(
        categories = categories.categories,
        onBackPress = onBackPress,
        onGoToEditCategory = onGoToEditCategory,
        onGoToCreateCategory = onGoToCreateCategory
    )
}

// Esta es la pantalla de gestión de categorías
@Composable
private fun CategoryManagementScreen(
    categories: List<Category>, // Lista de categorías
    onBackPress: () -> Unit,
    onGoToEditCategory: (categoryId: Int) -> Unit, // Pasar el ID de la categoría
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(paddingValues),
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

//Card de cada producto que son clickeables
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
            .aspectRatio(1f), // Hace que el Card sea un cuadrado perfecto
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        // Centramos el texto dentro del Card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

