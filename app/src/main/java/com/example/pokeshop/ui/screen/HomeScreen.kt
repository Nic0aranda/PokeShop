package com.example.pokeshop.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pokeshop.viewmodel.PokeShopViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

@Composable
fun HomePS(
    viewModel: PokeShopViewModel,
    onNavigateToCatalog: () -> Unit
) {
    val products by viewModel.allProducts.collectAsState(initial = emptyList())


    fun getProductsByCategory(categoryId: Long): List<com.example.pokeshop.data.entities.ProductEntity> {
        return products.filter { it.categoryId == categoryId }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Productos Destacados",
                style = MaterialTheme.typography.headlineSmall // ← nota: "headlineSmall" con 's' minúscula
            )
        }

        Text(
            text = "Booster Packs",
            style = MaterialTheme.typography.headlineSmall
        )

        // Mostrar BoosterPacks
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(getProductsByCategory(2)) { product ->
                ProductItem(product = product)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Sobres",
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(getProductsByCategory(3)) { product ->
                    ProductItem(product = product)
                }
            }

            // Botones de navegación
            Button(
                onClick = onNavigateToCatalog,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Catálogo Completo")
            }
        }
    }
}

@Composable
fun ProductItem(product: com.example.pokeshop.data.entities.ProductEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto - tamaño parametrizado
            Image(
                painter = painterResource(id = androidx.core.R.drawable.ic_call_answer),
                contentDescription = "Imagen de ${product.name}",
                modifier = Modifier
                    .size(80.dp) // Tamaño parametrizado
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )

            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${product.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Stock: ${product.stock}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (product.stock > 0) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}