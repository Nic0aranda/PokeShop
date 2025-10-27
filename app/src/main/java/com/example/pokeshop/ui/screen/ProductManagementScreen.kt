package com.example.pokeshop.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pokeshop.data.entities.ProductEntity
import com.example.pokeshop.ui.components.ManagementTopAppBar
import com.example.pokeshop.viewmodel.PokeShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagementScreen(
    viewModel: PokeShopViewModel,
    //navegacion
    onBackPress: () -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (Int) -> Unit
) {
    //Observa el mismo estado que el catálogo para obtener la lista de productos.
    val catalogState by viewModel.catalogUiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            ManagementTopAppBar(
                title = "Gestión de Productos",
                onBackPress = onBackPress,
                onAddPress = onAddProduct,
            )
        }
    ) { innerPadding ->
        //Comprueba el estado
        when {
            //Si esta cargando, muestra un indicador
            catalogState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            //Si hay un error, muestra un mensaje o si esta vacia la lista
            catalogState.products.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes productos para gestionar.")
                }
            }
            else -> {
                //Muestra los productos
                ProductManagementGrid(
                    modifier = Modifier.padding(innerPadding),
                    products = catalogState.products,
                    // La acción de clic navega a la pantalla de edición.
                    onProductClick = onEditProduct
                )
            }
        }
    }
}

//Card de producto
@Composable
fun ProductManagementGrid(
    modifier: Modifier = Modifier,
    products: List<ProductEntity>, // Lista de productos
    onProductClick: (Int) -> Unit // Acción de clic en un producto
) {
    // Reutiliza la misma estructura de card que el catalogo.
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(products, key = { it.id }) { product ->
            // Reutilizamos el ProductCard, pero la acción de clic es para editar.
            ProductCard(
                product = product,
                onProductClick = onProductClick
            )
        }
    }
}
