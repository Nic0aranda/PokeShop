package com.example.pokeshop.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeshop.data.entities.*
import com.example.pokeshop.data.repository.*
import com.example.pokeshop.domain.validation.Validation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// --- UI STATES (Data Classes) ---

data class PokeShopUiState(
    val isLoading: Boolean = true,
    val productDetail: ProductEntity? = null,
    val currentUser: UserEntity? = null, // Currently logged in user
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

// Cart Model (In-memory)
data class CartItem(
    val productId: Int,
    val name: String,
    val price: Double,
    var quantity: Int,
    val stock: Int // Needed for max quantity validation
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

// Simplified Category Model for UI
data class Category(val id: Long, val name: String)

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

data class ProductEditUiState(
    val isLoading: Boolean = true,
    val product: ProductEntity? = null,
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val stock: String = "",
    val category: CategoryEntity? = null,
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

// --- MAIN VIEWMODEL ---

class PokeShopViewModel(
    private val userRepository: UserRepository,
    private val rolRepository: RolRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val saleRepository: SaleRepository
) : ViewModel() {

    // --- Flujos de Estado ---
    private val _uiState = MutableStateFlow(PokeShopUiState())
    val uiState: StateFlow<PokeShopUiState> = _uiState.asStateFlow()

    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private val _catalogUiState = MutableStateFlow(CatalogUiState())
    val catalogUiState: StateFlow<CatalogUiState> = _catalogUiState.asStateFlow()

    private val _cartUiState = MutableStateFlow(CartUiState())
    val cartUiState: StateFlow<CartUiState> = _cartUiState.asStateFlow()

    private val _productDetailUiState = MutableStateFlow(ProductDetailUiState())
    val productDetailUiState: StateFlow<ProductDetailUiState> = _productDetailUiState.asStateFlow()

    private val _categoryManagementUiState = MutableStateFlow(CategoryManagementUiState())
    val categoryManagementUiState: StateFlow<CategoryManagementUiState> = _categoryManagementUiState.asStateFlow()

    private val _categoryEditUiState = MutableStateFlow(CategoryEditUiState())
    val categoryEditUiState: StateFlow<CategoryEditUiState> = _categoryEditUiState.asStateFlow()

    private val _categoryCreateUiState = MutableStateFlow(CategoryCreateUiState())
    val categoryCreateUiState: StateFlow<CategoryCreateUiState> = _categoryCreateUiState.asStateFlow()

    private val _productEditUiState = MutableStateFlow(ProductEditUiState())
    val productEditUiState: StateFlow<ProductEditUiState> = _productEditUiState.asStateFlow()

    // Form States
    var uiStateLogin by mutableStateOf(LoginUiState())
        private set
    var uiStateRegister by mutableStateOf(RegisterUiState())
        private set

    // Profile Editing State
    var isEditingProfile by mutableStateOf(false)
        private set
    var editUsername by mutableStateOf("")
        private set

    // Currency States
    var selectedCurrency by mutableStateOf("USD")
        private set
    var totalInClp by mutableDoubleStateOf(0.0)
        private set


    var isSavingProduct by mutableStateOf(false)
        private set
    val snackbarHostState = SnackbarHostState()
    private var isDataLoaded = false

    init {
        loadInitialData()
        observeAndFilterProducts()
    }

    // --- DATA LOADING ---

    fun loadInitialData() {
        if (isDataLoaded) return
        viewModelScope.launch {
            _catalogUiState.update { it.copy(isLoading = true) }
            try {
                coroutineScope {
                    val categoriesDeferred = async { categoryRepository.getAllCategories().first() }
                    val productsDeferred = async { productRepository.getAllProducts().first() }

                    val categories = categoriesDeferred.await()
                    val products = productsDeferred.await()

                    _catalogUiState.update {
                        it.copy(
                            categories = categories,
                            products = products,
                            isLoading = false
                        )
                    }
                }
                isDataLoaded = true
            } catch (e: Exception) {
                e.printStackTrace()
                _catalogUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun observeAndFilterProducts() {
        viewModelScope.launch {
            combine(
                productRepository.getAllProducts(),
                _catalogUiState.map { it.searchQuery }.distinctUntilChanged(),
                _catalogUiState.map { it.selectedCategoryId }.distinctUntilChanged()
            ) { products, query, categoryId ->
                products.filter { product ->
                    val matchesCategory = categoryId == null || product.category?.id == categoryId
                    val matchesSearch = query.isBlank() || product.name.contains(query, ignoreCase = true)
                    matchesCategory && matchesSearch
                }
            }.catch {
                emit(emptyList())
            }.collect { filteredProducts ->
                _catalogUiState.update {
                    it.copy(products = filteredProducts, isLoading = false)
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) { _catalogUiState.update { it.copy(searchQuery = query) } }
    fun onCategorySelected(categoryId: Long?) { _catalogUiState.update { it.copy(selectedCategoryId = categoryId) } }

    fun getProductById(productId: Long) {
        viewModelScope.launch {
            _productDetailUiState.value = ProductDetailUiState(isLoading = true)
            try {
                val product = productRepository.getProductById(productId)
                _productDetailUiState.value = ProductDetailUiState(product = product)
            } catch (e: Exception) {
                _productDetailUiState.value = ProductDetailUiState(error = "Error al cargar.")
            }
        }
    }

    // --- PRODUCT MANAGEMENT (Admin) ---
    fun addProduct(name: String, description: String, price: Double, stock: Int, categoryId: Long) {
        if (name.isBlank() || price <= 0 || stock < 0 || categoryId <= 0L) return

        viewModelScope.launch {
            val catObj = CategoryEntity(id = categoryId, name = "") // Temp object for ID linking

            val newProduct = ProductEntity(
                name = name, description = description, price = price, stock = stock, category = catObj, status = true
            )
            productRepository.insertProduct(newProduct)
            isDataLoaded = false
            loadInitialData()
        }
    }

    suspend fun Uri.toBase64String(context: Context): String? = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(this@toBase64String)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                // Usamos Base64.NO_WRAP para evitar saltos de línea
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun createProductAndUploadImage(
        context: Context,
        name: String, description: String, price: Double, stock: Int, categoryId: Long,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            isSavingProduct = true
            snackbarHostState.showSnackbar("Creando producto...")

            try {
                // 1. DTO y POST Producto (Obtener ID)
                val catObj = CategoryEntity(id = categoryId, name = "")
                val newProduct = ProductEntity(name = name, description = description, price = price, stock = stock, category = catObj, status = true)

                val newProductId = productRepository.insertProduct(newProduct)

                if (newProductId <= 0L) {
                    snackbarHostState.showSnackbar("Error al crear producto: API no devolvió ID.")
                    return@launch
                }

                // 2. Subir Imagen (si existe)
                if (imageUri != null) {
                    snackbarHostState.showSnackbar("Subiendo imagen...")

                    // Llama a la función de extensión externa (requiere importación en el archivo)
                    val base64String = imageUri.toBase64String(context)

                    if (!base64String.isNullOrEmpty()) {
                        val success = productRepository.uploadImages(newProductId, listOf(base64String))
                        if (!success) {
                            snackbarHostState.showSnackbar("Advertencia: Producto creado, pero falló la subida de imagen.")
                        }
                    }
                }

                // 3. Éxito Final y Recarga
                snackbarHostState.showSnackbar("Producto guardado con éxito!")
                isDataLoaded = false
                loadInitialData()

            } catch (e: Exception) {
                // Si el crash es síncrono (ej: DTO malformado), el stack trace se imprimirá en Logcat
                e.printStackTrace()
                snackbarHostState.showSnackbar("Error crítico al guardar: ${e.message}")
            } finally {
                isSavingProduct = false
            }
        }
    }

    fun loadProductForEdit(productId: Long) {
        viewModelScope.launch {
            Log.d("ViewModel", "Cargando producto para editar: $productId")
            _productEditUiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val product = productRepository.getProductById(productId)
                Log.d("ViewModel", "Producto recibido: ${product?.name}")

                if (product != null) {
                    _productEditUiState.update {
                        it.copy(
                            isLoading = false,
                            product = product,
                            name = product.name,
                            description = product.description ?: "", // Maneja null
                            price = product.price.toString(),
                            stock = product.stock.toString(),
                            category = product.category
                        )
                    }
                    Log.d("ViewModel", "Estado actualizado correctamente")
                } else {
                    Log.e("ViewModel", "Producto no encontrado: $productId")
                    _productEditUiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Producto no encontrado"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error cargando producto: ${e.message}")
                _productEditUiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }

    // 2. ACTUALIZACIÓN DE ESTADO DE CAMPOS DE TEXTO
    fun onEditProductDataChange(field: String, value: String) {
        _productEditUiState.update { currentState ->
            when (field) {
                "name" -> currentState.copy(name = value)
                "description" -> currentState.copy(description = value)
                "price" -> currentState.copy(price = value)
                "stock" -> currentState.copy(stock = value)
                else -> currentState
            }
        }
    }

    // 3. ACTUALIZACIÓN DE CATEGORÍA
    fun onEditProductCategoryChange(category: CategoryEntity) {
        _productEditUiState.update { it.copy(category = category) }
    }

    // 4. GUARDAR CAMBIOS (PUT /productos/{id})
    fun saveProductChanges() {
        viewModelScope.launch {
            val state = _productEditUiState.value
            val originalProduct = state.product ?: return@launch

            _productEditUiState.update { it.copy(isSaving = true, errorMessage = null) }

            try {
                // Se construyen los nuevos datos asegurando los tipos correctos (Double/Int)
                val updatedProduct = originalProduct.copy(
                    name = state.name,
                    description = state.description,
                    price = state.price.toDouble(),
                    stock = state.stock.toInt(),
                    category = state.category // Usamos el objeto CategoryEntity actualizado
                )

                val success = productRepository.updateProduct(updatedProduct)

                if (success) {
                    _productEditUiState.update { it.copy(isSaving = false, saveSuccess = true) }
                    // Recargar el catálogo principal para que el cambio sea visible
                    loadInitialData()
                } else {
                    _productEditUiState.update { it.copy(isSaving = false, errorMessage = "Error en el servidor al actualizar.") }
                }

            } catch (e: Exception) {
                _productEditUiState.update { it.copy(isSaving = false, errorMessage = "Datos inválidos: ${e.message}") }
            }
        }
    }

    // 5. BORRAR PRODUCTO (DELETE /productos/{id})
    fun deleteProduct(onDeletionSuccess: () -> Unit) {
        viewModelScope.launch {
            val productId = _productEditUiState.value.product?.id ?: return@launch

            try {
                val success = productRepository.deleteProduct(productId)

                if (success) {
                    onDeletionSuccess()
                    // Recargar el catálogo principal
                    loadInitialData()
                    snackbarHostState.showSnackbar("Producto eliminado correctamente.")
                } else {
                    snackbarHostState.showSnackbar("Error al eliminar del servidor.")
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Error: ${e.message}")
            }
        }
    }

    // --- CATEGORY MANAGEMENT (Admin) ---

    fun loadManagedCategories() {
        viewModelScope.launch {
            _categoryManagementUiState.update { it.copy(isLoading = true) }
            try {
                categoryRepository.getAllCategories().collect { categoryEntities ->
                    val categoriesModel = categoryEntities.map { Category(id = it.id, name = it.name) }
                    _categoryManagementUiState.update { it.copy(categories = categoriesModel, isLoading = false) }
                }
            } catch (e: Exception) {
                _categoryManagementUiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadCategoryForEdit(categoryId: Long) {
        viewModelScope.launch {
            _categoryEditUiState.update { it.copy(isLoading = true) }
            try {
                val categoryEntity = categoryRepository.getCategoryById(categoryId)
                if (categoryEntity != null) {
                    val categoryModel = Category(id = categoryEntity.id, name = categoryEntity.name)
                    _categoryEditUiState.update { it.copy(isLoading = false, category = categoryModel, categoryName = categoryModel.name) }
                } else {
                    _categoryEditUiState.update { it.copy(isLoading = false, errorMessage = "No encontrada.") }
                }
            } catch (e: Exception) {
                _categoryEditUiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar.") }
            }
        }
    }

    fun onCategoryNameChange(newName: String) {
        _categoryEditUiState.update { it.copy(categoryName = newName, canBeSaved = newName.isNotBlank()) }
    }

    fun saveCategoryChanges() {
        viewModelScope.launch {
            val currentState = _categoryEditUiState.value

            if (currentState.category == null) {
                _categoryEditUiState.update { it.copy(errorMessage = "No hay categoría cargada.") }
                return@launch
            }

            _categoryEditUiState.update { it.copy(isSaving = true, errorMessage = null) }

            try {
                val currentId = currentState.category!!.id

                val updatedCategoryEntity = CategoryEntity(
                    id = currentId,
                    name = currentState.categoryName
                )

                val success = categoryRepository.updateCategory(updatedCategoryEntity)

                if(success) {
                    _categoryEditUiState.update { it.copy(isSaving = false, saveSuccess = true) }
                    loadManagedCategories()
                } else {
                    _categoryEditUiState.update { it.copy(isSaving = false, errorMessage = "Error en API.") }
                }
            } catch (e: Exception) {
                _categoryEditUiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }

    fun onNewCategoryNameChange(newName: String) {
        _categoryCreateUiState.update { it.copy(categoryName = newName, canBeCreated = newName.isNotBlank()) }
    }

    fun createNewCategory() {
        viewModelScope.launch {
            val currentState = _categoryCreateUiState.value
            _categoryCreateUiState.update { it.copy(isCreating = true, errorMessage = null) }
            try {
                val newCategoryEntity = CategoryEntity(id = 0L, name = currentState.categoryName)
                val success = categoryRepository.insertCategory(newCategoryEntity)

                if(success) {
                    _categoryCreateUiState.update { it.copy(isCreating = false, createSuccess = true) }
                    loadManagedCategories()
                } else {
                    _categoryCreateUiState.update { it.copy(isCreating = false, errorMessage = "Error API.") }
                }
            } catch (e: Exception) {
                _categoryCreateUiState.update { it.copy(isCreating = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteCategory(categoryId: Long, onDeletionSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val success = categoryRepository.deleteCategory(categoryId)

                if (success) {
                    onDeletionSuccess()
                    loadManagedCategories()
                    snackbarHostState.showSnackbar("Categoría eliminada correctamente.")
                } else {
                    snackbarHostState.showSnackbar("Error al eliminar la categoría del servidor.")
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Error: ${e.message}")
            }
        }
    }

    fun clearEditCategoryState() { _categoryEditUiState.value = CategoryEditUiState() }
    fun clearCreateCategoryState() { _categoryCreateUiState.value = CategoryCreateUiState() }

    // --- CART & CHECKOUT ---

    fun onCurrencyChange(currency: String) { selectedCurrency = currency }

    fun addToCart(product: ProductEntity) {
        viewModelScope.launch {
            _cartUiState.update { currentState ->
                val existingItem = currentState.items.find { it.productId == product.id.toInt() }
                val newItems = if (existingItem != null) {
                    if (existingItem.quantity < product.stock) {
                        currentState.items.map { if (it.productId == product.id.toInt()) it.copy(quantity = it.quantity + 1) else it }
                    } else {
                        currentState.items
                    }
                } else {
                    currentState.items + CartItem(
                        productId = product.id.toInt(),
                        name = product.name,
                        price = product.price,
                        quantity = 1,
                        stock = product.stock
                    )
                }
                val newTotal = newItems.sumOf { it.price * it.quantity }
                currentState.copy(items = newItems, total = newTotal)
            }
            calculateClpTotal()
            snackbarHostState.showSnackbar("${product.name} añadido.")
        }
    }

    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            _cartUiState.update { currentState ->
                val newItems = currentState.items.filterNot { it.productId == productId }
                val newTotal = newItems.sumOf { it.price * it.quantity }
                currentState.copy(items = newItems, total = newTotal)
            }
            calculateClpTotal()
        }
    }

    fun clearCart() { _cartUiState.value = CartUiState(); totalInClp = 0.0 }

    fun increaseCartItemQuantity(productId: Int) {
        _cartUiState.update { currentState ->
            val newItems = currentState.items.map { item ->
                if (item.productId == productId && item.quantity < item.stock) {
                    item.copy(quantity = item.quantity + 1)
                } else {
                    item
                }
            }
            val newTotal = newItems.sumOf { it.price * it.quantity }
            currentState.copy(items = newItems, total = newTotal)
        }
        calculateClpTotal()
    }

    fun decreaseCartItemQuantity(productId: Int) {
        _cartUiState.update { currentState ->
            val itemToDecrease = currentState.items.find { it.productId == productId }
            val newItems = if (itemToDecrease != null && itemToDecrease.quantity > 1) {
                currentState.items.map { item ->
                    if (item.productId == productId) item.copy(quantity = item.quantity - 1) else item
                }
            } else {
                currentState.items.filterNot { it.productId == productId }
            }
            val newTotal = newItems.sumOf { it.price * it.quantity }
            currentState.copy(items = newItems, total = newTotal)
        }
        calculateClpTotal()
    }

    fun calculateClpTotal() {
        viewModelScope.launch {
            val dolarValue = saleRepository.getDolarPrice()
            totalInClp = _cartUiState.value.total * dolarValue
        }
    }

    fun performCheckout() {
        viewModelScope.launch {
            val currentUser = _uiState.value.currentUser
            if (currentUser == null) {
                snackbarHostState.showSnackbar("Inicia sesión para comprar")
                return@launch
            }

            val userId = currentUser.id
            if (userId == null) {
                snackbarHostState.showSnackbar("Error: Usuario sin ID")
                return@launch
            }

            if (_cartUiState.value.items.isEmpty()) {
                snackbarHostState.showSnackbar("Carrito vacío")
                return@launch
            }

            val result = saleRepository.checkout(userId, _cartUiState.value.items)

            if (result.success) {
                clearCart()
                snackbarHostState.showSnackbar("¡${result.message}!")
            } else {
                snackbarHostState.showSnackbar("Error: ${result.message}")
            }
        }
    }

    // --- PROFILE MANAGEMENT ---

    fun startEditingProfile() {
        val currentUser = _uiState.value.currentUser
        if (currentUser != null) {
            editUsername = currentUser.names
            isEditingProfile = true
        }
    }

    fun cancelEditingProfile() {
        isEditingProfile = false
    }

    fun onEditUsernameChange(newName: String) {
        editUsername = newName
    }

    fun saveProfileChanges() {
        viewModelScope.launch {
            val currentUser = _uiState.value.currentUser ?: return@launch
            if (editUsername.isBlank()) {
                snackbarHostState.showSnackbar("El nombre no puede estar vacío")
                return@launch
            }
            val updatedUser = currentUser.copy(names = editUsername)
            try {
                userRepository.updateUser(updatedUser)
                _uiState.update { it.copy(currentUser = updatedUser) }
                _userState.update { it.copy(username = updatedUser.names) }
                isEditingProfile = false
                snackbarHostState.showSnackbar("Perfil actualizado")
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Error: ${e.message}")
            }
        }
    }

    // --- AUTHENTICATION ---

    fun loginUser(onSuccess: (route: String) -> Unit) {
        viewModelScope.launch {
            uiStateLogin = uiStateLogin.copy(isSubmitting = true, errorMsg = null)
            try {
                val user = userRepository.getUserByEmail(uiStateLogin.email)
                if (user != null && user.password == uiStateLogin.pass) {
                    val isAdmin = user.rol?.id == 1L
                    _uiState.update { it.copy(currentUser = user, isAdmin = isAdmin) }
                    _userState.update { it.copy(username = user.names, email = user.email) }
                    onSuccess(if (isAdmin) "admin_home" else "catalog")
                } else {
                    uiStateLogin = uiStateLogin.copy(errorMsg = "Credenciales incorrectas", isSubmitting = false)
                }
            } catch (e: Exception) {
                uiStateLogin = uiStateLogin.copy(errorMsg = "Error: ${e.message}", isSubmitting = false)
            }
        }
    }

    fun registerUser(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val usernameError = Validation.validateUsername(uiStateRegister.username)
            val emailError = Validation.validateEmail(uiStateRegister.email)
            val passError = Validation.validatePassword(uiStateRegister.pass)
            val confirmPassError = Validation.validateConfirmPassword(uiStateRegister.pass, uiStateRegister.confirmPass)

            uiStateRegister = uiStateRegister.copy(usernameError = usernameError, emailError = emailError, passError = passError, confirmPassError = confirmPassError)
            if (listOf(usernameError, emailError, passError, confirmPassError).any { it != null }) return@launch

            uiStateRegister = uiStateRegister.copy(isSubmitting = true, errorMsg = null)

            try {
                if (userRepository.getUserByEmail(uiStateRegister.email) != null) {
                    uiStateRegister = uiStateRegister.copy(emailError = "Correo ya registrado.", isSubmitting = false)
                    return@launch
                }

                val clienteRol = RolEntity(id = 2, name = "Cliente")

                val newUser = UserEntity(
                    names = uiStateRegister.username,
                    email = uiStateRegister.email,
                    password = uiStateRegister.pass,
                    status = true,
                    rol = clienteRol
                )

                val newUserId = userRepository.insertUser(newUser)
                if (newUserId > 0) {
                    uiStateRegister = uiStateRegister.copy(success = true, isSubmitting = false)
                    onSuccess()
                } else {
                    uiStateRegister = uiStateRegister.copy(errorMsg = "Error en servidor.", isSubmitting = false)
                }
            } catch (e: Exception) {
                uiStateRegister = uiStateRegister.copy(errorMsg = "Error: ${e.message}", isSubmitting = false)
            }
        }
    }


    // --- HELPERS ---
    fun updateLoginEmail(email: String) { uiStateLogin = uiStateLogin.copy(email = email, emailError = Validation.validateEmail(email), canSubmit = validateLoginForm()) }
    fun updateLoginPassword(pass: String) { uiStateLogin = uiStateLogin.copy(pass = pass, passError = Validation.validatePassword(pass), canSubmit = validateLoginForm()) }
    fun updateRegisterUsername(username: String) { uiStateRegister = uiStateRegister.copy(username = username, usernameError = Validation.validateUsername(username), canSubmit = validateRegisterForm()) }
    fun updateRegisterEmail(email: String) { uiStateRegister = uiStateRegister.copy(email = email, emailError = Validation.validateEmail(email), canSubmit = validateRegisterForm()) }
    fun updateRegisterPassword(pass: String) { uiStateRegister = uiStateRegister.copy(pass = pass, passError = Validation.validatePassword(pass), canSubmit = validateRegisterForm()) }
    fun updateRegisterConfirmPassword(confirmPass: String) { uiStateRegister = uiStateRegister.copy(confirmPass = confirmPass, confirmPassError = Validation.validateConfirmPassword(uiStateRegister.pass, confirmPass), canSubmit = validateRegisterForm()) }

    fun clearLoginState() { uiStateLogin = LoginUiState() }
    fun clearRegisterState() { uiStateRegister = RegisterUiState() }

    private fun validateLoginForm(): Boolean {
        val state = uiStateLogin
        return state.emailError == null && state.passError == null && state.email.isNotBlank() && state.pass.isNotBlank()
    }

    private fun validateRegisterForm(): Boolean {
        val state = uiStateRegister
        return state.usernameError == null && state.emailError == null && state.passError == null && state.confirmPassError == null && state.username.isNotBlank() && state.email.isNotBlank() && state.pass.isNotBlank() && state.confirmPass.isNotBlank()
    }
}