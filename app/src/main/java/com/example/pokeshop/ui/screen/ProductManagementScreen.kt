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
import com.example.pokeshop.data.entities.ProductEntity
import com.example.pokeshop.ui.components.ManagementTopAppBar
import com.example.pokeshop.viewmodel.PokeShopViewModel

@Composable
fun ProductManagementScreenVm(
    viewModel: PokeShopViewModel,
    onBackPress: () -> Unit,
    onGoToEditProduct: (productId: Long) -> Unit, // Usar Long para consistencia
    onGoToCreateProduct: () -> Unit
) {
    // Cargar datos frescos al entrar
    LaunchedEffect(Unit) {
        viewModel.loadInitialData() // Esto ya carga productos y categorías
    }

    // Observar el estado del catálogo
    val catalogState by viewModel.catalogUiState.collectAsState()

    ProductManagementScreen(
        isLoading = catalogState.isLoading,
        products = catalogState.products,
        onBackPress = onBackPress,
        onGoToEditProduct = onGoToEditProduct,
        onGoToCreateProduct = onGoToCreateProduct
    )
}

@Composable
private fun ProductManagementScreen(
    isLoading: Boolean,
    products: List<ProductEntity>,
    onBackPress: () -> Unit,
    onGoToEditProduct: (productId: Long) -> Unit,
    onGoToCreateProduct: () -> Unit
) {
    Scaffold(
        topBar = {
            ManagementTopAppBar(
                title = "Gestión de Productos",
                onBackPress = onBackPress,
                onAddPress = onGoToCreateProduct
            )
        }
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // CASO 1: Cargando
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            // CASO 2: Lista Vacía
            else if (products.isEmpty()) {
                Text(
                    text = "No hay productos para gestionar.",
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
                    items(products, key = { it.id }) { product ->
                        ProductGridItem(
                            product = product,
                            onClick = { onGoToEditProduct(product.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductGridItem(
    product: ProductEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f), // Rectángulo vertical para productos
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Información principal del producto
            Column {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$${product.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Información de stock/estado
            Column {
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    text = if (product.stock > 0) "Stock: ${product.stock}" else "SIN STOCK",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (product.stock > 0) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )

                if (product.category != null) {
                    Text(
                        text = product.category.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}