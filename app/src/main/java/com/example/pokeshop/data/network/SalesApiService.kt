package com.example.pokeshop.data.network

import com.example.pokeshop.data.dto.SaleRequestDto
import com.example.pokeshop.data.dto.SaleResponseDto
import retrofit2.Response // Importante: Usamos Response<> para manejar errores 400
import retrofit2.http.Body
import retrofit2.http.POST

interface SalesApiService {

    // Usamos Response<SaleResponseDto> para poder leer el código de error (400)
    // si el stock es insuficiente.
    @POST("api/v1/ventas")
    suspend fun createSale(@Body request: SaleRequestDto): Response<SaleResponseDto>
}