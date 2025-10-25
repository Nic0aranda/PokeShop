package com.example.pokeshop.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pokeshop.data.entities.CategoryEntity
import com.example.pokeshop.data.entities.ProductEntity
import com.example.pokeshop.ui.components.DrawerItem
import com.example.pokeshop.ui.components.PokeDrawer
import com.example.pokeshop.ui.components.PokeTopBar
import com.example.pokeshop.viewmodel.PokeShopViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: PokeShopViewModel,
    onProductClick: (Int) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Items del menú lateral (similar a Home, pero "Catálogo" es el activo)
    val drawerItems = listOf(
        DrawerItem(
            title = "Inicio",
            icon = Icons.Default.Home,
            onClick = onNavigateToHome
        ),
        DrawerItem(
            title = "Catálogo",
            icon = Icons.Default.ShoppingCart,
            onClick = { /* Ya estamos en catálogo */ }
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
                // Usamos tu TopBar reutilizable
                PokeTopBar(
                    title = "Catálogo",
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    // Añadimos el icono del carrito a la TopBar
                    actions = {
                        val cartState by viewModel.cartUiState.collectAsStateWithLifecycle()
                        BadgedBox(
                            badge = {
                                if (cartState.items.isNotEmpty()) {
                                    Badge { Text("${cartState.items.sumOf { it.quantity }}") }
                                }
                            }
                        ) {
                            IconButton(onClick = onNavigateToCart) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Carrito de compras"
                                )
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            // El contenido específico del catálogo
            CatalogContent(
                modifier = Modifier.padding(innerPadding),
                viewModel = viewModel,
                onProductClick = onProductClick
            )
        }
    }
}

/**
 * Composable que contiene solo la UI del catálogo (búsqueda, filtros, productos).
 * Es el equivalente a tu 'HomeContent'.
 */
@Composable
fun CatalogContent(
    modifier: Modifier = Modifier,
    viewModel: PokeShopViewModel,
    onProductClick: (Int) -> Unit
) {
    val catalogState by viewModel.catalogUiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        // Barra de Búsqueda
        SearchBar(
            query = catalogState.searchQuery,
            onQueryChange = viewModel::onSearchQueryChanged
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Filtros por Categoría
        CategoryFilters(
            categories = catalogState.categories,
            selectedCategoryId = catalogState.selectedCategoryId,
            onCategorySelected = viewModel::onCategorySelected
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Lista de Productos
        if (catalogState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (catalogState.products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No se encontraron productos.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            ProductGrid(
                products = catalogState.products,
                onProductClick = onProductClick
            )
        }
    }
}

// --- Los Composables más pequeños (SearchBar, CategoryFilters, etc.) se mantienen igual ---

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Buscar por nombre...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilters(
    categories: List<CategoryEntity>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedCategoryId == null,
            onClick = { onCategorySelected(null) },
            label = { Text("Todas") }
        )
        categories.forEach { category ->
            FilterChip(
                selected = selectedCategoryId == category.id,
                onClick = { onCategorySelected(category.id) },
                label = { Text(category.name) }
            )
        }
    }
}

@Composable
fun ProductGrid(
    products: List<ProductEntity>,
    onProductClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.height(1200.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(products, key = { it.id }) { product ->
            ProductCard(product = product, onProductClick = onProductClick)
        }
    }
}

@Composable
fun ProductCard(product: ProductEntity, onProductClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(product.id.toInt()) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = androidx.core.R.drawable.ic_call_answer),
                contentDescription = "Imagen de ${product.name}",
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$${product.price}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
