package com.example.pokeshop.data.repository

import com.example.pokeshop.data.dao.UserDao
import com.example.pokeshop.data.entities.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun getUserById(userId: Long): UserEntity? = userDao.getUserById(userId)

    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)

    suspend fun insertUser(user: UserEntity): Long = userDao.insertUser(user)

    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)

    suspend fun updateUserStatus(userId: Long, status: Boolean) =
        userDao.updateUserStatus(userId, status)
}