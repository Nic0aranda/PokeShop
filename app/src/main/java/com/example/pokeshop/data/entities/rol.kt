package com.example.pokeshop.data.entities

import com.google.gson.annotations.SerializedName

data class RolEntity(
    @SerializedName("id", alternate = ["idRol", "id_rol"])
    val id: Long = 0,

    @SerializedName("name", alternate = ["nombre"])
    val name: String
)