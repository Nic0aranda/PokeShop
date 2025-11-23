package com.example.pokeshop.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {

    // Coincide con: @GetMapping("/dolar")
    @GET("api/v1/moneda/dolar")
    suspend fun getDolarPrice(): Double

    // Coincide con: @GetMapping("/convertir")
    @GET("api/v1/moneda/convertir")
    suspend fun convertUsdToClp(@Query("amount") amount: Double): Double
}