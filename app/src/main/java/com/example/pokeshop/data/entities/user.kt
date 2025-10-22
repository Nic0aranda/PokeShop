package com.example.pokeshop.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey


@Entity(
    tableName = "users"
    , foreignKeys =[
        ForeignKey(
        entity = RolEntity::class,
        parentColumns = ["id"],
        childColumns = ["rolId"],
            onDelete = ForeignKey.SET_NULL
    )
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val names: String,
    val lastNames: String,
    val email: String,
    val password: String,
    val status: Boolean = true,
    val rolId: Long? = null
)