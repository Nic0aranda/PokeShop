package com.example.pokeshop.data.repository

import com.example.pokeshop.data.dao.RolDao
import com.example.pokeshop.data.entities.RolEntity
import kotlinx.coroutines.flow.Flow

class RolRepository(private val rolDao: RolDao) {

    fun getAllRoles(): Flow<List<RolEntity>> = rolDao.getAllRoles()

    suspend fun getRolById(rolId: Long): RolEntity? = rolDao.getRolById(rolId)

    suspend fun getRolByName(name: String): RolEntity? = rolDao.getRolByName(name)

    suspend fun insertRol(rol: RolEntity): Long = rolDao.insertRol(rol)

    suspend fun updateRol(rol: RolEntity) = rolDao.updateRol(rol)

    suspend fun deleteRol(rol: RolEntity) = rolDao.deleteRol(rol)
}