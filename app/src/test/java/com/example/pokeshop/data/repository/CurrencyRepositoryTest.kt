package com.example.pokeshop.data.repository

import com.example.pokeshop.data.network.CurrencyApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyRepositoryTest {

    // 1. Mock de la API de Moneda
    private val mockApi = mockk<CurrencyApiService>()

    // 2. Repositorio con el Mock inyectado
    private val repository = CurrencyRepository(mockApi)

    // ================= GET DOLAR VALUE =================

    @Test
    fun `getDolarValue retorna valor de la API cuando es exitoso`() = runBlocking {
        // GIVEN: La API responde que el dólar está a 950.5
        coEvery { mockApi.getDolarPrice() } returns 950.5

        // WHEN
        val result = repository.getDolarValue()

        // THEN
        assertEquals(950.5, result, 0.0)
    }

    @Test
    fun `getDolarValue retorna valor por defecto (980) cuando API falla`() = runBlocking {
        // GIVEN: La API falla (ej: sin internet)
        coEvery { mockApi.getDolarPrice() } throws RuntimeException("Error de conexión")

        // WHEN
        val result = repository.getDolarValue()

        // THEN: Debería devolver el valor "fallback" hardcodeado en el repositorio
        assertEquals(980.0, result, 0.0)
    }

    // ================= CONVERT TO CLP =================

    @Test
    fun `convertToClp retorna conversion de la API cuando es exitoso`() = runBlocking {
        // GIVEN: Queremos convertir 10 USD. La API dice que son 9500 CLP.
        val amountUsd = 10.0
        val expectedClp = 9500.0
        coEvery { mockApi.convertUsdToClp(amountUsd) } returns expectedClp

        // WHEN
        val result = repository.convertToClp(amountUsd)

        // THEN
        assertEquals(expectedClp, result, 0.0)
    }

    @Test
    fun `convertToClp realiza calculo manual cuando API falla`() = runBlocking {
        // GIVEN: La API falla.
        val amountUsd = 10.0
        coEvery { mockApi.convertUsdToClp(amountUsd) } throws RuntimeException("Error")

        // WHEN
        val result = repository.convertToClp(amountUsd)

        // THEN: El cálculo manual es amount * 980.0 -> 10 * 980 = 9800
        val expectedManualCalculation = 10.0 * 980.0
        assertEquals(expectedManualCalculation, result, 0.0)
    }
}