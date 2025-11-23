package com.example.pokeshop.data.repository

import com.example.pokeshop.data.dto.SaleResponseDto
import com.example.pokeshop.data.network.CurrencyApiService
import com.example.pokeshop.data.network.SalesApiService
import com.example.pokeshop.viewmodel.CartItem
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response

class SaleRepositoryTest {

    // 1. Mockeamos ambas APIs
    private val mockSalesApi = mockk<SalesApiService>()
    private val mockCurrencyApi = mockk<CurrencyApiService>()

    // 2. Inyectamos los mocks
    private val repository = SaleRepository(mockSalesApi, mockCurrencyApi)

    // Datos de prueba
    private val cartItem = CartItem(productId = 1, name = "Pikachu", price = 100.0, quantity = 2, stock = 10)
    private val cartList = listOf(cartItem)

    // ================= CHECKOUT (VENTAS) =================

    @Test
    fun `checkout retorna SUCCESS cuando la API responde 200 OK`() = runBlocking {
        // GIVEN: La API responde correctamente con un objeto Venta
        val fakeSaleResponse = SaleResponseDto(id = 123, total = 200.0, status = "COMPLETADA")

        // Simulamos una respuesta HTTP 200 de Retrofit
        coEvery { mockSalesApi.createSale(any()) } returns Response.success(fakeSaleResponse)

        // WHEN
        val result = repository.checkout(1L, cartList)

        // THEN
        assertTrue(result.success)
        assertTrue(result.message.contains("Venta #123"))
    }

    @Test
    fun `checkout retorna FAILURE cuando la API responde 400 Bad Request (Stock insuficiente)`() = runBlocking {
        // GIVEN: El backend devuelve error 400 con un mensaje de texto plano
        val errorJson = "Stock insuficiente para: Pikachu"
        val errorBody = errorJson.toResponseBody("text/plain".toMediaTypeOrNull())

        // Simulamos una respuesta HTTP 400 de Retrofit
        coEvery { mockSalesApi.createSale(any()) } returns Response.error(400, errorBody)

        // WHEN
        val result = repository.checkout(1L, cartList)

        // THEN
        assertFalse(result.success) // Debe fallar
        assertEquals("Stock insuficiente para: Pikachu", result.message) // Debe contener el mensaje del backend
    }

    @Test
    fun `checkout retorna FAILURE cuando hay excepcion de red`() = runBlocking {
        // GIVEN: Falla la conexión (sin internet)
        coEvery { mockSalesApi.createSale(any()) } throws RuntimeException("No internet")

        // WHEN
        val result = repository.checkout(1L, cartList)

        // THEN
        assertFalse(result.success)
        assertTrue(result.message.contains("Error de conexión"))
    }

    // ================= GET DOLAR PRICE =================

    @Test
    fun `getDolarPrice retorna valor real si API funciona`() = runBlocking {
        coEvery { mockCurrencyApi.getDolarPrice() } returns 950.0
        val result = repository.getDolarPrice()
        assertEquals(950.0, result, 0.0)
    }

    @Test
    fun `getDolarPrice retorna valor por defecto (980) si API falla`() = runBlocking {
        coEvery { mockCurrencyApi.getDolarPrice() } throws RuntimeException("Error")
        val result = repository.getDolarPrice()
        assertEquals(980.0, result, 0.0)
    }
}