package com.example.pokeshop.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeshop.data.entities.*
import com.example.pokeshop.data.repository.*
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

    // Estados de la UI
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    //Estado UI login
    data class LoginUiState(                                   // Estado de la pantalla Login
        val email: String = "",                                // Campo email
        val pass: String = "",                                 // Campo contraseña (texto)
        val emailError: String? = null,                        // Error de email
        val passError: String? = null,                         // (Opcional) error de pass en login
        val isSubmitting: Boolean = false,                     // Flag de carga
        val canSubmit: Boolean = false,                        // Habilitar botón
        val success: Boolean = false,                          // Resultado OK
        val errorMsg: String? = null                           // Error global (credenciales inválidas)
    )
    var UiStateLogin by mutableStateOf(LoginUiState())
        private set

    //Funcion para login
    // Función para actualizar email
    fun updateEmail(email: String) {
        UiStateLogin = UiStateLogin.copy(
            email = email,
            emailError = if (email.isBlank()) "Email es requerido" else null,
            canSubmit = validateForm(email, UiStateLogin.pass)
        )
    }

    // Función para actualizar contraseña
    fun updatePassword(pass: String) {
        UiStateLogin = UiStateLogin.copy(
            pass = pass,
            passError = if (pass.isBlank()) "Contraseña es requerida" else null,
            canSubmit = validateForm(UiStateLogin.email, pass)
        )
    }

    // Validación básica del formulario
    private fun validateForm(email: String, pass: String): Boolean {
        return email.isNotBlank() && pass.isNotBlank()
    }

    // Función para realizar el login
    fun loginUser(onSuccess: (isAdmin: Boolean) -> Unit) {
        viewModelScope.launch {
            UiStateLogin = UiStateLogin.copy(isSubmitting = true, errorMsg = null)

            try {
                // Buscar usuario por email
                val user = userRepository.getUserByEmail(UiStateLogin.email)

                if (user != null && user.password == UiStateLogin.pass) {
                    // Verificar el rol del usuario
                    val isAdmin = user.rolId == 1L // Asumiendo que 1 es admin

                    UiStateLogin = UiStateLogin.copy(
                        success = true,
                        isSubmitting = false
                    )

                    // Llamar al callback con el tipo de usuario
                    onSuccess(isAdmin)
                } else {
                    UiStateLogin = UiStateLogin.copy(
                        errorMsg = "Credenciales inválidas",
                        isSubmitting = false,
                        success = false
                    )
                }
            } catch (e: Exception) {
                UiStateLogin = UiStateLogin.copy(
                    errorMsg = "Error: ${e.message}",
                    isSubmitting = false,
                    success = false
                )
            }
        }
    }

    // Limpiar estado
    fun clearLoginState() {
        UiStateLogin = LoginUiState()
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