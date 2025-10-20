package com.example.pokeshop.data.dao

import androidx.room.*
import com.example.pokeshop.data.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    //funcion para obtener todas las categorias
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    //funcion para obtener categoria segun su id
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): CategoryEntity?

    //funcion para crear categorias
    @Insert
    suspend fun insertCategory(category: CategoryEntity): Long

    //funcion para editar categorias
    @Update
    suspend fun updateCategory(category: CategoryEntity)

    //funcion para borrar categorias
    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    //funcion para obtener cuantos productos pertenecen a x categoria
    @Query("SELECT COUNT(*) FROM products WHERE categoryId = :categoryId")
    suspend fun getProductCountByCategory(categoryId: Long): Int
}
