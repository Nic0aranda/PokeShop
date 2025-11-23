package com.example.pokeshop.data.network

import com.example.pokeshop.data.dto.ImagesJsonRequest
import com.example.pokeshop.data.dto.ImgEntity
import com.example.pokeshop.data.entities.CategoryEntity
import com.example.pokeshop.data.entities.ProductEntity
import retrofit2.http.*

interface ProductApiService {

    // ================= PRODUCTOS (/api/v1/productos) =================

    // @GetMapping (con opcional ?categoria=...)
    @GET("api/v1/productos")
    suspend fun getAllProducts(
        @Query("categoria") categoria: String? = null
    ): List<ProductEntity>

    // @GetMapping("/{id}")
    @GET("api/v1/productos/{id}")
    suspend fun getProductById(@Path("id") id: Long): ProductEntity

    // @PostMapping
    @POST("api/v1/productos")
    suspend fun createProduct(@Body product: ProductEntity): ProductEntity

    // @PutMapping("/{id}")
    @PUT("api/v1/productos/{id}")
    suspend fun updateProduct(@Path("id") id: Long, @Body product: ProductEntity): ProductEntity

    // @PutMapping("/{id}/stock")
    @PUT("api/v1/productos/{id}/stock")
    suspend fun updateStock(@Path("id") id: Long, @Query("cantidad") cantidad: Int): String

    // @DeleteMapping("/{id}")
    @DELETE("api/v1/productos/{id}")
    suspend fun deleteProduct(@Path("id") id: Long)

    // @GetMapping("/{id}/imagenes")
    @GET("api/v1/productos/{id}/imagenes")
    suspend fun getImagesByProduct(@Path("id") id: Long): List<ImgEntity>

    // @PostMapping("/{id}/imagenes-json")
    @POST("api/v1/productos/{id}/imagenes-json")
    suspend fun uploadImages(@Path("id") id: Long, @Body request: ImagesJsonRequest): List<ImgEntity>


    // ================= CATEGORÍAS (/api/v1/categorias) =================

    // @GetMapping
    @GET("api/v1/categorias")
    suspend fun getAllCategories(): List<CategoryEntity>

    // @GetMapping("/{id}")
    @GET("api/v1/categorias/{id}")
    suspend fun getCategoryById(@Path("id") id: Long): CategoryEntity

    // @PostMapping
    @POST("api/v1/categorias")
    suspend fun createCategory(@Body category: CategoryEntity): CategoryEntity

    @PUT("api/v1/categorias/{id}")
    suspend fun updateCategory(@Path("id") id: Long, @Body category: CategoryEntity): CategoryEntity

    // @DeleteMapping("/{id}")
    @DELETE("api/v1/categorias/{id}")
    suspend fun deleteCategory(@Path("id") id: Long)

    @DELETE("api/v1/imagenes/{id}")
    suspend fun deleteImage(@Path("id") id: Long)
}