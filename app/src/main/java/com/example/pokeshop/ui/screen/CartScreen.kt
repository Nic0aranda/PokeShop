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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.pokeshop.R
import com.example.pokeshop.viewmodel.CartItem
import com.example.pokeshop.viewmodel.PokeShopViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: PokeShopViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    val cartUiState by viewModel.cartUiState.collectAsStateWithLifecycle()
    val selectedCurrency = viewModel.selectedCurrency
    val totalClp = viewModel.totalInClp

    // Recalcular CLP al entrar por si acaso
    LaunchedEffect(Unit) {
        viewModel.calculateClpTotal()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // --- MENU DESPLEGABLE MONEDA ---
                    CurrencyDropdown(
                        currentCurrency = selectedCurrency,
                        onCurrencyChange = { viewModel.onCurrencyChange(it) }
                    )

                    // Botón vaciar
                    if (cartUiState.items.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearCart() }) {
                            Icon(Icons.Default.RemoveShoppingCart, contentDescription = "Vaciar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = viewModel.snackbarHostState) },
        bottomBar = {
            if (cartUiState.items.isNotEmpty()) {
                // Calculamos el total a mostrar según la moneda seleccionada
                val displayTotal = if (selectedCurrency == "CLP") totalClp else cartUiState.total

                CartBottomBar(
                    total = displayTotal,
                    currency = selectedCurrency,
                    onCheckoutClick = onNavigateToCheckout
                )
            }
        }
    ) { innerPadding ->
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
                items(cartUiState.items, key = { it.productId }) { cartItem ->
                    // Calculamos precio unitario visual
                    // Nota: Esto es una estimación visual. El cálculo real preciso se hace con el total.
                    val unitPriceDisplay = if (selectedCurrency == "CLP") {
                        cartItem.price * (if (cartUiState.total > 0) totalClp / cartUiState.total else 980.0)
                    } else {
                        cartItem.price
                    }

                    CartItemRow(
                        cartItem = cartItem,
                        displayPrice = unitPriceDisplay,
                        currencySymbol = if (selectedCurrency == "CLP") "$" else "US$",
                        isClp = selectedCurrency == "CLP",
                        onIncrease = { viewModel.increaseCartItemQuantity(cartItem.productId) },
                        onDecrease = { viewModel.decreaseCartItemQuantity(cartItem.productId) },
                        onRemove = { viewModel.removeFromCart(cartItem.productId) }
                    )
                }
            }
        }
    }
}

// Componente Dropdown para Moneda
@Composable
fun CurrencyDropdown(
    currentCurrency: String,
    onCurrencyChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        TextButton(onClick = { expanded = true }) {
            Text(text = currentCurrency, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("USD (Dólar)") },
                onClick = {
                    onCurrencyChange("USD")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("CLP (Peso Chileno)") },
                onClick = {
                    onCurrencyChange("CLP")
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun CartItemRow(
    cartItem: CartItem,
    displayPrice: Double,
    currencySymbol: String,
    isClp: Boolean,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen con Coil
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://placeholder.com/pokecard.png")
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = cartItem.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                // Formato de precio
                val priceText = if (isClp) {
                    NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(displayPrice)
                } else {
                    "$currencySymbol${String.format("%.2f", displayPrice)}"
                }

                Text(text = priceText, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)

                // Mostrar Stock disponible
                Text(
                    text = "Stock disponible: ${cartItem.stock}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Controles de cantidad
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Botón Menos
                IconButton(onClick = onDecrease, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = "Menos")
                }

                Text(
                    text = "${cartItem.quantity}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Botón Más (SE BLOQUEA SI LLEGA AL STOCK)
                val isMaxStock = cartItem.quantity >= cartItem.stock
                IconButton(
                    onClick = onIncrease,
                    modifier = Modifier.size(30.dp),
                    enabled = !isMaxStock // <--- AQUÍ ESTÁ EL BLOQUEO
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Más",
                        tint = if (isMaxStock) Color.LightGray else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun CartBottomBar(
    total: Double,
    currency: String,
    onCheckoutClick: () -> Unit
) {
    val formattedTotal = if (currency == "CLP") {
        NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(total)
    } else {
        "US$ ${String.format("%.2f", total)}"
    }

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Total a Pagar", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = formattedTotal,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = onCheckoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pagar")
            }
        }
    }
}

@Composable
fun EmptyCartMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color.LightGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Tu carrito está vacío", style = MaterialTheme.typography.titleLarge, color = Color.Gray)
        }
    }
}