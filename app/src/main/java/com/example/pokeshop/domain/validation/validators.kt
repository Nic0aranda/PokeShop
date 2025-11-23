package com.example.pokeshop.domain.validation

import android.util.Patterns

object Validation {

    /**
     * Valida el nombre de usuario:
     * - No puede estar vacío.
     * - Debe tener al menos 3 caracteres.
     * - No puede contener números.
     */
    fun validateUsername(username: String): String? {
        if (username.isBlank()) {
            return "El nombre de usuario no puede estar vacío."
        }
        if (username.length < 3) {
            return "El nombre debe tener al menos 3 letras."
        }
        // Verificamos si contiene algún dígito
        if (username.any { it.isDigit() }) {
            return "El nombre no puede contener números."
        }
        return null // Válido
    }

    /**
     * Valida el email:
     * - Usa el patrón nativo de Android.
     */
    fun validateEmail(email: String): String? {
        if (email.isBlank()) {
            return "El correo no puede estar vacío."
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Introduce un correo electrónico válido."
        }
        return null // Válido
    }

    /**
     * Valida la contraseña:
     * - Mínimo 8 caracteres.
     * - Al menos una mayúscula.
     * - Al menos una minúscula.
     * - Al menos un número.
     * - Al menos un carácter especial (algo que no sea letra ni número).
     */
    fun validatePassword(password: String): String? {
        if (password.length < 8) {
            return "La contraseña debe tener al menos 8 caracteres."
        }
        if (!password.any { it.isUpperCase() }) {
            return "La contraseña debe tener al menos una mayúscula."
        }
        if (!password.any { it.isLowerCase() }) {
            return "La contraseña debe tener al menos una minúscula."
        }
        if (!password.any { it.isDigit() }) {
            return "La contraseña debe tener al menos un número."
        }
        // Verificamos si hay al menos un carácter que NO sea letra ni dígito
        if (password.all { it.isLetterOrDigit() }) {
            return "La contraseña debe tener al menos un carácter especial (ej: @, #, $)."
        }

        return null // Válido
    }

    /**
     * Valida la confirmación de contraseña:
     * - Deben ser idénticas.
     */
    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        if (password != confirmPassword) {
            return "Las contraseñas no coinciden."
        }
        return null // Válido
    }
}