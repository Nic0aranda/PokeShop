package com.example.pokeshop.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pokeshop.R
import com.example.pokeshop.data.entities.ProductEntity
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
    onNavigateToProductDetail: (Int) -> Unit,
    onNavigateToCart: () -> Unit
) {
    val catalogState by viewModel.catalogUiState.collectAsStateWithLifecycle()
    val products = catalogState.products
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val drawerItems = listOf(
        DrawerItem("Inicio", Icons.Default.Home) { scope.launch { drawerState.close() } },
        DrawerItem("Catálogo", Icons.Default.ShoppingCart, onNavigateToCatalog),
        DrawerItem("Perfil", Icons.Default.Person, onNavigateToProfile)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { PokeDrawer(drawerState, scope, drawerItems) }
    ) {
        Scaffold(
            topBar = {
                PokeTopBar(
                    title = "PokeShop",
                    onMenuClick = { scope.launch { drawerState.open() } },
                    // --- AGREGAMOS EL CARRITO AQUÍ ---
                    actions = {
                        val cartState by viewModel.cartUiState.collectAsStateWithLifecycle()
                        IconButton(onClick = onNavigateToCart) {
                            BadgedBox(
                                badge = {
                                    if (cartState.items.isNotEmpty()) {
                                        Badge { Text("${cartState.items.sumOf { it.quantity }}") }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ShoppingCart, "Carrito")
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            if (catalogState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                HomeContent(
                    products = products,
                    modifier = Modifier.padding(innerPadding),
                    onProductClick = onNavigateToProductDetail
                )
            }
        }
    }
}

// ... (El resto de HomeContent y ProductItem sigue igual que te pasé antes)
// ... (Asegúrate de mantener HomeContent y ProductItem que ya tenías corregidos)
@Composable
fun HomeContent(
    products: List<ProductEntity>,
    modifier: Modifier = Modifier,
    onProductClick: (Int) -> Unit
) {
    fun getProductsByCategory(categoryId: Long): List<ProductEntity> {
        return products.filter { it.category?.id == categoryId }
    }

    if (products.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("La API respondió, pero no hay productos cargados. 🧐")
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Booster Packs
            val boosters = getProductsByCategory(1) // ID 1
            if (boosters.isNotEmpty()) {
                item { Text("Booster Packs", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                items(boosters) { product ->
                    ProductItem(product, onProductClick = { onProductClick(product.id.toInt()) })
                }
            }
            // 2. Sobres
            val sobres = getProductsByCategory(2) // ID 2
            if (sobres.isNotEmpty()) {
                item { Text("Sobres", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
                items(sobres) { product ->
                    ProductItem(product, onProductClick = { onProductClick(product.id.toInt()) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItem(
    product: ProductEntity,
    onProductClick: () -> Unit
) {
    Card(
        onClick = onProductClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data("https://placeholder.com/pokecard.png").crossfade(true).build(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier.size(80.dp).padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("$${product.price} USD", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
    }
}