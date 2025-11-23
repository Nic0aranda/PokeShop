package com.example.pokeshop.data.dto

import com.google.gson.annotations.SerializedName

data class ImgEntity(
    val id: Long,
    val url: String, // O el campo que tengas en tu modelo Java Img
    @SerializedName("producto_id") val productoId: Long?
)