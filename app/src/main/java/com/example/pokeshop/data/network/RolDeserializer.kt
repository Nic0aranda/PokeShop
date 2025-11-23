package com.example.pokeshop.data.network

import com.example.pokeshop.data.entities.RolEntity
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class RolDeserializer : JsonDeserializer<RolEntity?> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): RolEntity? {
        // Caso 1: El servidor devuelve un número (ej: "rol": 2)
        if (json.isJsonPrimitive && json.asJsonPrimitive.isNumber) {
            return RolEntity(
                id = json.asLong,
                name = "Rol Cargado por ID" // Nombre genérico porque la API no nos dio el nombre
            )
        }

        // Caso 2: El servidor devuelve un objeto (ej: "rol": { "id": 1, "name": "..." })
        if (json.isJsonObject) {
            val obj = json.asJsonObject
            return RolEntity(
                id = obj.get("id").asLong,
                name = obj.get("name")?.asString ?: ""
            )
        }

        return null
    }
}