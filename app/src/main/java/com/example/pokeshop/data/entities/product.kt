package com.example.pokeshop.data.entities

import com.google.gson.annotations.SerializedName

data class ProductEntity(
    @SerializedName("id", alternate = ["idProducto", "id_producto"])
    val id: Long = 0,

    @SerializedName("name", alternate = ["nombre"])
    val name: String,

    @SerializedName("description", alternate = ["descripcion"])
    val description: String,

    @SerializedName("price", alternate = ["precio"])
    val price: Double,

    @SerializedName("stock")
    val stock: Int,

    @SerializedName("categoria")
    val category: CategoryEntity? = null,

    @SerializedName("status", alternate = ["estado"])
    val status: Boolean = true

)