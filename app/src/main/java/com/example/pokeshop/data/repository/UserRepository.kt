package com.example.pokeshop.data.repository

import com.example.pokeshop.data.entities.UserEntity
import com.example.pokeshop.data.network.RetrofitClient
import com.example.pokeshop.data.network.UserApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

open class UserRepository(
    private val api: UserApiService = RetrofitClient.createService(UserApiService::class.java, "8080")
) {

    // --- Lectura --- (Sin cambios)
    fun getAllUsers(): Flow<List<UserEntity>> = flow {
        val users = api.getAllUsers()
        emit(users)
    }.catch { e ->
        e.printStackTrace()
        emit(emptyList())
    }

    suspend fun getUserById(userId: Long): UserEntity? {
        return try {
            api.getUserById(userId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return try {
            val allUsers = api.getAllUsers()
            allUsers.find { it.email == email }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // --- Escritura (CORREGIDO) ---

    suspend fun insertUser(user: UserEntity): Long {
        return try {
            val createdUser = api.createUser(user)
            // Si el servidor devuelve null en el ID (raro, pero posible), retornamos -1
            createdUser.id ?: -1L
        } catch (e: Exception) {
            e.printStackTrace()
            -1L
        }
    }

    suspend fun updateUser(user: UserEntity) {
        // Validamos: Si el usuario no tiene ID, no podemos actualizarlo
        val userId = user.id ?: return

        try {
            api.updateUser(userId, user)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateUserStatus(userId: Long, status: Boolean) {
        try {
            api.updateUserStatus(userId, status)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteUser(user: UserEntity) {
        // Validamos: Si no tiene ID, no podemos borrarlo
        val userId = user.id ?: return

        try {
            api.deleteUser(userId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}