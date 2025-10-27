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

//Data Classes

// Estado general de la app (No se está usando activamente, pero disponible para uso futuro)
data class PokeShopUiState(
    val isLoading: Boolean = true,
    val productDetail: ProductEntity? = null,
    val currentUser: UserEntity? = null,
    val isAdmin: Boolean = false,
    val userMessage: String? = null
)

// Estado para la pantalla de Login
data class LoginUiState(
    val email: String = "", // setea el valor del campo email
    val pass: String = "",  // setea el valor del campo pass
    val emailError: String? = null, //mensaje de error para el campo email
    val passError: String? = null, //mensaje de error para el campo pass
    val isSubmitting: Boolean = false, // indica si el formulario se está enviando al servidor/repositorio
    val canSubmit: Boolean = false, // booleano que controla si el botón de login está habilitado
    val success: Boolean = false, // indica si el login fue exitoso
    val errorMsg: String? = null // mensaje de error general para mostrar en la pantalla
)

// Estado para la pantalla de Registro sigue la misma logica que el de Login
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

// Estado para la pantalla de Perfil
data class UserState(
    val username: String = "",
    val email: String = ""
)

// Estado para la pantalla del Catálogo de productos
data class CatalogUiState(
    val isLoading: Boolean = true, // Indica si se están cargando los productos o categorías.
    val products: List<ProductEntity> = emptyList(), // La lista de productos a mostrar.
    val categories: List<CategoryEntity> = emptyList(), // La lista completa de categorías disponibles para filtrar.
    val searchQuery: String = "", // El texto actual en la barra de búsqueda.
    val selectedCategoryId: Long? = null // El ID de la categoría seleccionada para el filtro.
)

// Estado para el carrito de compras
// Modelo de datos para un elemento en el carrito
data class CartItem(
    val productId: Int,
    val name: String,
    val price: Double,
    var quantity: Int,
)

//Estado para el carrito de compras
data class CartUiState(
    val items: List<CartItem> = emptyList(), // Lista de productos en el carrito
    val total: Double = 0.0 //valor total de los productos
)

// Estado para la pantalla de Detalle de un producto
data class ProductDetailUiState(
    val isLoading: Boolean = false, // Indica si el producto se está cargando.
    val product: ProductEntity? = null, // Puede ser el producto cargado  o un nulo lo cual resultaria en error
    val error: String? = null // Mensaje de error si la carga falla.
)

// Simula entidad categoria
data class Category(
    val id: Long,
    val name: String
)

// Estado para la pantalla de Gestión de Categorías
data class CategoryManagementUiState(
    val isLoading: Boolean = true, // Indica si se están cargando las categorías.
    val categories: List<Category> = emptyList() // La lista de categorías a mostrar.
)

// Estado para la pantalla de Edición de una Categoría
data class CategoryEditUiState(
    val isLoading: Boolean = true, // Indica si se está cargando la categoría.
    val category: Category? = null, // Puede ser la categoría cargada o un nulo lo cual resultaria en error
    val categoryName: String = "", // Nombre de la categoría
    val nameError: String? = null, // Mensaje de error para el nombre de la categoría
    val canBeSaved: Boolean = false, // Indica si el nombre de la categoría es válido para guardar
    val isSaving: Boolean = false, // Indica si se está guardando la categoría
    val saveSuccess: Boolean = false, // Indica si la categoría se guardó correctamente
    val errorMessage: String? = null // Mensaje de error si la categoría no se puede guardar
)

// Estado para la pantalla de Creación de una Categoría
data class CategoryCreateUiState(
    val categoryName: String = "", // Nombre de la categoría
    val nameError: String? = null, // Mensaje de error para el nombre de la categoría
    val canBeCreated: Boolean = false, // Indica si el nombre de la categoría es válido para crear
    val isCreating: Boolean = false, // Indica si se está creando la categoría
    val createSuccess: Boolean = false, // Indica si la categoría se creó correctamente
    val errorMessage: String? = null // Mensaje de error si la categoría no se puede crear
)

class PokeShopViewModel(
    private val userRepository: UserRepository,
    private val rolRepository: RolRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val saleRepository: SaleRepository,
    private val saleDetailRepository: SaleDetailRepository
) : ViewModel() {

    //Estados

    // Estado global de la app (No se está usando activamente, pero disponible para uso futuro)
    private val _uiState = MutableStateFlow(PokeShopUiState())

    // Estado del perfil de usuario, expuesto públicamente como StateFlow de solo lectura.
    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    // Estado del catálogo, expuesto públicamente.
    private val _catalogUiState = MutableStateFlow(CatalogUiState())
    val catalogUiState: StateFlow<CatalogUiState> = _catalogUiState.asStateFlow()

    // Estado del carrito, expuesto públicamente.
    private val _cartUiState = MutableStateFlow(CartUiState())
    val cartUiState: StateFlow<CartUiState> = _cartUiState.asStateFlow()

    // Estado del detalle de producto, expuesto públicamente.
    private val _productDetailUiState = MutableStateFlow(ProductDetailUiState())
    val productDetailUiState: StateFlow<ProductDetailUiState> = _productDetailUiState.asStateFlow()

    // Estados para la gestión de categorías, expuestos públicamente.
    private val _categoryManagementUiState = MutableStateFlow(CategoryManagementUiState())
    val categoryManagementUiState: StateFlow<CategoryManagementUiState> = _categoryManagementUiState.asStateFlow()

    private val _categoryEditUiState = MutableStateFlow(CategoryEditUiState())
    val categoryEditUiState: StateFlow<CategoryEditUiState> = _categoryEditUiState.asStateFlow()

    private val _categoryCreateUiState = MutableStateFlow(CategoryCreateUiState())
    val categoryCreateUiState: StateFlow<CategoryCreateUiState> = _categoryCreateUiState.asStateFlow()


    // Estados para formularios de login y registro (usando `mutableStateOf` para cambios frecuentes).
    var uiStateLogin by mutableStateOf(LoginUiState())
        private set
    var uiStateRegister by mutableStateOf(RegisterUiState())
        private set

    // Estado para controlar y mostrar notificaciones (Snackbars) en la UI.
    val snackbarHostState = SnackbarHostState()

    // Flag para asegurar que la carga de datos pesados se ejecute una sola vez.
    private var isDataLoaded = false

    //Inizialización
    init {
        // Llama a las funciones de configuración inicial al crear el ViewModel.
        loadInitialData()
        observeAndFilterProducts()
    }

    //Logica de catalogo y productos
    // Carga inicial de datos, como productos y categorías.
    private fun loadInitialData() {
        if (isDataLoaded) return
        // Inicia una corrutina en el ámbito del ViewModel
        viewModelScope.launch {
            // Actualiza el estado de carga
            _catalogUiState.update { it.copy(isLoading = true) }
            try {
                // Ejecuta ambas cargas en paralelo para mejorar el rendimiento.
                coroutineScope {
                    val categoriesDeferred = async { categoryRepository.getAllCategories().first() }
                    val productsDeferred = async { productRepository.getAllProducts().first() }
                    // Actualiza el estado del catálogo con los resultados.
                    _catalogUiState.update {
                        it.copy(
                            categories = categoriesDeferred.await(), //carga la lista inicial de categorias
                            products = productsDeferred.await(), //carga la lista inicial de productos
                            isLoading = false
                        )
                    }
                }
                isDataLoaded = true // Marca los datos como cargados.

            } catch (e: Exception) {
                // En caso de error, actualiza el estado de carga.
                _catalogUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Carga los productos y filtra según la categoría seleccionada.
    private fun observeAndFilterProducts() {
        viewModelScope.launch {
            combine( // Combina varios flujos en uno solo.
                productRepository.getAllProducts(), // Obtiene la lista de productos.
                _catalogUiState.map { it.searchQuery }.distinctUntilChanged(), //Permite el filtro de búsqueda.
                _catalogUiState.map { it.selectedCategoryId }.distinctUntilChanged() //Permite el filtro de categoría.
            ) { products, query, categoryId ->
                // Filtra la lista en memoria.
                products.filter { product ->
                    val matchesCategory = categoryId == null || product.categoryId == categoryId
                    val matchesSearch = query.isBlank() || product.name.contains(query, ignoreCase = true)
                    matchesCategory && matchesSearch
                }
            }.catch {
                _catalogUiState.update { it.copy(isLoading = false) }
                emit(emptyList()) // En caso de error, emite una lista vacía.
            }.collect { filteredProducts ->
                // Actualiza la UI solo con los productos filtrados.
                _catalogUiState.update {
                    it.copy(
                        products = filteredProducts,
                        isLoading = false
                    )
                }
            }
        }
    }

    //Actualiza el texto de busqueda en el estado del catalogo
    fun onSearchQueryChanged(query: String) {
        _catalogUiState.update { it.copy(searchQuery = query) }
    }

    //Actualiza la categoria seleccionada en el estado del catalogo
    fun onCategorySelected(categoryId: Long?) {
        _catalogUiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    //Obtiene un producto segun su id y actualiza el estado de productDetailUiState
    fun getProductById(productId: Long) {
        viewModelScope.launch {
            _productDetailUiState.value = ProductDetailUiState(isLoading = true) //actualiza el estado de carga
            try {
                val product = productRepository.getProductById(productId) //guarda el producto en una variable
                _productDetailUiState.value = ProductDetailUiState(product = product) //actualiza el estado con el producto
            } catch (e: Exception) {
                _productDetailUiState.value = ProductDetailUiState(error = "Error al cargar el producto.") //mensaje de error en caso de falla
            }
        }
    }

    //Crea un nuevo producto
    fun addProduct(name: String, description: String, price: Double, stock: Int, categoryId: Long) {
        if (name.isBlank() || description.isBlank() || price <= 0 || stock < 0 || categoryId <= 0) return // Valida los datos antes de crear el producto
        // Crea un nuevo producto en la base de datos
        viewModelScope.launch {
            val newProduct = ProductEntity(
                name = name,
                description = description,
                price = price,
                stock = stock,
                categoryId = categoryId,
                status = true // Por defecto, el producto está activo
            )
            productRepository.insertProduct(newProduct)
        }
    }

    //gestion de categorias

    // Carga las categorías desde la base de datos y las actualiza en el estado.
    fun loadManagedCategories() {
        viewModelScope.launch {
            _categoryManagementUiState.update { it.copy(isLoading = true) }
            try {
                // Obtiene las categorías desde la base de datos y las convierte a Category.
                categoryRepository.getAllCategories().collect { categoryEntities ->
                    val categoriesModel = categoryEntities.map { Category(id = it.id, name = it.name) }
                    // Actualiza el estado con las categorías cargadas.
                    _categoryManagementUiState.update {
                        it.copy(categories = categoriesModel, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                // En caso de error, actualiza el estado de carga.
                _categoryManagementUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Carga una categoría específica para su edición.
    fun loadCategoryForEdit(categoryId: Long) {
        viewModelScope.launch {
            _categoryEditUiState.value = CategoryEditUiState(isLoading = true)
            try {
                // Obtiene la categoría desde la base de datos y la convierte a CategoryModel.
                val categoryEntity = categoryRepository.getCategoryById(categoryId)
                // Actualiza el estado con la categoría cargada.
                if (categoryEntity != null) {
                    // Convierte la entidad a un modelo
                    val categoryModel = Category(id = categoryEntity.id, name = categoryEntity.name)
                    _categoryEditUiState.update {
                        it.copy(isLoading = false, category = categoryModel, categoryName = categoryModel.name)
                    }
                } else {
                    // En caso de error, actualiza el estado de carga.
                    _categoryEditUiState.update { it.copy(isLoading = false, errorMessage = "Categoría no encontrada.") }
                }
            } catch (e: Exception) {
                // En caso de error, actualiza el estado de carga.
                _categoryEditUiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar la categoría.") }
            }
        }
    }

    //Actualiza el nombre de la categoria
    fun onCategoryNameChange(newName: String) {
        _categoryEditUiState.update {
            it.copy(
                categoryName = newName,
                canBeSaved = newName.isNotBlank() && newName != it.category?.name
            )
        }
    }

    //Actualiza una categoria
    fun saveCategoryChanges() {
        viewModelScope.launch {
            // Obtiene la categoría actual
            val currentState = _categoryEditUiState.value
            // Valida que haya una categoría cargada
            if (currentState.category == null) {
                _categoryEditUiState.update { it.copy(errorMessage = "No hay categoría cargada.") }
                return@launch
            }
            _categoryEditUiState.update { it.copy(isSaving = true) }
            try {
                // Actualiza la categoría en la base de datos
                val updatedCategoryEntity = CategoryEntity(id = currentState.category.id, name = currentState.categoryName)
                categoryRepository.updateCategory(updatedCategoryEntity)
                _categoryEditUiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                // En caso de error, actualiza el estado de carga.
                _categoryEditUiState.update { it.copy(isSaving = false, errorMessage = "Error al guardar.") }
            }
        }
    }

    //Actualiza el nombre de la categoria
    fun onNewCategoryNameChange(newName: String) {
        _categoryCreateUiState.update { it.copy(categoryName = newName, canBeCreated = newName.isNotBlank()) }
    }

    //Crea una nueva categoria
    fun createNewCategory() {
        viewModelScope.launch {
            // Obtiene el nombre de la categoría
            val currentState = _categoryCreateUiState.value
            _categoryCreateUiState.update { it.copy(isCreating = true) }
            try {
                // Inserta la nueva categoría en la base de datos
                val newCategoryEntity = CategoryEntity(name = currentState.categoryName)
                categoryRepository.insertCategory(newCategoryEntity)
                _categoryCreateUiState.update { it.copy(isCreating = false, createSuccess = true) }
            } catch (e: Exception) {
                // En caso de error, actualiza el estado de carga.
                _categoryCreateUiState.update { it.copy(isCreating = false, errorMessage = "Error al crear la categoría.") }
            }
        }
    }

    //Limpia el estado de la pantalla de edición de categoría.
    fun clearEditCategoryState() {
        _categoryEditUiState.value = CategoryEditUiState()
    }

    //Limpia el estado de la pantalla de creación de categoría.
    fun clearCreateCategoryState() {
        _categoryCreateUiState.value = CategoryCreateUiState()
    }

    //logica del carrito

    // Agrega un producto al carrito
    fun addToCart(product: ProductEntity) {
        viewModelScope.launch {
            _cartUiState.update { currentState ->
                // Busca si ya existe el producto en el carrito
                val existingItem = currentState.items.find { it.productId == product.id.toInt() }
                val newItems = if (existingItem != null) {
                    // Si existe, actualiza la cantidad
                    currentState.items.map { if (it.productId == product.id.toInt()) it.copy(quantity = it.quantity + 1) else it }
                } else {
                    // Si no existe, lo añade a la lista
                    currentState.items + CartItem(productId = product.id.toInt(), name = product.name, price = product.price, quantity = 1)
                }
                // Calcula el nuevo total
                val newTotal = newItems.sumOf { it.price * it.quantity }
                currentState.copy(items = newItems, total = newTotal)
            }
            // Muestra una notificación
            snackbarHostState.showSnackbar(
                message = "${product.name} fue añadido al carrito.",
                duration = SnackbarDuration.Short
            )
        }
    }

    //Elimina un producto del carrito
    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            // Busca el producto en el carrito
            var removedItemName = "" // Nombre del producto que se eliminará
            _cartUiState.update { currentState ->
                removedItemName = currentState.items.find { it.productId == productId }?.name ?: "Producto" // Nombre del producto que se eliminará
                val newItems = currentState.items.filterNot { it.productId == productId } // Lista de productos actualizada
                val newTotal = newItems.sumOf { it.price * it.quantity } // Nuevo total
                currentState.copy(items = newItems, total = newTotal)
            }
            // Muestra una notificación
            snackbarHostState.showSnackbar(
                message = "$removedItemName removido del carrito",
                duration = SnackbarDuration.Short
            )
        }
    }

    //Elimina todos los productos del carrito
    fun clearCart() {
        viewModelScope.launch {
            if (_cartUiState.value.items.isNotEmpty()) {
                _cartUiState.value = CartUiState() // Resetea al estado inicial
            }
        }
    }

    //Actualiza la cantidad de un producto en el carrito
    fun increaseCartItemQuantity(productId: Int) {
        _cartUiState.update { currentState ->
            val newItems = currentState.items.map { item ->
                // Actualiza la cantidad del producto actual+1
                if (item.productId == productId) item.copy(quantity = item.quantity + 1) else item
            }
            // Calcula el nuevo total
            val newTotal = newItems.sumOf { it.price * it.quantity }
            currentState.copy(items = newItems, total = newTotal)
        }
    }

    //Actualiza la cantidad de un producto en el carrito
    fun decreaseCartItemQuantity(productId: Int) {
        _cartUiState.update { currentState ->
            // Busca el producto en el carrito
            val itemToDecrease = currentState.items.find { it.productId == productId }
            val newItems = if (itemToDecrease != null && itemToDecrease.quantity > 1) {
                currentState.items.map { item ->
                    // Actualiza la cantidad del producto actual-1
                    if (item.productId == productId) item.copy(quantity = item.quantity - 1) else item
                }
            } else {
                currentState.items.filterNot { it.productId == productId }
            }
            // Calcula el nuevo total
            val newTotal = newItems.sumOf { it.price * it.quantity }
            currentState.copy(items = newItems, total = newTotal)
        }
    }

    //logica de autenticacion de usuarios
    fun loginUser(onSuccess: (route: String) -> Unit) {
        viewModelScope.launch {
            // Actualiza el estado de carga
            uiStateLogin = uiStateLogin.copy(isSubmitting = true, errorMsg = null)
            try {
                // Intenta autenticar al usuario
                val user = userRepository.getUserByEmail(uiStateLogin.email)
                if (user != null && user.password == uiStateLogin.pass) {
                    val isAdmin = user.rolId == 1L // Asumiendo que 1 es rol de Admin creamos esta variable
                    // Actualiza el estado con la información del usuario
                    _uiState.update { it.copy(currentUser = user, isAdmin = isAdmin) }
                    _userState.update { it.copy(username = user.names, email = user.email) }
                    // Navega a la pantalla correspondiente
                    val destinationRoute = if (isAdmin) "admin_home" else "catalog"
                    onSuccess(destinationRoute)
                } else {
                    // En caso de error, actualiza el estado
                    uiStateLogin = uiStateLogin.copy(errorMsg = "Credenciales inválidas", isSubmitting = false)
                }
            } catch (e: Exception) {
                // En caso de error, actualiza el estado
                uiStateLogin = uiStateLogin.copy(errorMsg = "Error: ${e.message}", isSubmitting = false)
            }
        }
    }

    //Crea un nuevo usuario
    fun registerUser(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Valida los datos del formulario
            val usernameError = Validation.validateUsername(uiStateRegister.username)
            val emailError = Validation.validateEmail(uiStateRegister.email)
            val passError = Validation.validatePassword(uiStateRegister.pass)
            val confirmPassError = Validation.validateConfirmPassword(uiStateRegister.pass, uiStateRegister.confirmPass)

            // Actualiza el estado
            uiStateRegister = uiStateRegister.copy(usernameError = usernameError, emailError = emailError, passError = passError, confirmPassError = confirmPassError)
            if (listOf(usernameError, emailError, passError, confirmPassError).any { it != null }) return@launch // Si hay errores, no continua
            uiStateRegister = uiStateRegister.copy(isSubmitting = true, errorMsg = null)
            try {
                // Verifica si el correo ya está registrado
                if (userRepository.getUserByEmail(uiStateRegister.email) != null) {
                    uiStateRegister = uiStateRegister.copy(emailError = "El correo ya está registrado.", isSubmitting = false)
                    return@launch
                }
                // Crea el nuevo usuario
                val newUser = UserEntity(names = uiStateRegister.username, lastNames = "", email = uiStateRegister.email, password = uiStateRegister.pass, status = true, rolId = 2) // rolId 2 para cliente
                userRepository.insertUser(newUser)
                uiStateRegister = uiStateRegister.copy(success = true, isSubmitting = false)
                onSuccess()
            } catch (e: Exception) {
                uiStateRegister = uiStateRegister.copy(errorMsg = "Error en el registro: ${e.message}", isSubmitting = false)
            }
        }
    }

    //Actualiza el estado del formulario de login especificamente el email
    fun updateLoginEmail(email: String) {
        uiStateLogin = uiStateLogin.copy(email = email, emailError = Validation.validateEmail(email), canSubmit = validateLoginForm())
    }

    //Actualiza el estado del formulario de login especificamente la contraseña
    fun updateLoginPassword(pass: String) {
        uiStateLogin = uiStateLogin.copy(pass = pass, passError = Validation.validatePassword(pass), canSubmit = validateLoginForm())
    }

    //Actualiza el estado del formulario de registro especificamente el nombre de usuario
    fun updateRegisterUsername(username: String) {
        uiStateRegister = uiStateRegister.copy(username = username, usernameError = Validation.validateUsername(username), canSubmit = validateRegisterForm())
    }

    //Actualiza el estado del formulario de registro especificamente el email
    fun updateRegisterEmail(email: String) {
        uiStateRegister = uiStateRegister.copy(email = email, emailError = Validation.validateEmail(email), canSubmit = validateRegisterForm())
    }

    //Actualiza el estado del formulario de registro especificamente la contraseña
    fun updateRegisterPassword(pass: String) {
        uiStateRegister = uiStateRegister.copy(pass = pass, passError = Validation.validatePassword(pass), canSubmit = validateRegisterForm())
    }

    //Actualiza el estado del formulario de registro especificamente la confirmacion de la contraseña
    fun updateRegisterConfirmPassword(confirmPass: String) {
        uiStateRegister = uiStateRegister.copy(confirmPass = confirmPass, confirmPassError = Validation.validateConfirmPassword(uiStateRegister.pass, confirmPass), canSubmit = validateRegisterForm())
    }

    //Limpia el estado del formulario de login
    fun clearLoginState() {
        uiStateLogin = LoginUiState()
    }

    //Limpia el estado del formulario de registro
    fun clearRegisterState() {
        uiStateRegister = RegisterUiState()
    }

    //Valida el formulario de login
    private fun validateLoginForm(): Boolean {
        val state = uiStateLogin
        return state.emailError == null && state.passError == null && state.email.isNotBlank() && state.pass.isNotBlank()
    }

    //Valida el formulario de registro
    private fun validateRegisterForm(): Boolean {
        val state = uiStateRegister
        return state.usernameError == null && state.emailError == null && state.passError == null && state.confirmPassError == null &&
                state.username.isNotBlank() && state.email.isNotBlank() && state.pass.isNotBlank() && state.confirmPass.isNotBlank()
    }
}
