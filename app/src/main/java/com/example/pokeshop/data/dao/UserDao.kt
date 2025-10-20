package com.example.pokeshop.data.dao

import androidx.room.*
import com.example.pokeshop.data.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    //funcion para obtener todos los usuarios
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    //funcion para recibir usuarios por id
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): UserEntity?

    //funcion para obtener un usuario por medio de su email
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    //funcion para crear usuarios nuevos
    @Insert
    suspend fun insertUser(user: UserEntity): Long

    //funcion para editar latos de los usuarios
    @Update
    suspend fun updateUser(user: UserEntity)

    //funcion para cambiar estado de los usuarios y realizar un soft delete
    @Query("UPDATE users SET status = :status WHERE id = :userId")
    suspend fun updateUserStatus(userId: Long, status: Boolean)

    //funcion para borrar usuarios de manera definitiva
    @Delete
    suspend fun deleteUser(user: UserEntity)
}