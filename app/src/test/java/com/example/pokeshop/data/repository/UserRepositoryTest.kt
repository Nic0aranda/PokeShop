package com.example.pokeshop.data.repository

import com.example.pokeshop.data.entities.UserEntity
import com.example.pokeshop.data.network.UserApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class UserRepositoryTest {

    // 1. Mock de la API
    private val mockApi = mockk<UserApiService>()

    // 2. Repositorio con el Mock
    private val repository = UserRepository(mockApi)

    // Usuario ficticio para pruebas
    private val dummyUser = UserEntity(
        id = 1,
        names = "Ash",
        email = "ash@poke.com",
        password = "123",
        status = true,
        rol = null
    )

    // ================= GET ALL USERS =================

    @Test
    fun `getAllUsers retorna lista cuando API responde`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getAllUsers() } returns listOf(dummyUser)

        // WHEN
        val result = repository.getAllUsers().first()

        // THEN
        assertEquals(1, result.size)
        assertEquals("Ash", result[0].names)
    }

    @Test
    fun `getAllUsers retorna vacio cuando API falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getAllUsers() } throws RuntimeException("Error")

        // WHEN
        val result = repository.getAllUsers().first()

        // THEN
        assertTrue(result.isEmpty())
    }

    // ================= GET USER BY ID =================

    @Test
    fun `getUserById retorna usuario cuando existe`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getUserById(1) } returns dummyUser

        // WHEN
        val result = repository.getUserById(1)

        // THEN
        assertNotNull(result)
        assertEquals(1L, result?.id)
    }

    @Test
    fun `getUserById retorna null cuando falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getUserById(any()) } throws RuntimeException("Error")

        // WHEN
        val result = repository.getUserById(1)

        // THEN
        assertNull(result)
    }

    // ================= GET USER BY EMAIL (Lógica especial) =================

    @Test
    fun `getUserByEmail encuentra el usuario correcto en la lista`() = runBlocking {
        // GIVEN: La API devuelve una lista con 2 usuarios
        val user1 = dummyUser.copy(email = "otro@poke.com")
        val user2 = dummyUser.copy(email = "ash@poke.com") // Este es el que buscamos

        // Mockeamos getAllUsers porque es lo que llama el repositorio internamente
        coEvery { mockApi.getAllUsers() } returns listOf(user1, user2)

        // WHEN: Buscamos "ash@poke.com"
        val result = repository.getUserByEmail("ash@poke.com")

        // THEN: Debe encontrar el user2
        assertNotNull(result)
        assertEquals("ash@poke.com", result?.email)
    }

    @Test
    fun `getUserByEmail retorna null si el email no esta en la lista`() = runBlocking {
        // GIVEN: La lista no tiene ese email
        coEvery { mockApi.getAllUsers() } returns listOf(dummyUser)

        // WHEN
        val result = repository.getUserByEmail("noexiste@mail.com")

        // THEN
        assertNull(result)
    }

    @Test
    fun `getUserByEmail retorna null si la API falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.getAllUsers() } throws RuntimeException("Error")

        // WHEN
        val result = repository.getUserByEmail("ash@poke.com")

        // THEN
        assertNull(result)
    }

    // ================= INSERT USER =================

    @Test
    fun `insertUser retorna ID si es exitoso`() = runBlocking {
        // GIVEN
        coEvery { mockApi.createUser(any()) } returns dummyUser

        // WHEN
        val result = repository.insertUser(dummyUser)

        // THEN
        assertEquals(1L, result)
    }

    @Test
    fun `insertUser retorna -1 si falla`() = runBlocking {
        // GIVEN
        coEvery { mockApi.createUser(any()) } throws RuntimeException("Error")

        // WHEN
        val result = repository.insertUser(dummyUser)

        // THEN
        assertEquals(-1L, result)
    }

    // ================= UPDATES & DELETE =================

    @Test
    fun `updateUser llama a la API correctamente`() = runBlocking {
        coEvery { mockApi.updateUser(any(), any()) } returns dummyUser
        repository.updateUser(dummyUser)
        coVerify { mockApi.updateUser(1, dummyUser) }
    }

    @Test
    fun `updateUserStatus llama a la API correctamente`() = runBlocking {
        coEvery { mockApi.updateUserStatus(any(), any()) } returns Unit
        repository.updateUserStatus(1, false)
        coVerify { mockApi.updateUserStatus(1, false) }
    }

    @Test
    fun `deleteUser llama a la API correctamente`() = runBlocking {
        coEvery { mockApi.deleteUser(any()) } returns Unit
        repository.deleteUser(dummyUser)
        coVerify { mockApi.deleteUser(1) }
    }
}