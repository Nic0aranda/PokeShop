package com.example.pokeshop.data.repository

import com.example.pokeshop.data.entities.CategoryEntity
import com.example.pokeshop.data.network.ProductApiService
import com.example.pokeshop.data.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

// Aceptamos 'api' en el constructor para poder inyectar el Mock en los tests
open class CategoryRepository(
    private val api: ProductApiService = RetrofitClient.createService(ProductApiService::class.java, "8081")
) {

    // Obtener todas las categorías
    fun getAllCategories(): Flow<List<CategoryEntity>> = flow {
        val categories = api.getAllCategories()
        emit(categories)
    }.catch { e ->
        e.printStackTrace()
        emit(emptyList())
    }

    // Obtener por ID
    suspend fun getCategoryById(id: Long): CategoryEntity? {
        return try {
            api.getCategoryById(id)
        } catch (e: Exception) {
            null
        }
    }

    // Crear categoría
    suspend fun insertCategory(category: CategoryEntity): Boolean {
        return try {
            api.createCategory(category)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Actualizar categoría
    suspend fun updateCategory(category: CategoryEntity): Boolean {
        return try {
            api.updateCategory(category.id, category)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Borrar categoría
    suspend fun deleteCategory(id: Long): Boolean {
        return try {
            api.deleteCategory(id)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}