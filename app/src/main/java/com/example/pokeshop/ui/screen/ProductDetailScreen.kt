package com.example.pokeshop.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Inventory2
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
import com.example.pokeshop.data.entities.ProductEntity
import com.example.pokeshop.viewmodel.PokeShopViewModel
import com.example.pokeshop.viewmodel.ProductDetailUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: PokeShopViewModel,
    productId: Int,
    onNavigateBack: () -> Unit
) {
    // 1. Cargar producto al iniciar
    LaunchedEffect(key1 = productId) {
        viewModel.getProductById(productId.toLong())
    }

    // 2. Observar estado
    val productState by viewModel.productDetailUiState.collectAsStateWithLifecycle()
    val snackbarHostState = viewModel.snackbarHostState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles") }, // Título más corto para la barra
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        ProductDetailContent(
            modifier = Modifier.padding(innerPadding),
            productState = productState,
            onAddToCart = { viewModel.addToCart(it) },
            onRetry = { viewModel.getProductById(productId.toLong()) }
        )
    }
}

@Composable
fun ProductDetailContent(
    modifier: Modifier = Modifier,
    productState: ProductDetailUiState,
    onAddToCart: (ProductEntity) -> Unit,
    onRetry: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {

        // ESTADO 1: CARGANDO
        if (productState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        // ESTADO 2: ERROR
        else if (productState.error != null) {
            Column(
                modifier = Modifier.align(Alignment.Center).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ups! ${productState.error}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) { Text("Reintentar") }
            }
        }

        // ESTADO 3: ÉXITO
        else if (productState.product != null) {
            val product = productState.product!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // --- IMAGEN GRANDE ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(bottom = 16.dp)
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://placeholder.com/pokecard.png") // Aquí iría product.imageUrl
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_launcher_background),
                        error = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = product.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // --- DATOS EN TARJETA ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Plano, unido al fondo
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {

                        // Nombre y Precio
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "$${product.price} USD",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Chips de Información (Stock y Categoría)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Chip de Stock
                            val stockColor = if (product.stock > 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                            val stockBg = if (product.stock > 0) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.errorContainer

                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        text = if (product.stock > 0) "Stock: ${product.stock}" else "Agotado",
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Inventory2, contentDescription = null, tint = stockColor)
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = stockBg,
                                    labelColor = stockColor
                                )
                            )

                            // Chip de Categoría (Si existe el objeto)
                            product.category?.let { category ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text(category.name) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Divider()

                        Spacer(modifier = Modifier.height(16.dp))

                        // Descripción
                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            textAlign = TextAlign.Justify
                        )

                        Spacer(modifier = Modifier.height(32.dp)) // Espacio final
                    }
                }
            }

            // --- BOTÓN FLOTANTE INFERIOR (STICKY) ---
            // Lo ponemos fuera del Column scrollable para que siempre esté visible abajo
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { onAddToCart(product) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = product.stock > 0, // Deshabilitar si no hay stock
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (product.stock > 0) "Añadir al Carrito" else "Sin Stock",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}