package com.example.pokeshop.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokeshop.PokeShopApplication
import com.example.pokeshop.viewmodel.PokeShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePS() {
    val context = LocalContext.current
    val app = context.applicationContext as PokeShopApplication
    val viewModel: PokeShopViewModel = viewModel(
        factory = remember {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PokeShopViewModel(
                        userRepository = app.UserRepository,
                        rolRepository = app.rolRepository,
                        categoryRepository = app.categoryRepository,
                        productRepository = app.productRepository,
                        saleRepository = app.saleRepository,
                        saleDetailRepository = app.saleDetailRepository
                    ) as T
                }
            }
        }
    )

    val products by viewModel.allProducts.collectAsState(initial = emptyList())

    // Insertar datos de ejemplo al iniciar
    LaunchedEffect(Unit) {
        if (products.isEmpty()) {
            viewModel.insertSampleData()
        }
    }

    // Componente para mostrar cada producto
    @Composable
    fun ProductItem(product: com.example.pokeshop.data.entities.ProductEntity) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = product.name,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$${product.price}",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Stock: ${product.stock}",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
            )
        }
    }

    // Scaffold con contenido
    Scaffold(
        topBar = {
            // Tu TopAppBar aquí (puedes usar el componente que ya creaste)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Productos Populares",
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
            )

            // Mostrar productos
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products.take(5)) { product ->
                    ProductItem(product = product)
                }
            }
        }
    }

}