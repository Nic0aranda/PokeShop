package com.example.pokeshop.data.repository

import com.example.pokeshop.data.network.CurrencyApiService
import com.example.pokeshop.data.network.RetrofitClient

// Aceptamos la API en el constructor para poder pasarle un Mock en los tests
open class CurrencyRepository(
    private val api: CurrencyApiService = RetrofitClient.createService(CurrencyApiService::class.java, "8082")
) {

    suspend fun getDolarValue(): Double {
        return try {
            api.getDolarPrice()
        } catch (e: Exception) {
            e.printStackTrace()
            // Valor fallback en caso de error para no romper la UI
            980.0
        }
    }

    /**
     * Convierte un monto específico de USD a CLP usando la API.
     */
    suspend fun convertToClp(amountInUsd: Double): Double {
        return try {
            api.convertUsdToClp(amountInUsd)
        } catch (e: Exception) {
            e.printStackTrace()
            // Cálculo manual si falla la API
            amountInUsd * 980.0
        }
    }
}