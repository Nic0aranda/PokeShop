package com.example.pokeshop.data.repository

import com.example.pokeshop.data.entities.RolEntity
import com.example.pokeshop.data.network.RetrofitClient
import com.example.pokeshop.data.network.UserApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

// Aceptamos 'api' en el constructor para los Tests
open class RolRepository(
    private val api: UserApiService = RetrofitClient.createService(UserApiService::class.java, "8080")
) {

    fun getAllRoles(): Flow<List<RolEntity>> = flow {
        val roles = api.getAllRoles()
        emit(roles)
    }.catch { e ->
        e.printStackTrace()
        emit(emptyList())
    }

    suspend fun getRolById(rolId: Long): RolEntity? {
        return try {
            api.getRoleById(rolId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun insertRol(rol: RolEntity): Long {
        return try {
            val newRol = api.createRole(rol)
            newRol.id
        } catch (e: Exception) {
            -1L
        }
    }

    suspend fun updateRol(rol: RolEntity) {
        try {
            api.updateRole(rol.id, rol)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteRol(rol: RolEntity) {
        try {
            api.deleteRole(rol.id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}