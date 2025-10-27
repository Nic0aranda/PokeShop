package com.example.pokeshop.data.dao

import androidx.room.*
import com.example.pokeshop.data.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    //funcion para obtener toda la lista de productos
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    //funcion para obtener producto segun su id
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Long): ProductEntity?

    //funcion para obtener productos segun su categoria
    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    fun getProductsByCategory(categoryId: Long): Flow<List<ProductEntity>>

    //funcion para obtener productos segun su nombre
    @Query("SELECT * FROM products WHERE name LIKE '%' || :searchQuery || '%'")
    fun searchProducts(searchQuery: String): Flow<List<ProductEntity>>

    //funcion para obtener productos disponibles
    @Query("SELECT * FROM products WHERE stock > 0")
    fun getAvailableProducts(): Flow<List<ProductEntity>>

    //funcion para obtener productos inactivos
    @Query("SELECT * FROM products WHERE status = 1")
    fun getActiveProducts(): Flow<List<ProductEntity>>

    //funcion para crear productos
    @Insert
    suspend fun insertProduct(product: ProductEntity): Long

    //funcion para editar productos
    @Update
    suspend fun updateProduct(product: ProductEntity)

    //funcion para borrar productos
    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :productId")
    suspend fun decreaseStock(productId: Long, quantity: Int)

    //funcion para aumentar stock
    @Query("UPDATE products SET stock = stock + :quantity WHERE id = :productId")
    suspend fun increaseStock(productId: Long, quantity: Int)

    //funcion para cambiar el estado de un producto
    @Query("UPDATE products SET status = :status WHERE id = :productId")
    suspend fun updateProductStatus(productId: Long, status: Boolean)

    //funcion para borrar productos
    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    //funcion para borrar todos los productos
    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
}