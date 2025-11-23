package com.example.pokeshop.data.network

import com.example.pokeshop.data.entities.RolEntity
import com.example.pokeshop.data.entities.UserEntity
import retrofit2.http.*

interface UserApiService {

    // ================= USUARIOS (/api/v1/usuarios) =================

    @GET("api/v1/usuarios")
    suspend fun getAllUsers(): List<UserEntity>

    @GET("api/v1/usuarios/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserEntity

    @POST("api/v1/usuarios")
    suspend fun createUser(@Body user: UserEntity): UserEntity

    @PUT("api/v1/usuarios/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body user: UserEntity): UserEntity

    // Endpoint especial para Soft Delete / Cambio de estado
    // Java: @PutMapping("/{id}/status") ... @RequestParam Boolean status
    @PUT("api/v1/usuarios/{id}/status")
    suspend fun updateUserStatus(@Path("id") id: Long, @Query("status") status: Boolean)

    @DELETE("api/v1/usuarios/{id}")
    suspend fun deleteUser(@Path("id") id: Long)


    // ================= ROLES (/api/v1/roles) =================

    @GET("api/v1/roles")
    suspend fun getAllRoles(): List<RolEntity>

    @GET("api/v1/roles/{id}")
    suspend fun getRoleById(@Path("id") id: Long): RolEntity

    @POST("api/v1/roles")
    suspend fun createRole(@Body rol: RolEntity): RolEntity

    @PUT("api/v1/roles/{id}")
    suspend fun updateRole(@Path("id") id: Long, @Body rol: RolEntity): RolEntity

    @DELETE("api/v1/roles/{id}")
    suspend fun deleteRole(@Path("id") id: Long)
}