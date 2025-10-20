package com.example.pokeshop.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "roles")
data class RolEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)