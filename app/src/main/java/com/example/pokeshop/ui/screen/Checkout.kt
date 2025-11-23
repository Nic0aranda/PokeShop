package com.example.pokeshop.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pokeshop.viewmodel.CartItem
import com.example.pokeshop.viewmodel.PokeShopViewModel
import java.text.NumberFormat
import java.util.Locale

// --- PANTALLA PRINCIPAL DE CHECKOUT (REVISIÓN) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: PokeShopViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSuccess: () -> Unit // Navegación cuando la compra sea exitosa
) {
    val cartUiState by viewModel.cartUiState.collectAsStateWithLifecycle()
    val selectedCurrency = viewModel.selectedCurrency
    val totalClp = viewModel.totalInClp

    // Estado local para saber si estamos procesando el pago (para mostrar loading)
    var isProcessingPayment by remember { mutableStateOf(false) }

    // Calcular valores (Neto, IVA, Total) dinámicamente según moneda
    val conversionRate = if (selectedCurrency == "CLP" && cartUiState.total > 0) totalClp / cartUiState.total else 1.0

    // Helper para formatear precios
    fun formatPrice(amount: Double): String {
        val value = amount * if (selectedCurrency == "CLP") conversionRate else 1.0
        return if (selectedCurrency == "CLP") {
            NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(value)
        } else {
            "US$ ${String.format("%.2f", value)}"
        }
    }

    val subtotal = cartUiState.total
    val iva = subtotal * 0.19
    val totalFinal = subtotal * 1.19

    // Efecto para detectar si la compra fue exitosa
    // Nota: La lógica real de "éxito" debería ser un estado en el ViewModel,
    // pero por simplicidad, si el carrito se vacía repentinamente mientras estamos aquí, asumimos éxito.
    LaunchedEffect(cartUiState.items.size) {
        if (isProcessingPayment && cartUiState.items.isEmpty()) {
            isProcessingPayment = false
            onNavigateToSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen de Orden") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Reutilizamos el dropdown de moneda que creamos antes
                    CurrencyDropdown(
                        currentCurrency = selectedCurrency,
                        onCurrencyChange = {
                            viewModel.onCurrencyChange(it)
                            viewModel.calculateClpTotal() // Recalcular al cambiar
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = viewModel.snackbarHostState) },
        bottomBar = {
            // Barra de pago final
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total a Pagar:", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = formatPrice(totalFinal),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            isProcessingPayment = true
                            viewModel.performCheckout()
                            // Si falla, isProcessingPayment debería volver a false (puedes manejarlo con un estado de error en VM)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !isProcessingPayment && cartUiState.items.isNotEmpty()
                    ) {
                        if (isProcessingPayment) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(Icons.Default.Payment, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("CONFIRMAR Y PAGAR")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Título Detalle
            item {
                Text(
                    text = "Detalle de Productos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // 2. Lista de Items (Solo lectura, no botones de editar)
            items(cartUiState.items) { item ->
                CheckoutItemRow(item = item, formatPrice = { formatPrice(it) })
            }

            item { Divider(modifier = Modifier.padding(vertical = 8.dp)) }

            // 3. Resumen de Costos
            item {
                CostSummaryCard(
                    neto = subtotal,
                    iva = iva,
                    total = totalFinal,
                    formatPrice = { formatPrice(it) }
                )
            }
        }
    }
}

// Componente para mostrar un producto en el checkout (más simple que el del carrito editable)
@Composable
fun CheckoutItemRow(
    item: CartItem,
    formatPrice: (Double) -> String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, fontWeight = FontWeight.SemiBold)
            Text(
                text = "${item.quantity} x ${formatPrice(item.price)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Text(
            text = formatPrice(item.price * item.quantity),
            fontWeight = FontWeight.Bold
        )
    }
}

// Tarjeta con el desglose de precios
@Composable
fun CostSummaryCard(
    neto: Double,
    iva: Double,
    total: Double,
    formatPrice: (Double) -> String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Resumen de Costos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            CostRow(label = "Neto (Subtotal)", value = formatPrice(neto))
            CostRow(label = "IVA (19%)", value = formatPrice(iva))

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = formatPrice(total),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CostRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

// --- PANTALLA DE ÉXITO (ORDER SUCCESS) ---
@Composable
fun OrderSuccessScreen(
    onNavigateToHome: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Éxito",
                modifier = Modifier.size(120.dp),
                tint = Color(0xFF4CAF50) // Verde éxito
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¡Compra Exitosa!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu orden ha sido registrada correctamente.\nHemos descontado el stock de los productos.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onNavigateToHome,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Volver al Inicio")
            }
        }
    }
}