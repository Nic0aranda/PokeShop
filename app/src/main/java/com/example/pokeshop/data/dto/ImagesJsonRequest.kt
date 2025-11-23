package com.example.pokeshop.data.dto

import com.google.gson.annotations.SerializedName

data class ImagesJsonRequest(
    @SerializedName("images") val images: List<String>
)
