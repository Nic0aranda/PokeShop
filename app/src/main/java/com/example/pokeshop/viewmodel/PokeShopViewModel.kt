package com.example.pokeshop.viewmodel

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeshop.data.entities.*
import com.example.pokeshop.data.repository.*
import com.example.pokeshop.domain.validation.Validation
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// --- 1. DEFINICIONES DE ESTADOS (Data Classes) ---

data class PokeShopUiState(
    val isLoading: Boolean = true,
    val productDetail: ProductEntity? = null,
    val currentUser: UserEntity? = null,
    val isAdmin: Boolean = false,
    val userMessage: String? = null
)

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

data class UserState(
    val username: String = "",
    val email: String = ""
)

data class CatalogUiState(
    val isLoading: Boolean = true,
    val products: List<ProductEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryId: Long? = null
)

data class CartItem(
    val productId: Int,
    val name: String,
    val price: Double,
    var quantity: Int,
)

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val total: Double = 0.0
)

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: ProductEntity? = null,
    val error: String? = null
)

data class Category(
    val id: Long,
    val name: String
)

data class CategoryManagementUiState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList()
)

data class CategoryEditUiState(
    val isLoading: Boolean = true,
    val category: Category? = null,
    val categoryName: String = "",
    val nameError: String? = null,
    val canBeSaved: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

data class CategoryCreateUiState(
    val categoryName: String = "",
    val nameError: String? = null,
    val canBeCreated: Boolean = false,
    val isCreating: Boolean = false,
    val createSuccess: Boolean = false,
    val errorMessage: String? = null
)

// --- 2. VIEWMODEL PRINCIPAL ---

class PokeShopViewModel(
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    // --- 3. ESTADOS (StateFlows y MutableState) ---

    // Estado global de la app
    private val _uiState = MutableStateFlow(PokeShopUiState())

    // Estado del perfil de usuario
    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    // Estado del catálogo
    private val _catalogUiState = MutableStateFlow(CatalogUiState())
    val catalogUiState: StateFlow<CatalogUiState> = _catalogUiState.asStateFlow()

    // Estado del carrito
    private val _cartUiState = MutableStateFlow(CartUiState())
    val cartUiState: StateFlow<CartUiState> = _cartUiState.asStateFlow()

    // Estado del detalle de producto
    private val _productDetailUiState = MutableStateFlow(ProductDetailUiState())
    val productDetailUiState: StateFlow<ProductDetailUiState> = _productDetailUiState.asStateFlow()

    //Estado para categorias
    private val _categoryManagementUiState = MutableStateFlow(CategoryManagementUiState())
    val categoryManagementUiState: StateFlow<CategoryManagementUiState> = _categoryManagementUiState.asStateFlow()

    private val _categoryEditUiState = MutableStateFlow(CategoryEditUiState())
    val categoryEditUiState: StateFlow<CategoryEditUiState> = _categoryEditUiState.asStateFlow()

    private val _categoryCreateUiState = MutableStateFlow(CategoryCreateUiState())
    val categoryCreateUiState: StateFlow<CategoryCreateUiState> = _categoryCreateUiState.asStateFlow()


    // Estados para formularios de login y registro
    var uiStateLogin by mutableStateOf(LoginUiState())
        private set
    var uiStateRegister by mutableStateOf(RegisterUiState())
        private set

    // Estado para notificaciones
    val snackbarHostState = SnackbarHostState()

    // Flag para controlar la carga inicial única
    private var isDataLoaded = false

    // --- 4. BLOQUE DE INICIALIZACIÓN ---

    init {
        // Llama a las funciones de configuración inicial.
        loadInitialData()
        observeAndFilterProducts()
    }

    // --- 5. LÓGICA DE CATÁLOGO Y PRODUCTOS ---

    /**
     * Carga los datos iniciales (categorías y la primera lista de productos) una sola vez.
     * Usa un flag 'isDataLoaded' para evitar recargas innecesarias (ej. al rotar la pantalla).
     */
    private fun loadInitialData() {
        // Evita la recarga si los datos ya se han cargado.
        if (isDataLoaded) return

        viewModelScope.launch {
            _catalogUiState.update { it.copy(isLoading = true) }
            try {
                // Ejecuta ambas cargas en paralelo y espera a que terminen.
                coroutineScope {
                    val categoriesDeferred = async { categoryRepository.getAllCategories().first() }
                    val productsDeferred = async { productRepository.getAllProducts().first() }

                    val categories = categoriesDeferred.await()
                    val products = productsDeferred.await()

                    _catalogUiState.update {
                        it.copy(
                            categories = categories,
                            products = products, // Carga la lista completa inicial
                            isLoading = false
                        )
                    }
                }
                // Marca los datos como cargados para evitar futuras ejecuciones.
                isDataLoaded = true

            } catch (e: Exception) {
                _catalogUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun observeAndFilterProducts() {
        viewModelScope.launch {
            combine(
                productRepository.getAllProducts(), // Fuente de verdad para los productos
                _catalogUiState.map { it.searchQuery }.distinctUntilChanged(),
                _catalogUiState.map { it.selectedCategoryId }.distinctUntilChanged()
            ) { products, query, categoryId ->
                // Filtra los productos en memoria según los criterios actuales.
                products.filter { product ->
                    val matchesCategory = categoryId == null || product.categoryId == categoryId
                    val matchesSearch = query.isBlank() || product.name.contains(query, ignoreCase = true)
                    matchesCategory && matchesSearch
                }
            }.catch {
                // En caso de error en el flujo, actualiza el estado y emite una lista vacía.
                _catalogUiState.update { it.copy(isLoading = false) }
                emit(emptyList())
            }.collect { filteredProducts ->
                // Actualiza el estado de la UI solo con la lista de productos filtrada.
                _catalogUiState.update {
                    it.copy(
                        products = filteredProducts,
                        isLoading = false // Apaga el loading tras el filtro/actualización.
                    )
                }
            }
        }
    }

    fun loadManagedCategories() {
        viewModelScope.launch {
            // 1. Pone el estado en 'cargando'
            _categoryManagementUiState.update { it.copy(isLoading = true) }
            try {
                // 2. Escucha el flujo de datos del repositorio
                categoryRepository.getAllCategories().collect { categoryEntities ->
                    // 3. Mapea las entidades de BD (CategoryEntity) al modelo de UI (Category)
                    val categoriesModel = categoryEntities.map { entity ->
                        Category(id = entity.id, name = entity.name)
                    }
                    // 4. Actualiza el estado con la nueva lista y desactiva 'cargando'
                    _categoryManagementUiState.update {
                        it.copy(categories = categoriesModel, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                // En caso de error, simplemente desactiva 'cargando'
                _categoryManagementUiState.update { it.copy(isLoading = false) }
                // Aquí podrías también enviar un mensaje de error a la UI
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _catalogUiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(categoryId: Long?) {
        _catalogUiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun getProductById(productId: Long) {
        viewModelScope.launch {
            _productDetailUiState.value = ProductDetailUiState(isLoading = true)
            try {
                val product = productRepository.getProductById(productId)
                _productDetailUiState.value = ProductDetailUiState(product = product)
            } catch (e: Exception) {
                _productDetailUiState.value = ProductDetailUiState(error = "Error al cargar el producto.")
            }
        }
    }

    //Logica para el cambio de categorias
    fun loadCategoryForEdit(categoryId: Long) {
        viewModelScope.launch {
            _categoryEditUiState.value = CategoryEditUiState(isLoading = true) // Resetea y muestra carga
            try {
                val categoryEntity = categoryRepository.getCategoryById(categoryId) // Asume que esta función existe en tu repo
                if (categoryEntity != null) {
                    val categoryModel = Category(id = categoryEntity.id, name = categoryEntity.name)
                    _categoryEditUiState.update {
                        it.copy(
                            isLoading = false,
                            category = categoryModel,
                            categoryName = categoryModel.name // Rellena el campo de texto
                        )
                    }
                } else {
                    // La categoría no se encontró
                    _categoryEditUiState.update {
                        it.copy(isLoading = false, errorMessage = "Categoría no encontrada.")
                    }
                }
            } catch (e: Exception) {
                _categoryEditUiState.update {
                    it.copy(isLoading = false, errorMessage = "Error al cargar la categoría.")
                }
            }
        }
    }

    fun onCategoryNameChange(newName: String) {
        _categoryEditUiState.update {
            it.copy(
                categoryName = newName,
                // Habilita el guardado solo si el nombre no está vacío y es diferente al original
                canBeSaved = newName.isNotBlank() && newName != it.category?.name
            )
        }
    }

    fun saveCategoryChanges() {
        viewModelScope.launch {
            val currentState = _categoryEditUiState.value

            // 1. Validar que realmente hay una categoría cargada
            if (currentState.category == null) {
                _categoryEditUiState.update { it.copy(errorMessage = "No se ha cargado ninguna categoría para guardar.") }
                return@launch
            }

            // 2. Poner el estado en "guardando"
            _categoryEditUiState.update { it.copy(isSaving = true) }

            try {
                // 3. Crear la entidad actualizada para enviar al repositorio
                val updatedCategoryEntity = CategoryEntity(
                    id = currentState.category.id,
                    name = currentState.categoryName // Usa el nombre modificado del campo de texto
                )

                // 4. Llamar a la función del repositorio que nos indicaste
                categoryRepository.updateCategory(updatedCategoryEntity)

                // 5. Actualizar el estado para indicar éxito
                _categoryEditUiState.update { it.copy(isSaving = false, saveSuccess = true) }

            } catch (e: Exception) {
                // 6. En caso de error, actualizar el estado con un mensaje
                _categoryEditUiState.update {
                    it.copy(isSaving = false, errorMessage = "Error al guardar los cambios.")
                }
            }
        }
    }

    //logica para nuevas categorias

    fun onNewCategoryNameChange(newName: String) {
        _categoryCreateUiState.update {
            it.copy(
                categoryName = newName,
                // Habilita el botón de crear solo si el campo no está vacío.
                canBeCreated = newName.isNotBlank()
            )
        }
    }

    fun createNewCategory() {
        viewModelScope.launch {
            val currentState = _categoryCreateUiState.value

            // Poner el estado en "creando"
            _categoryCreateUiState.update { it.copy(isCreating = true) }

            try {
                // Crear la nueva entidad de categoría. El ID se genera automáticamente.
                val newCategoryEntity = CategoryEntity(name = currentState.categoryName)

                // Llamar a la función del repositorio para insertar.
                categoryRepository.insertCategory(newCategoryEntity)

                // Actualizar el estado para indicar éxito.
                _categoryCreateUiState.update { it.copy(isCreating = false, createSuccess = true) }

            } catch (e: Exception) {
                // En caso de error, actualizar el estado con un mensaje.
                _categoryCreateUiState.update {
                    it.copy(isCreating = false, errorMessage = "Error al crear la categoría.")
                }
            }
        }
    }

    fun clearEditCategoryState() {
        _categoryEditUiState.value = CategoryEditUiState()
    }

    fun clearCreateCategoryState() {
        _categoryCreateUiState.value = CategoryCreateUiState()
    }
    // --- 6. LÓGICA DEL CARRITO ---

    fun addToCart(product: ProductEntity) {
        viewModelScope.launch {
            _cartUiState.update { currentState ->
                val existingItem = currentState.items.find { it.productId == product.id.toInt() }
                val newItems = if (existingItem != null) {
                    currentState.items.map { item ->
                        if (item.productId == product.id.toInt()) item.copy(quantity = item.quantity + 1) else item
                    }
                } else {
                    currentState.items + CartItem(
                        productId = product.id.toInt(),
                        name = product.name,
                        price = product.price,
                        quantity = 1
                    )
                }
                val newTotal = newItems.sumOf { it.price * it.quantity }
                currentState.copy(items = newItems, total = newTotal)
            }
            snackbarHostState.showSnackbar(
                message = "${product.name} fue añadido al carrito.",
                duration = SnackbarDuration.Short
            )
        }
    }

    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            var removedItemName = ""
            _cartUiState.update { currentState ->
                removedItemName = currentState.items.find { it.productId == productId }?.name ?: "Producto"
                val newItems = currentState.items.filterNot { it.productId == productId }
                val newTotal = newItems.sumOf { it.price * it.quantity }
                currentState.copy(items = newItems, total = newTotal)
            }
            snackbarHostState.showSnackbar(
                message = "$removedItemName removido del carrito",
                duration = SnackbarDuration.Short
            )
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            if (_cartUiState.value.items.isNotEmpty()) {
                _cartUiState.value = CartUiState()
                snackbarHostState.showSnackbar(
                    message = "El carrito ha sido vaciado",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    fun increaseCartItemQuantity(productId: Int) {
        _cartUiState.update { currentState ->
            val newItems = currentState.items.map { item ->
                if (item.productId == productId) {
                    item.copy(quantity = item.quantity + 1)
                } else {
                    item
                }
            }
            val newTotal = newItems.sumOf { it.price * it.quantity }
            currentState.copy(items = newItems, total = newTotal)
        }
    }

    fun decreaseCartItemQuantity(productId: Int) {
        _cartUiState.update { currentState ->
            val itemToDecrease = currentState.items.find { it.productId == productId }
            val newItems = if (itemToDecrease != null && itemToDecrease.quantity > 1) {
                currentState.items.map { item ->
                    if (item.productId == productId) {
                        item.copy(quantity = item.quantity - 1)
                    } else {
                        item
                    }
                }
            } else {
                currentState.items.filterNot { it.productId == productId }
            }
            val newTotal = newItems.sumOf { it.price * it.quantity }
            currentState.copy(items = newItems, total = newTotal)
        }
    }

    // --- 7. LÓGICA DE AUTENTICACIÓN Y USUARIO ---

    // Login
    fun loginUser(onSuccess: (route: String) -> Unit) {
        viewModelScope.launch {
            uiStateLogin = uiStateLogin.copy(isSubmitting = true, errorMsg = null)
            try {
                val user = userRepository.getUserByEmail(uiStateLogin.email)
                if (user != null && user.password == uiStateLogin.pass) {
                    val isAdmin = user.rolId == 1L

                    _uiState.update { it.copy(currentUser = user, isAdmin = isAdmin) }
                    _userState.update { it.copy(username = user.names, email = user.email) }

                    uiStateLogin = uiStateLogin.copy(success = true,
                        errorMsg = if (isAdmin) "vendedor" else "")
                    val destinationRoute = if (isAdmin) "admin_home" else "catalog"
                    onSuccess(destinationRoute)

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

    // Registro
    fun registerUser(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val usernameError = Validation.validateUsername(uiStateRegister.username)
            val emailError = Validation.validateEmail(uiStateRegister.email)
            val passError = Validation.validatePassword(uiStateRegister.pass)
            val confirmPassError = Validation.validateConfirmPassword(uiStateRegister.pass, uiStateRegister.confirmPass)

            uiStateRegister = uiStateRegister.copy(
                usernameError = usernameError, emailError = emailError,
                passError = passError, confirmPassError = confirmPassError
            )

            if (listOf(usernameError, emailError, passError, confirmPassError).any { it != null }) return@launch

            uiStateRegister = uiStateRegister.copy(isSubmitting = true, errorMsg = null)
            try {
                if (userRepository.getUserByEmail(uiStateRegister.email) != null) {
                    uiStateRegister = uiStateRegister.copy(emailError = "El correo ya está registrado.", isSubmitting = false)
                    return@launch
                }

                val newUser = UserEntity(
                    names = uiStateRegister.username,
                    lastNames = "",
                    email = uiStateRegister.email,
                    password = uiStateRegister.pass,
                    status = true,
                    rolId = 2 // Cliente
                )
                userRepository.insertUser(newUser)
                uiStateRegister = uiStateRegister.copy(success = true, isSubmitting = false)
                onSuccess()
            } catch (e: Exception) {
                uiStateRegister = uiStateRegister.copy(errorMsg = "Error en el registro: ${e.message}", isSubmitting = false)
            }
        }
    }

    // --- Métodos de actualización de formularios (Login y Registro) ---

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
            passError = if (pass.isBlank()) "La contraseña es requerida" else null,
            canSubmit = validateLoginForm(uiStateLogin.email, pass)
        )
    }

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

    fun clearLoginState() {
        uiStateLogin = LoginUiState()
    }

    fun clearRegisterState() {
        uiStateRegister = RegisterUiState()
    }

    private fun validateLoginForm(email: String, pass: String): Boolean {
        return Validation.isLoginEmailValid(email) && Validation.isLoginPasswordValid(pass)
    }

    private fun validateRegisterForm() {
        val state = uiStateRegister
        uiStateRegister = uiStateRegister.copy(
            canSubmit = state.username.isNotBlank() && state.email.isNotBlank() &&
                    state.pass.isNotBlank() && state.confirmPass.isNotBlank()
        )
    }
}
