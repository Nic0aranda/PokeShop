package com.example.pokeshop.data.repository

import com.example.pokeshop.data.dao.ProductDao
import com.example.pokeshop.data.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    suspend fun getProductById(productId: Long): ProductEntity? =
        productDao.getProductById(productId)

    fun getProductsByCategory(categoryId: Long): Flow<List<ProductEntity>> =
        productDao.getProductsByCategory(categoryId)

    fun searchProducts(searchQuery: String): Flow<List<ProductEntity>> =
        productDao.searchProducts(searchQuery)

    fun getAvailableProducts(): Flow<List<ProductEntity>> =
        productDao.getAvailableProducts()

    fun getActiveProducts(): Flow<List<ProductEntity>> =
        productDao.getActiveProducts()

    suspend fun insertProduct(product: ProductEntity): Long =
        productDao.insertProduct(product)

    suspend fun updateProduct(product: ProductEntity) =
        productDao.updateProduct(product)

    suspend fun decreaseStock(productId: Long, quantity: Int) =
        productDao.decreaseStock(productId, quantity)

    suspend fun increaseStock(productId: Long, quantity: Int) =
        productDao.increaseStock(productId, quantity)

    suspend fun updateProductStatus(productId: Long, status: Boolean) =
        productDao.updateProductStatus(productId, status)

    suspend fun deleteProduct(product: ProductEntity) =
        productDao.deleteProduct(product)

    suspend fun deleteAllProducts() {
        productDao.deleteAllProducts()
    }
}