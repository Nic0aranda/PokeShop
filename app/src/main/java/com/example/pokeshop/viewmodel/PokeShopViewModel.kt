package com.example.pokeshop.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeshop.data.entities.*
import com.example.pokeshop.data.repository.*
import com.example.pokeshop.domain.validation.Validation // <-- Importar el archivo de validaciones
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

    // Estados de la UI generales
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    //ESTADO Y LÓGICA DE LOGIN

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
        // Usamos las funciones de Validation para validar los campos
        return Validation.isLoginEmailValid(email) && Validation.isLoginPasswordValid(pass)
    }

    fun loginUser(onSuccess: (isAdmin: Boolean) -> Unit) {
        viewModelScope.launch {
            uiStateLogin = uiStateLogin.copy(isSubmitting = true, errorMsg = null)

            try {
                val user = userRepository.getUserByEmail(uiStateLogin.email)

                if (user != null && user.password == uiStateLogin.pass) {
                    val isAdmin = user.rolId == 1L // Asumiendo que 1 es admin
                    uiStateLogin = uiStateLogin.copy(
                        success = true,
                        isSubmitting = false,
                        // Usamos errorMsg para pasar el rol, ya que no se mostrará si hay éxito
                        errorMsg = if (isAdmin) "admin" else "user"
                    )
                    // Invocamos el callback directamente aquí para desacoplarlo de LaunchedEffect
                    onSuccess(isAdmin)
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
            // 1. Validar campos
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

            // 2. Iniciar el proceso de registro
            uiStateRegister = uiStateRegister.copy(isSubmitting = true, errorMsg = null)

            try {
                // Verificar si el email ya existe
                val existingUser = userRepository.getUserByEmail(uiStateRegister.email)
                if (existingUser != null) {
                    uiStateRegister = uiStateRegister.copy(
                        emailError = "El correo electrónico ya está registrado.",
                        isSubmitting = false
                    )
                    return@launch
                }

                // 3. Crear y guardar el usuario
                val newUser = UserEntity(
                    names = uiStateRegister.username,
                    lastNames = "", // Campo opcional, puedes añadirlo a la UI si quieres
                    email = uiStateRegister.email,
                    password = uiStateRegister.pass,
                    status = true,
                    rolId = 2 // Por defecto, rol cliente (asumiendo 2)
                )
                userRepository.insertUser(newUser)

                // 4. Actualizar estado a éxito
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
                // Insertar roles
                val roles = listOf(
                    RolEntity(name = "Vendedor"),
                    RolEntity(name = "Cliente")
                )

                roles.forEach { rolRepository.insertRol(it) }

                // Insertar categorías de ejemplo
                val categories = listOf(
                    CategoryEntity(name = "Otro tipo de producto"),
                    CategoryEntity(name = "Booster packs"),
                    CategoryEntity(name = "Sobres"),
                    CategoryEntity(name = "Cajas")
                )

                categories.forEach { categoryRepository.insertCategory(it) }

                // Insertar productos de ejemplo - Cartas Pokémon
                val products = listOf(
                    // Booster Packs (categoría 2)
                    ProductEntity(
                        name = "Booster Pack Escarlata y Púrpura",
                        description = "Sobre de 10 cartas de la nueva generación",
                        price = 1200.0,
                        stock = 25,
                        categoryId = 2
                    ),
                    ProductEntity(
                        name = "Booster Pack Espada y Escudo",
                        description = "Sobre de 10 cartas de la generación Galar",
                        price = 1000.0,
                        stock = 30,
                        categoryId = 2
                    ),
                    ProductEntity(
                        name = "Booster Pack Sol y Luna",
                        description = "Sobre de 10 cartas de la generación Alola",
                        price = 900.0,
                        stock = 15,
                        categoryId = 2
                    ),

                    // Sobres (categoría 3)
                    ProductEntity(
                        name = "Sobre Promocional Pikachu",
                        description = "Sobre especial con carta promocional de Pikachu",
                        price = 800.0,
                        stock = 40,
                        categoryId = 3
                    ),
                    ProductEntity(
                        name = "Sobre Edición Especial Charizard",
                        description = "Sobre con carta holográfica de Charizard",
                        price = 1500.0,
                        stock = 20,
                        categoryId = 3
                    ),
                    ProductEntity(
                        name = "Sobre Coleccionista Mewtwo",
                        description = "Sobre limitado con carta de Mewtwo",
                        price = 2000.0,
                        stock = 10,
                        categoryId = 3
                    ),

                    // Cajas (categoría 4)
                    ProductEntity(
                        name = "Caja Elite Trainer",
                        description = "Caja completa con 8 boosters, dados y accesorios",
                        price = 12000.0,
                        stock = 8,
                        categoryId = 4
                    ),
                    ProductEntity(
                        name = "Caja Coleccionista Legendaria",
                        description = "Caja con cartas legendarias exclusivas",
                        price = 18000.0,
                        stock = 5,
                        categoryId = 4
                    ),
                    ProductEntity(
                        name = "Caja Premium V-Star",
                        description = "Caja premium con garantía de carta V-Star",
                        price = 25000.0,
                        stock = 3,
                        categoryId = 4
                    ),

                    // Otros productos (categoría 1)
                    ProductEntity(
                        name = "Protector de Cartas Premium",
                        description = "Set de 50 protectores para cartas",
                        price = 500.0,
                        stock = 100,
                        categoryId = 1
                    ),
                    ProductEntity(
                        name = "Álbum de Colección",
                        description = "Álbum para guardar y organizar cartas",
                        price = 3000.0,
                        stock = 20,
                        categoryId = 1
                    ),
                    ProductEntity(
                        name = "Dados Oficiales Pokémon",
                        description = "Set de dados para juegos de cartas",
                        price = 400.0,
                        stock = 50,
                        categoryId = 1
                    )
                )

                products.forEach { productRepository.insertProduct(it) }

                // Insertar usuarios de ejemplo con roles
                val users = listOf(
                    UserEntity(
                        names = "Misty",
                        lastNames = "Waterflower",
                        email = "misty@pokemon.com",
                        password = "growlithe123",
                        status = true,
                        rolId = 2 //cliente
                    ),
                    UserEntity(
                        names = "Brock",
                        lastNames = "Takeshi",
                        email = "brock@pokemon.com",
                        password = "vendedor2025",
                        status = true,
                        rolId = 1 //Vendedor
                    )
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
