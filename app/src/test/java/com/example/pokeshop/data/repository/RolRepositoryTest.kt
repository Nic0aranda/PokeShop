package com.example.pokeshop.data.repository

import com.example.pokeshop.data.entities.RolEntity
import com.example.pokeshop.data.network.UserApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class RolRepositoryTest {

    // 1. Mock de la API (UserApiService maneja roles también)
    private val mockApi = mockk<UserApiService>()

    // 2. Repositorio con el Mock inyectado
    private val repository = RolRepository(mockApi)

    // Entidad de prueba
    private val dummyRol = RolEntity(id = 1, name = "Admin")

    // ================= GET ALL ROLES =================

    @Test
    fun `getAllRoles retorna lista cuando API responde`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getAllRoles() } returns listOf(dummyRol)

        // WHEN
        val result = repository.getAllRoles().first()

        // THEN
        assertEquals(1, result.size)
        assertEquals("Admin", result[0].name)
    }

    @Test
    fun `getAllRoles retorna lista vacia cuando API falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getAllRoles() } throws RuntimeException("Error")

        // WHEN
        val result = repository.getAllRoles().first()

        // THEN
        assertTrue(result.isEmpty())
    }

    // ================= GET ROL BY ID =================

    @Test
    fun `getRolById retorna rol cuando existe`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getRoleById(1) } returns dummyRol

        // WHEN
        val result = repository.getRolById(1)

        // THEN
        assertNotNull(result)
        assertEquals("Admin", result?.name)
    }

    @Test
    fun `getRolById retorna null cuando falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getRoleById(any()) } throws RuntimeException("Error")

        // WHEN
        val result = repository.getRolById(1)

        // THEN
        assertNull(result)
    }

    // ================= INSERT ROL =================

    @Test
    fun `insertRol retorna ID cuando es exitoso`() = runBlocking {
        // GIVEN
        coEvery { mockApi.createRole(any()) } returns dummyRol

        // WHEN
        val result = repository.insertRol(dummyRol)

        // THEN
        assertEquals(1L, result)
    }

    @Test
    fun `insertRol retorna -1 cuando falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.createRole(any()) } throws RuntimeException("Error")

        // WHEN
        val result = repository.insertRol(dummyRol)

        // THEN
        assertEquals(-1L, result)
    }

    // ================= UPDATE ROL =================

    @Test
    fun `updateRol llama a la API correctamente`() = runBlocking {
        // GIVEN
        coEvery { mockApi.updateRole(any(), any()) } returns dummyRol

        // WHEN
        repository.updateRol(dummyRol)

        // THEN
        coVerify(exactly = 1) { mockApi.updateRole(1, dummyRol) }
    }

    @Test
    fun `updateRol maneja excepcion sin crashear`() = runBlocking {
        // GIVEN
        coEvery { mockApi.updateRole(any(), any()) } throws RuntimeException("Error")

        // WHEN
        repository.updateRol(dummyRol)

        // THEN: Si llegamos aquí sin excepción, el test pasa (el repo captura el error)
        coVerify(exactly = 1) { mockApi.updateRole(1, dummyRol) }
    }

    // ================= DELETE ROL =================

    @Test
    fun `deleteRol llama a la API correctamente`() = runBlocking {
        // GIVEN
        coEvery { mockApi.deleteRole(any()) } returns Unit

        // WHEN
        repository.deleteRol(dummyRol)

        // THEN
        coVerify(exactly = 1) { mockApi.deleteRole(1) }
    }

    @Test
    fun `deleteRol maneja excepcion sin crashear`() = runBlocking {
        // GIVEN
        coEvery { mockApi.deleteRole(any()) } throws RuntimeException("Error")

        // WHEN
        repository.deleteRol(dummyRol)

        // THEN: Si llegamos aquí sin excepción, el test pasa
        coVerify(exactly = 1) { mockApi.deleteRole(1) }
    }
}