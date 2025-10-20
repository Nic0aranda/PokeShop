package com.example.pokeshop.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.pokeshop.data.entities.RolEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface RolDao {

    //funcion para obtener todos los roles
    @Query("SELECT * FROM roles")
    fun getAllRoles(): Flow<List<RolEntity>>

    //funcion para obtener roles por id
    @Query("SELECT * FROM roles WHERE id = :rolId")
    suspend fun getRolById(rolId: Long): RolEntity?

    //funcion para obtener rol por su nombre (no creo utilizarla)
    @Query("SELECT * FROM roles WHERE name = :name")
    suspend fun getRolByName(name: String): RolEntity?

    //funcion para crear roles
    @Insert
    suspend fun insertRol(rol: RolEntity): Long

    //funcion para actualizar datos de roles
    @Update
    suspend fun updateRol(rol: RolEntity)

    //funcion para borrar roles
    @Delete
    suspend fun deleteRol(rol: RolEntity)

}