package com.example.pokeshop.data.entities

import com.google.gson.annotations.SerializedName

data class CategoryEntity(
    // Eliminamos @PrimaryKey y @Entity. Solo dejamos SerializedName para la API.
    @SerializedName("id", alternate = ["idCategoria", "id_categoria"])
    val id: Long = 0,

    @SerializedName("nombre", alternate = ["name"])
    val name: String
)