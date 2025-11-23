package com.example.pokeshop.data.entities

import com.google.gson.annotations.SerializedName

data class ProductImageEntity(
    @SerializedName("id")
    val id: Long = 0,

    // Mapea la URL o el string base64 que viene de la API
    @SerializedName("url", alternate = ["imageUrl", "image_url", "data"])
    val url: String,

    // Relación con el producto padre
    // Nota: La lógica de Foreign Key ahora la maneja MySQL en el servidor, no la App.
    @SerializedName("productId", alternate = ["producto_id", "product_id", "id_producto"])
    val productId: Long
)