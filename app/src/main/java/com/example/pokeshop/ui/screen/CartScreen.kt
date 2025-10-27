package com.example.pokeshop.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokeshop.R
import com.example.pokeshop.viewmodel.CartItem
import com.example.pokeshop.viewmodel.PokeShopViewModel
import java.text.NumberFormat
import java.util.Locale

// Formateador para el precio en CLP (Peso Chileno)
fun formatToCLP(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

// Pantalla de carrito
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: PokeShopViewModel,
    // Navegación
    onNavigateBack: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    // Estado del carrito
    val cartUiState by viewModel.cartUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                // Configuración del TopAppBar
                title = { Text("Mi Carrito") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver atrás"
                        )
                    }
                },
                actions = {
                    // Botón para vaciar el carrito
                    if (cartUiState.items.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearCart() }) {
                            Icon(
                                imageVector = Icons.Default.RemoveShoppingCart,
                                contentDescription = "Vaciar carrito"
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = viewModel.snackbarHostState) },
        bottomBar = {
            // Botón de compra si hay elementos en el carrito
            if (cartUiState.items.isNotEmpty()) {
                CartBottomBar(
                    total = cartUiState.total,
                    onCheckoutClick = onNavigateToCheckout
                )
            }
        }
    ) { innerPadding ->
        // Contenido de la pantalla si es que el carro esta vacio
        if (cartUiState.items.isEmpty()) {
            EmptyCartMessage(modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Lista de elementos en el carrito
                items(cartUiState.items, key = { it.productId }) { cartItem ->
                    CartItemRow(
                        cartItem = cartItem,
                        onIncrease = { viewModel.increaseCartItemQuantity(cartItem.productId) },
                        onDecrease = { viewModel.decreaseCartItemQuantity(cartItem.productId) },
                        // 3. AÑADIDO: Pasamos la acción de remover
                        onRemove = { viewModel.removeFromCart(cartItem.productId) }
                    )
                }
            }
        }
    }
}

// Fila de elemento en el carrito
@Composable
fun CartItemRow(
    cartItem: CartItem,
    //funciones de aumentar y disminuir cantidad ademas de eliminar del carrito
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Contenido de la fila
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = cartItem.name,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = cartItem.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = formatToCLP(cartItem.price), color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Controles de cantidad
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onDecrease, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = "Disminuir cantidad")
                }
                Text(text = "${cartItem.quantity}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onIncrease, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar cantidad")
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar producto del carrito",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// Barra inferior para el carrito
@Composable
fun CartBottomBar(total: Double, onCheckoutClick: () -> Unit) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        // Total y botón de compra
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total: ${formatToCLP(total)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = onCheckoutClick) {
                Text("Ir a Pagar")
            }
        }
    }
}

// Mensaje de carrito vacío
@Composable
fun EmptyCartMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Carrito vacío",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = "Tu carrito está vacío",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Añade productos desde el catálogo para verlos aquí.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
