package com.example.pokeshop.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pokeshop.R
import com.example.pokeshop.ui.components.DrawerItem
import com.example.pokeshop.ui.components.PokeDrawer
import com.example.pokeshop.ui.components.PokeTopBar
import com.example.pokeshop.viewmodel.PokeShopViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePS(
    viewModel: PokeShopViewModel,
    onNavigateToCatalog: () -> Unit,
    onNavigateToProfile: () -> Unit,
    //sirve para navegar a la pagina de cada producto segun su id
    onNavigateToProductDetail: (Int) -> Unit
) {
    // Estado del catálogo
    val catalogState by viewModel.catalogUiState.collectAsState()
    val products = catalogState.products
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // Estado del menú lateral
    val scope = rememberCoroutineScope() // Alcance para el menú lateral


    // Items del menú lateral
    val drawerItems = listOf(
        DrawerItem(
            title = "Inicio",
            icon = Icons.Default.Home,
            onClick = { /* Ya estamos en inicio */ }
        ),
        DrawerItem(
            title = "Catálogo",
            icon = Icons.Default.ShoppingCart,
            onClick = onNavigateToCatalog
        ),
        DrawerItem(
            title = "Perfil",
            icon = Icons.Default.Person,
            onClick = onNavigateToProfile
        )
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            PokeDrawer(
                drawerState = drawerState,
                scope = scope,
                drawerItems = drawerItems
            )
        }
    ) {
        Scaffold(
            topBar = {
                PokeTopBar(
                    title = "PokeShop",
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        ) { innerPadding ->
            HomeContent(
                products = products,
                modifier = Modifier.padding(innerPadding),
                onProductClick = onNavigateToProductDetail
            )
        }
    }
}

// Contenido de la pantalla
@Composable
fun HomeContent(
    products: List<com.example.pokeshop.data.entities.ProductEntity>, // Lista de productos
    modifier: Modifier = Modifier,
    onProductClick: (Int) -> Unit // Acción al hacer clic en un producto
) {

    // Filtra los productos por categoría
    fun getProductsByCategory(categoryId: Long): List<com.example.pokeshop.data.entities.ProductEntity> {
        return products.filter { it.categoryId == categoryId }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Booster Packs",
                style = MaterialTheme.typography.headlineSmall
            )
        }
        // Lista de Booster Packs
        items(getProductsByCategory(2)) { product ->
            ProductItem(
                product = product,
                onProductClick = { onProductClick(product.id.toInt()) }
            )
        }

        // Título Sobres
        item {
            Text(
                text = "Sobres",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // Lista de Sobres
        items(getProductsByCategory(3)) { product ->
            ProductItem(
                product = product,
                onProductClick = { onProductClick(product.id.toInt()) }
            )
        }
    }
}

// Elemento de producto en la lista
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItem(
    product: com.example.pokeshop.data.entities.ProductEntity,
    onProductClick: () -> Unit // Acción al hacer clic en el producto
) {
    // Elemento de producto
    Card(
        onClick = onProductClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Imagen de ${product.name}",
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )

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
