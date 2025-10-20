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
    @Query("SELECT * FROM roles")
    fun getAllrols(): Flow<List<RolEntity>>

    @Query("SELECT * FROM roles WHERE id = :rolId")
    suspend fun getRolById(rolId: Long): RolEntity?

    @Query("SELECT * FROM roles WHERE name = :name")
    suspend fun getRolByName(name: String): RolEntity?

    @Insert
    suspend fun insertRol(rol: RolEntity): Long

    @Update
    suspend fun updateRol(rol: RolEntity)

    @Delete
    suspend fun deleteRol(rol: RolEntity)

    @Query("DELETE FROM roles WHERE id = :rolId")
    suspend fun deleteRolById(rolId: Long)



}