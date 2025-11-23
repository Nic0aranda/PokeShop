package com.example.pokeshop.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState // Para los filtros horizontales
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pokeshop.R
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

    val drawerItems = listOf(
        DrawerItem("Inicio", Icons.Default.Home, onNavigateToHome),
        DrawerItem("Catálogo", Icons.Default.ShoppingCart) { scope.launch { drawerState.close() } },
        DrawerItem("Perfil", Icons.Default.Person, onNavigateToProfile)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { PokeDrawer(drawerState, scope, drawerItems) }
    ) {
        Scaffold(
            topBar = {
                PokeTopBar(
                    title = "Catálogo",
                    onMenuClick = { scope.launch { drawerState.open() } },
                    // --- BOTÓN DEL CARRITO ---
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
            CatalogContent(
                modifier = Modifier.padding(innerPadding),
                viewModel = viewModel,
                onProductClick = onProductClick
            )
        }
    }
}

@Composable
fun CatalogContent(
    modifier: Modifier = Modifier,
    viewModel: PokeShopViewModel,
    onProductClick: (Int) -> Unit
) {
    val catalogState by viewModel.catalogUiState.collectAsStateWithLifecycle()

    // Usamos LazyVerticalGrid como contenedor PRINCIPAL para evitar scroll anidado
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp) // Padding final
    ) {
        // --- HEADER (Buscador y Filtros) ---
        // Usamos span = { GridItemSpan(2) } para que ocupen todo el ancho
        item(span = { GridItemSpan(2) }) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                SearchBar(
                    query = catalogState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged
                )
                Spacer(modifier = Modifier.height(16.dp))
                CategoryFilters(
                    categories = catalogState.categories,
                    selectedCategoryId = catalogState.selectedCategoryId,
                    onCategorySelected = viewModel::onCategorySelected
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // --- ESTADOS DE CARGA / VACÍO ---
        if (catalogState.isLoading) {
            item(span = { GridItemSpan(2) }) {
                Box(modifier = Modifier.height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else if (catalogState.products.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Box(modifier = Modifier.height(200.dp), contentAlignment = Alignment.Center) {
                    Text("No se encontraron productos.", style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            // --- LISTA DE PRODUCTOS ---
            items(catalogState.products, key = { it.id }) { product ->
                ProductCard(product = product, onProductClick = onProductClick)
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Buscar carta o sobre...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        shape = MaterialTheme.shapes.medium
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
fun ProductCard(product: ProductEntity, onProductClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(product.id.toInt()) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen con Coil
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://placeholder.com/pokecard.png") // URL real futura
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background),
                contentDescription = product.name,
                modifier = Modifier
                    .size(100.dp) // Un poco más grande para el catálogo
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center
            )

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                minLines = 2 // Para alinear las tarjetas
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$${product.price} USD",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}