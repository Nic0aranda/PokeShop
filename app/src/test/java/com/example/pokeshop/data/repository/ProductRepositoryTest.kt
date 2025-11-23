package com.example.pokeshop.data.repository

import com.example.pokeshop.data.dto.ImgEntity
import com.example.pokeshop.data.entities.ProductEntity
import com.example.pokeshop.data.network.ProductApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ProductRepositoryTest {

    // 1. Mockeamos la API
    private val mockApi = mockk<ProductApiService>()

    // 2. Inyectamos el mock al repositorio
    private val repository = ProductRepository(mockApi)

    // Entidad de prueba reutilizable
    private val dummyProduct = ProductEntity(
        id = 1,
        name = "Test Product",
        description = "Desc",
        price = 100.0,
        stock = 10,
        category = null,
        status = true
    )

    // ================= GET ALL PRODUCTS (FLOW) =================

    @Test
    fun `getAllProducts retorna lista cuando API responde`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getAllProducts(null) } returns listOf(dummyProduct)

        // WHEN
        val result = repository.getAllProducts().first()

        // THEN
        assertEquals(1, result.size)
        assertEquals("Test Product", result[0].name)
    }

    @Test
    fun `getAllProducts retorna lista vacia cuando API falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getAllProducts(any()) } throws RuntimeException("Error")

        // WHEN
        val result = repository.getAllProducts().first()

        // THEN
        assertTrue(result.isEmpty())
    }

    // ================= GET PRODUCT BY ID =================

    @Test
    fun `getProductById retorna producto cuando existe`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getProductById(1) } returns dummyProduct

        // WHEN
        val result = repository.getProductById(1)

        // THEN
        assertNotNull(result)
        assertEquals(1L, result?.id)
    }

    @Test
    fun `getProductById retorna null cuando API falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getProductById(1) } throws RuntimeException("404 Not Found")

        // WHEN
        val result = repository.getProductById(1)

        // THEN
        assertNull(result)
    }

    // ================= INSERT PRODUCT =================

    @Test
    fun `insertProduct retorna true cuando es exitoso`() = runBlocking {
        // GIVEN
        // La API devuelve el producto creado, simulamos eso
        coEvery { mockApi.createProduct(any()) } returns dummyProduct

        // WHEN
        val result = repository.insertProduct(dummyProduct)

        // THEN
        assertTrue(result)
        coVerify { mockApi.createProduct(any()) }
    }

    @Test
    fun `insertProduct retorna false cuando falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.createProduct(any()) } throws RuntimeException("Error")

        // WHEN
        val result = repository.insertProduct(dummyProduct)

        // THEN
        assertFalse(result)
    }

    // ================= UPDATE PRODUCT =================

    @Test
    fun `updateProduct retorna true cuando es exitoso`() = runBlocking {
        // GIVEN
        coEvery { mockApi.updateProduct(any(), any()) } returns dummyProduct

        // WHEN
        val result = repository.updateProduct(dummyProduct)

        // THEN
        assertTrue(result)
    }

    @Test
    fun `updateProduct retorna false cuando falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.updateProduct(any(), any()) } throws RuntimeException("Error")

        // WHEN
        val result = repository.updateProduct(dummyProduct)

        // THEN
        assertFalse(result)
    }

    // ================= DELETE PRODUCT =================

    @Test
    fun `deleteProduct retorna true cuando es exitoso`() = runBlocking {
        // GIVEN (coEvery para funciones Unit o Void usa returns Unit)
        coEvery { mockApi.deleteProduct(1) } returns Unit

        // WHEN
        val result = repository.deleteProduct(1)

        // THEN
        assertTrue(result)
    }

    @Test
    fun `deleteProduct retorna false cuando falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.deleteProduct(1) } throws RuntimeException("Error")

        // WHEN
        val result = repository.deleteProduct(1)

        // THEN
        assertFalse(result)
    }

    // ================= STOCK =================

    @Test
    fun `decreaseStock retorna true cuando es exitoso`() = runBlocking {
        // GIVEN
        coEvery { mockApi.updateStock(1, 5) } returns "Stock actualizado"

        // WHEN
        val result = repository.decreaseStock(1, 5)

        // THEN
        assertTrue(result)
    }

    @Test
    fun `decreaseStock retorna false cuando falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.updateStock(any(), any()) } throws RuntimeException("Error")

        // WHEN
        val result = repository.decreaseStock(1, 5)

        // THEN
        assertFalse(result)
    }

    // ================= IMAGES (GET) =================

    @Test
    fun `getImages retorna lista cuando es exitoso`() = runBlocking {
        // GIVEN
        val fakeImage = ImgEntity(1, "http://url.com", 1)
        coEvery { mockApi.getImagesByProduct(1) } returns listOf(fakeImage)

        // WHEN
        val result = repository.getImages(1)

        // THEN
        assertEquals(1, result.size)
        assertEquals("http://url.com", result[0].url)
    }

    @Test
    fun `getImages retorna lista vacia cuando falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getImagesByProduct(1) } throws RuntimeException("Error")

        // WHEN
        val result = repository.getImages(1)

        // THEN
        assertTrue(result.isEmpty())
    }

    // ================= IMAGES (UPLOAD) =================

    @Test
    fun `uploadImages retorna true cuando es exitoso`() = runBlocking {
        // GIVEN
        val base64List = listOf("data:image...")
        coEvery { mockApi.uploadImages(1, any()) } returns listOf(ImgEntity(1, "url", 1))

        // WHEN
        val result = repository.uploadImages(1, base64List)

        // THEN
        assertTrue(result)
    }

    @Test
    fun `uploadImages retorna false cuando falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.uploadImages(any(), any()) } throws RuntimeException("Error")

        // WHEN
        val result = repository.uploadImages(1, listOf("img"))

        // THEN
        assertFalse(result)
    }

    // ================= IMAGES (DELETE) =================

    @Test
    fun `deleteImage retorna true cuando es exitoso`() = runBlocking {
        // GIVEN
        coEvery { mockApi.deleteImage(100) } returns Unit

        // WHEN
        val result = repository.deleteImage(100)

        // THEN
        assertTrue(result)
    }

    @Test
    fun `deleteImage retorna false cuando falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.deleteImage(any()) } throws RuntimeException("Error")

        // WHEN
        val result = repository.deleteImage(100)

        // THEN
        assertFalse(result)
    }
}