package com.example.pokeshop.domain.validation

import android.util.Patterns

// Objeto para agrupar las funciones de validación
object Validation {

    fun validateUsername(username: String): String? {
        return if (username.isBlank()) {
            "El nombre de usuario no puede estar vacío."
        } else {
            null // Válido
        }
    }

    fun validateEmail(email: String): String? {
        return if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Introduce un correo electrónico válido."
        } else {
            null // Válido
        }
    }

    fun validatePassword(password: String): String? {
        return if (password.length < 6) {
            "La contraseña debe tener al menos 6 caracteres."
        } else {
            null // Válido
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return if (password != confirmPassword) {
            "Las contraseñas no coinciden."
        } else {
            null // Válido
        }
    }

    // --- Validaciones para Login (más sencillas, solo comprueban que no estén vacíos) ---

    fun isLoginEmailValid(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isLoginPasswordValid(pass: String): Boolean {
        return pass.isNotBlank()
    }
}