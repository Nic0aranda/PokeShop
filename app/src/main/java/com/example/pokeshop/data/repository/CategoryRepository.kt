package com.example.pokeshop.data.repository

import com.example.pokeshop.data.dao.CategoryDao
import com.example.pokeshop.data.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {

    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    suspend fun getCategoryById(categoryId: Long): CategoryEntity? = categoryDao.getCategoryById(categoryId)

    suspend fun insertCategory(category: CategoryEntity): Long = categoryDao.insertCategory(category)

    suspend fun updateCategory(category: CategoryEntity) = categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: CategoryEntity) = categoryDao.deleteCategory(category)

    suspend fun getProductCountByCategory(categoryId: Long): Int = categoryDao.getProductCountByCategory(categoryId)
}