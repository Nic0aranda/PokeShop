package com.example.pokeshop.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val subtotal: Double,
    val status: String = "PENDIENTE" // PENDIENTE, COMPLETADA, CANCELADA
)