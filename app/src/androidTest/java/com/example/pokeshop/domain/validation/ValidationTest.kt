package com.example.pokeshop.domain.validation

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class ValidationTest {

    // ================= USERNAME TESTS =================

    @Test
    fun validateUsername_Valido_RetornaNull() {
        val result = Validation.validateUsername("AshKetchum")
        assertNull(result)
    }

    @Test
    fun validateUsername_Vacio_RetornaError() {
        val result = Validation.validateUsername("")
        assertEquals("El nombre de usuario no puede estar vacío.", result)
    }

    @Test
    fun validateUsername_MuyCorto_RetornaError() {
        val result = Validation.validateUsername("Jo")
        assertEquals("El nombre debe tener al menos 3 letras.", result)
    }

    @Test
    fun validateUsername_ConNumeros_RetornaError() {
        val result = Validation.validateUsername("Ash123")
        assertEquals("El nombre no puede contener números.", result)
    }

    // ================= EMAIL TESTS =================
    // Como estamos en androidTest, Patterns.EMAIL_ADDRESS funciona real.

    @Test
    fun validateEmail_Valido_RetornaNull() {
        val result = Validation.validateEmail("entrenador@poke.com")
        assertNull(result)
    }

    @Test
    fun validateEmail_FormatoInvalido_RetornaError() {
        val result = Validation.validateEmail("estoNoEsUnCorreo")
        assertEquals("Introduce un correo electrónico válido.", result)
    }

    @Test
    fun validateEmail_Vacio_RetornaError() {
        val result = Validation.validateEmail("")
        assertEquals("El correo no puede estar vacío.", result)
    }

    // ================= PASSWORD TESTS =================

    @Test
    fun validatePassword_Valido_RetornaNull() {
        // Tiene 8 chars, Mayus, Minus, Numero, Especial
        val result = Validation.validatePassword("Pikachu1!")
        assertNull(result)
    }

    @Test
    fun validatePassword_MuyCorta_RetornaError() {
        val result = Validation.validatePassword("Pika1!")
        assertEquals("La contraseña debe tener al menos 8 caracteres.", result)
    }

    @Test
    fun validatePassword_SinMayuscula_RetornaError() {
        val result = Validation.validatePassword("pikachu1!")
        assertEquals("La contraseña debe tener al menos una mayúscula.", result)
    }

    @Test
    fun validatePassword_SinMinuscula_RetornaError() {
        val result = Validation.validatePassword("PIKACHU1!")
        assertEquals("La contraseña debe tener al menos una minúscula.", result)
    }

    @Test
    fun validatePassword_SinNumero_RetornaError() {
        val result = Validation.validatePassword("Pikachu!!")
        assertEquals("La contraseña debe tener al menos un número.", result)
    }

    @Test
    fun validatePassword_SinCaracterEspecial_RetornaError() {
        val result = Validation.validatePassword("Pikachu12")
        assertEquals("La contraseña debe tener al menos un carácter especial (ej: @, #, $).", result)
    }

    // ================= CONFIRM PASSWORD TESTS =================

    @Test
    fun validateConfirmPassword_Iguales_RetornaNull() {
        val result = Validation.validateConfirmPassword("123456", "123456")
        assertNull(result)
    }

    @Test
    fun validateConfirmPassword_Distintas_RetornaError() {
        val result = Validation.validateConfirmPassword("123456", "abcdef")
        assertEquals("Las contraseñas no coinciden.", result)
    }
}