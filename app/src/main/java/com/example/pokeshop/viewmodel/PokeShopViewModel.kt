package com.example.pokeshop.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeshop.data.entities.*
import com.example.pokeshop.data.repository.*
import com.example.pokeshop.domain.validation.Validation
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PokeShopViewModel(
    private val userRepository: UserRepository,
    private val rolRepository: RolRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val saleRepository: SaleRepository,
    private val saleDetailRepository: SaleDetailRepository
) : ViewModel() {

    // --- ESTADO DEL USUARIO (MODIFICADO) ---
    // Se usa StateFlow para una mejor gestión del estado a través de la app.
    data class UserState(
        val username: String = "",
        val email: String = "",
        val isLoggedIn: Boolean = false,
        val isAdmin: Boolean = false
    )

    // _userState es privado y mutable, solo el ViewModel puede cambiarlo.
    private val _userState = MutableStateFlow(UserState())
    // userState es público e inmutable, las Vistas solo pueden leerlo.
    val userState: StateFlow<UserState> = _userState.asStateFlow()


    // Estados de la UI generales
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    //region === ESTADO Y LÓGICA DE LOGIN (MODIFICADO) ===

    data class LoginUiState(
        val email: String = "",
        val pass: String = "",
        val emailError: String? = null,
        val passError: String? = null,
        val isSubmitting: Boolean = false,
        val canSubmit: Boolean = false,
        val success: Boolean = false,
        val errorMsg: String? = null
    )
    var uiStateLogin by mutableStateOf(LoginUiState())
        private set

    fun updateLoginEmail(email: String) {
        val isValid = Validation.isLoginEmailValid(email)
        uiStateLogin = uiStateLogin.copy(
            email = email,
            emailError = if (!isValid && email.isNotBlank()) "Email no válido" else null,
            canSubmit = validateLoginForm(email, uiStateLogin.pass)
        )
    }

    fun updateLoginPassword(pass: String) {
        uiStateLogin = uiStateLogin.copy(
            pass = pass,
            passError = if (pass.isBlank()) "Contraseña es requerida" else null,
            canSubmit = validateLoginForm(uiStateLogin.email, pass)
        )
    }

    private fun validateLoginForm(email: String, pass: String): Boolean {
        return Validation.isLoginEmailValid(email) && Validation.isLoginPasswordValid(pass)
    }

    fun loginUser(onSuccess: (isAdmin: Boolean) -> Unit) {
        viewModelScope.launch {
            uiStateLogin = uiStateLogin.copy(isSubmitting = true, errorMsg = null)

            try {
                val user = userRepository.getUserByEmail(uiStateLogin.email)

                if (user != null && user.password == uiStateLogin.pass) {
                    val isAdmin = user.rolId == 1L // Asumiendo que 1 es admin

                    // --- ACTUALIZACIÓN CLAVE ---
                    // Actualizamos el estado global del usuario aquí.
                    _userState.value = UserState(
                        username = user.names,
                        email = user.email,
                        isLoggedIn = true,
                        isAdmin = isAdmin
                    )

                    uiStateLogin = uiStateLogin.copy(success = true, isSubmitting = false)
                    onSuccess(isAdmin) // Llama al callback para navegar

                } else {
                    uiStateLogin = uiStateLogin.copy(
                        errorMsg = "Credenciales inválidas",
                        isSubmitting = false,
                        success = false
                    )
                }
            } catch (e: Exception) {
                uiStateLogin = uiStateLogin.copy(
                    errorMsg = "Error: ${e.message}",
                    isSubmitting = false,
                    success = false
                )
            }
        }
    }

    fun clearLoginState() {
        uiStateLogin = LoginUiState()
    }

    //endregion

    //region === ESTADO Y LÓGICA DE REGISTRO ===

    data class RegisterUiState(
        val username: String = "",
        val email: String = "",
        val pass: String = "",
        val confirmPass: String = "",
        val usernameError: String? = null,
        val emailError: String? = null,
        val passError: String? = null,
        val confirmPassError: String? = null,
        val isSubmitting: Boolean = false,
        val canSubmit: Boolean = false,
        val success: Boolean = false,
        val errorMsg: String? = null
    )
    var uiStateRegister by mutableStateOf(RegisterUiState())
        private set

    fun updateRegisterUsername(username: String) {
        uiStateRegister = uiStateRegister.copy(username = username, usernameError = null)
        validateRegisterForm()
    }

    fun updateRegisterEmail(email: String) {
        uiStateRegister = uiStateRegister.copy(email = email, emailError = null)
        validateRegisterForm()
    }

    fun updateRegisterPassword(pass: String) {
        uiStateRegister = uiStateRegister.copy(pass = pass, passError = null)
        validateRegisterForm()
    }

    fun updateRegisterConfirmPassword(confirmPass: String) {
        uiStateRegister = uiStateRegister.copy(confirmPass = confirmPass, confirmPassError = null)
        validateRegisterForm()
    }

    private fun validateRegisterForm() {
        val state = uiStateRegister
        val canSubmit = state.username.isNotBlank() &&
                state.email.isNotBlank() &&
                state.pass.isNotBlank() &&
                state.confirmPass.isNotBlank()
        uiStateRegister = uiStateRegister.copy(canSubmit = canSubmit)
    }

    fun registerUser() {
        viewModelScope.launch {
            val usernameError = Validation.validateUsername(uiStateRegister.username)
            val emailError = Validation.validateEmail(uiStateRegister.email)
            val passError = Validation.validatePassword(uiStateRegister.pass)
            val confirmPassError = Validation.validateConfirmPassword(uiStateRegister.pass, uiStateRegister.confirmPass)

            uiStateRegister = uiStateRegister.copy(
                usernameError = usernameError,
                emailError = emailError,
                passError = passError,
                confirmPassError = confirmPassError
            )

            val hasErrors = listOf(usernameError, emailError, passError, confirmPassError).any { it != null }
            if (hasErrors) return@launch

            uiStateRegister = uiStateRegister.copy(isSubmitting = true, errorMsg = null)

            try {
                val existingUser = userRepository.getUserByEmail(uiStateRegister.email)
                if (existingUser != null) {
                    uiStateRegister = uiStateRegister.copy(
                        emailError = "El correo electrónico ya está registrado.",
                        isSubmitting = false
                    )
                    return@launch
                }

                val newUser = UserEntity(
                    names = uiStateRegister.username,
                    lastNames = "",
                    email = uiStateRegister.email,
                    password = uiStateRegister.pass,
                    status = true,
                    rolId = 2
                )
                userRepository.insertUser(newUser)

                uiStateRegister = uiStateRegister.copy(success = true, isSubmitting = false)

            } catch (e: Exception) {
                uiStateRegister = uiStateRegister.copy(
                    errorMsg = "Error en el registro: ${e.message}",
                    isSubmitting = false
                )
            }
        }
    }

    fun clearRegisterState() {
        uiStateRegister = RegisterUiState()
    }
    //endregion

    //region === LÓGICA DE PERFIL Y SESIÓN (AÑADIDO) ===

    /**
     * Cierra la sesión del usuario actual.
     * Restablece el estado del usuario a sus valores predeterminados.
     */
    fun logout() {
        viewModelScope.launch {
            // Aquí podrías añadir lógica para limpiar tokens o preferencias si los tuvieras.
            _userState.value = UserState() // Restablece el estado del usuario
            clearLoginState() // Opcional: Limpia el estado del formulario de login
        }
    }

    //endregion

    // obtener todos los productos
    val allProducts = productRepository.getAllProducts()

    //funcion para limpiar todos los datos del viewModel
    fun clearAllData() {
        viewModelScope.launch {
            productRepository.deleteAllProducts()
        }
    }

    // datos de prueba o insert de datos
    fun insertSampleData() {
        viewModelScope.launch {
            try {
                val roles = listOf(RolEntity(name = "Vendedor"), RolEntity(name = "Cliente"))
                roles.forEach { rolRepository.insertRol(it) }

                val categories = listOf(
                    CategoryEntity(name = "Otro tipo de producto"),
                    CategoryEntity(name = "Booster packs"),
                    CategoryEntity(name = "Sobres"),
                    CategoryEntity(name = "Cajas")
                )
                categories.forEach { categoryRepository.insertCategory(it) }

                val products = listOf(
                    ProductEntity(name = "Booster Pack Escarlata y Púrpura", description = "Sobre de 10 cartas de la nueva generación", price = 1200.0, stock = 25, categoryId = 2),
                    ProductEntity(name = "Booster Pack Espada y Escudo", description = "Sobre de 10 cartas de la generación Galar", price = 1000.0, stock = 30, categoryId = 2),
                    ProductEntity(name = "Sobre Promocional Pikachu", description = "Sobre especial con carta promocional de Pikachu", price = 800.0, stock = 40, categoryId = 3),
                    ProductEntity(name = "Caja Elite Trainer", description = "Caja completa con 8 boosters, dados y accesorios", price = 12000.0, stock = 8, categoryId = 4)
                    // ... otros productos ...
                )
                products.forEach { productRepository.insertProduct(it) }

                val users = listOf(
                    UserEntity(names = "Misty", lastNames = "Waterflower", email = "misty@pokemon.com", password = "growlithe123", status = true, rolId = 2),
                    UserEntity(names = "Brock", lastNames = "Takeshi", email = "brock@pokemon.com", password = "vendedor2025", status = true, rolId = 1)
                )
                users.forEach { userRepository.insertUser(it) }

                _uiState.value = UiState.Success("Datos de cartas Pokémon insertados")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al insertar datos: ${e.message}")
            }
        }
    }

    // Estados de la UI
    sealed class UiState {
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val message: String) : UiState()
        object Idle : UiState()
    }
}
