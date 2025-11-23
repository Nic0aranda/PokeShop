package com.example.pokeshop.data.repository

import com.example.pokeshop.data.dto.SaleProductItemDto
import com.example.pokeshop.data.dto.SaleRequestDto
import com.example.pokeshop.data.network.CurrencyApiService
import com.example.pokeshop.data.network.RetrofitClient
import com.example.pokeshop.data.network.SalesApiService
import com.example.pokeshop.viewmodel.CartItem

data class CheckoutResult(
    val success: Boolean,
    val message: String
)

// Aceptamos ambas APIs en el constructor
open class SaleRepository(
    private val salesApi: SalesApiService = RetrofitClient.createService(SalesApiService::class.java, "8083"),
    private val currencyApi: CurrencyApiService = RetrofitClient.createService(CurrencyApiService::class.java, "8082")
) {

    suspend fun checkout(userId: Long, cartItems: List<CartItem>): CheckoutResult {
        return try {
            // 1. Convertir
            val productDtos = cartItems.map {
                SaleProductItemDto(
                    productId = it.productId.toLong(),
                    quantity = it.quantity
                )
            }

            // 2. Request
            val request = SaleRequestDto(
                userId = userId,
                products = productDtos
            )

            // 3. Llamada
            val response = salesApi.createSale(request)

            if (response.isSuccessful) {
                val venta = response.body()
                CheckoutResult(true, "Venta #${venta?.id} realizada con éxito. Total: $${venta?.total}")
            } else {
                // Leemos el errorBody
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido en la compra"
                CheckoutResult(false, errorMsg)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            CheckoutResult(false, "Error de conexión: ${e.message}")
        }
    }

    suspend fun getDolarPrice(): Double {
        return try {
            currencyApi.getDolarPrice()
        } catch (e: Exception) {
            980.0
        }
    }
}