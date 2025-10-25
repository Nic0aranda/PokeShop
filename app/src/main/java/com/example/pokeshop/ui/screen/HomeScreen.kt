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
    // 1. AÑADIDO: Recibe la lambda para navegar al detalle del producto
    onNavigateToProductDetail: (Int) -> Unit
) {
    val catalogState by viewModel.catalogUiState.collectAsState()
    val products = catalogState.products
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                // 2. AÑADIDO: Pasa la lambda de navegación al HomeContent
                onProductClick = onNavigateToProductDetail
            )
        }
    }
}

@Composable
fun HomeContent(
    products: List<com.example.pokeshop.data.entities.ProductEntity>,
    modifier: Modifier = Modifier,
    // 3. AÑADIDO: Recibe la lambda desde HomePS
    onProductClick: (Int) -> Unit
) {

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
        // ... (otros items)

        // Lista de Booster Packs
        items(getProductsByCategory(2)) { product ->
            // 4. AÑADIDO: Pasa la lambda al ProductItem con el ID del producto
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
            // 5. AÑADIDO: Haz lo mismo para la otra lista
            ProductItem(
                product = product,
                onProductClick = { onProductClick(product.id.toInt()) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Recomendado para el Card clickeable
@Composable
fun ProductItem(
    product: com.example.pokeshop.data.entities.ProductEntity,
    // 6. CORREGIDO: La sintaxis correcta para una lambda que no devuelve nada (Unit)
    onProductClick: () -> Unit
) {
    Card(
        // 7. AÑADIDO: Usa la lambda en el Card para hacerlo clickeable
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
                // Ojo: Asegúrate que este recurso existe y es el correcto
                painter = painterResource(id = androidx.core.R.drawable.ic_call_answer),
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
