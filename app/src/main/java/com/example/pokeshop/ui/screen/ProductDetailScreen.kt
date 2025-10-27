package com.example.pokeshop.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.pokeshop.R
import com.example.pokeshop.data.entities.ProductEntity
import com.example.pokeshop.viewmodel.PokeShopViewModel
import com.example.pokeshop.viewmodel.ProductDetailUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: PokeShopViewModel,
    productId: Int, // Id del producto a mostrar
    onNavigateBack: () -> Unit
) {
    // Usamos `LaunchedEffect` para indicar al ViewModel qué producto cargar.
    LaunchedEffect(key1 = productId) {
        viewModel.getProductById(productId.toLong())
    }

    // Observamos el estado de la UI de los detalles del producto desde el ViewModel
    val productState by viewModel.productDetailUiState.collectAsStateWithLifecycle()

    // Composición de la pantalla de detalles del producto
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(productState.product?.name ?: "Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver atrás"
                        )
                    }
                }
            )
        },
        // Usamos un `SnackbarHost` para mostrar confirmaciones al usuario
        snackbarHost = { SnackbarHost(hostState = viewModel.snackbarHostState) }
    ) { innerPadding ->
        ProductDetailContent(
            modifier = Modifier.padding(innerPadding),
            productState = productState,
            onAddToCart = {
                // El ViewModel se encarga de la lógica de añadir al carrito
                viewModel.addToCart(it)
            }
        )
    }
}

// Contenido de la pantalla de detalles del producto
@Composable
fun ProductDetailContent(
    modifier: Modifier = Modifier,
    productState: ProductDetailUiState, // Estado del producto
    onAddToCart: (ProductEntity) -> Unit // Callback para añadir al carrito
) {
    // Manejo de los estados de carga, error y éxito
    when {
        productState.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        productState.error != null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = productState.error,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
        productState.product != null -> {
            // Estado de éxito: Mostramos los detalles del producto
            val product = productState.product
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Para que la pantalla sea desplazable
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //imagen del producto
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background), //imagen provisional hasta implementacion futura
                    contentDescription = "Imagen de ${product.name}",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )

                // nombre del producto
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                //precio
                Text(
                    text = "$${product.price}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                //descripcion
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo

                //boton para añadir al carrito
                Button(
                    onClick = { onAddToCart(product) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Añadir al Carrito", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
