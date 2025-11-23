package com.example.pokeshop.data.repository

import com.example.pokeshop.data.dto.ImagesJsonRequest
import com.example.pokeshop.data.dto.ImgEntity
import com.example.pokeshop.data.entities.ProductEntity
import com.example.pokeshop.data.network.ProductApiService
import com.example.pokeshop.data.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

open class ProductRepository(
    private val api: ProductApiService = RetrofitClient.createService(ProductApiService::class.java, "8081")
) {

    fun getAllProducts(categoryName: String? = null): Flow<List<ProductEntity>> = flow {
        // Solo hacemos la llamada y emitimos. Si falla, el operador .catch de abajo lo maneja.
        val products = api.getAllProducts(categoryName)
        emit(products)
        }.catch { e ->
        // Aquí capturamos el error de forma segura y "transparente" para Flow
        e.printStackTrace()
        emit(emptyList())
        }

    suspend fun getProductById(id: Long): ProductEntity? {
        return try {
            api.getProductById(id)
        } catch (e: Exception) {
            null
        }
    }

    // --- Escritura (CRUD) ---

    suspend fun insertProduct(product: ProductEntity): Boolean {
        return try {
            api.createProduct(product)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateProduct(product: ProductEntity): Boolean {
        return try {
            api.updateProduct(product.id, product)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteProduct(id: Long): Boolean {
        return try {
            api.deleteProduct(id)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // --- Gestión de Stock ---
    suspend fun decreaseStock(id: Long, quantity: Int): Boolean {
        return try {
            // Tu API espera que la cantidad sea positiva para descontar.
            // Si tu lógica en Java es "stock - cantidad", enviamos el número tal cual.
            api.updateStock(id, quantity)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // --- Gestión de Imágenes ---

    suspend fun getImages(productId: Long): List<ImgEntity> {
        return try {
            api.getImagesByProduct(productId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun uploadImages(productId: Long, base64Images: List<String>): Boolean {
        return try {
            val request = ImagesJsonRequest(base64Images)
            api.uploadImages(productId, request)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // NUEVA FUNCIÓN: Borrar una imagen específica por su ID
    suspend fun deleteImage(imageId: Long): Boolean {
        return try {
            api.deleteImage(imageId)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}