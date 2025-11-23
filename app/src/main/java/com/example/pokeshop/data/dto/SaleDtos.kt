package com.example.pokeshop.data.dto

import com.google.gson.annotations.SerializedName

// --- LO QUE ENVIAMOS (Match con SolicitudVentaDto de Java) ---
data class SaleRequestDto(
    @SerializedName("idUsuario")
    val userId: Long,

    @SerializedName("productos")
    val products: List<SaleProductItemDto>
)

data class SaleProductItemDto(
    @SerializedName("idProducto")
    val productId: Long,

    @SerializedName("cantidad")
    val quantity: Int
)

// --- LO QUE RECIBIMOS (Match con Venta de Java) ---
data class SaleResponseDto(
    @SerializedName("idVenta")
    val id: Long,

    @SerializedName("total")
    val total: Double,

    @SerializedName("estado")
    val status: String,

    // Puedes agregar más campos si quieres mostrar la fecha o el detalle
    @SerializedName("fecha")
    val date: String? = null
)