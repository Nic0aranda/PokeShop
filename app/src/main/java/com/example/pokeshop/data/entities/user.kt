package com.example.pokeshop.data.entities

import com.google.gson.annotations.SerializedName

data class UserEntity(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("names")
    val names: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("status")
    val status: Boolean = true,

    @SerializedName("rol")
    val rol: RolEntity? = null
)