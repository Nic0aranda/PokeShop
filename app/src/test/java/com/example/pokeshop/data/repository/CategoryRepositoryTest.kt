package com.example.pokeshop.data.repository

import com.example.pokeshop.data.entities.CategoryEntity
import com.example.pokeshop.data.network.ProductApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class CategoryRepositoryTest {

    // 1. Mock de la API
    private val mockApi = mockk<ProductApiService>()

    // 2. Repositorio con el Mock inyectado
    private val repository = CategoryRepository(mockApi)

    // Entidad de prueba
    private val dummyCategory = CategoryEntity(id = 1, name = "Cartas Raras")

    // ================= GET ALL CATEGORIES (FLOW) =================

    @Test
    fun `getAllCategories retorna lista cuando API responde`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getAllCategories() } returns listOf(dummyCategory)

        // WHEN
        val result = repository.getAllCategories().first()

        // THEN
        assertEquals(1, result.size)
        assertEquals("Cartas Raras", result[0].name)
    }

    @Test
    fun `getAllCategories retorna lista vacia cuando API falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getAllCategories() } throws RuntimeException("Error 500")

        // WHEN
        val result = repository.getAllCategories().first()

        // THEN
        assertTrue(result.isEmpty())
    }

    // ================= GET BY ID =================

    @Test
    fun `getCategoryById retorna categoria cuando existe`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getCategoryById(1) } returns dummyCategory

        // WHEN
        val result = repository.getCategoryById(1)

        // THEN
        assertNotNull(result)
        assertEquals(1L, result?.id)
    }

    @Test
    fun `getCategoryById retorna null cuando falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getCategoryById(1) } throws RuntimeException("404")

        // WHEN
        val result = repository.getCategoryById(1)

        // THEN
        assertNull(result)
    }

    // ================= INSERT =================

    @Test
    fun `insertCategory retorna true cuando es exitoso`() = runBlocking {
        // GIVEN
        coEvery { mockApi.createCategory(any()) } returns dummyCategory

        // WHEN
        val result = repository.insertCategory(dummyCategory)

        // THEN
        assertTrue(result)
    }

    @Test
    fun `insertCategory retorna false cuando falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.createCategory(any()) } throws RuntimeException("Error")

        // WHEN
        val result = repository.insertCategory(dummyCategory)

        // THEN
        assertFalse(result)
    }

    // ================= UPDATE =================

    @Test
    fun `updateCategory retorna true cuando es exitoso`() = runBlocking {
        // GIVEN
        coEvery { mockApi.updateCategory(any(), any()) } returns dummyCategory

        // WHEN
        val result = repository.updateCategory(dummyCategory)

        // THEN
        assertTrue(result)
    }

    @Test
    fun `updateCategory retorna false cuando falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.updateCategory(any(), any()) } throws RuntimeException("Error")

        // WHEN
        val result = repository.updateCategory(dummyCategory)

        // THEN
        assertFalse(result)
    }

    // ================= DELETE =================

    @Test
    fun `deleteCategory retorna true cuando es exitoso`() = runBlocking {
        // GIVEN
        coEvery { mockApi.deleteCategory(1) } returns Unit

        // WHEN
        val result = repository.deleteCategory(1)

        // THEN
        assertTrue(result)
    }

    @Test
    fun `deleteCategory retorna false cuando falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.deleteCategory(1) } throws RuntimeException("Error")

        // WHEN
        val result = repository.deleteCategory(1)

        // THEN
        assertFalse(result)
    }
}