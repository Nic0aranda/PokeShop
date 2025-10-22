// viewmodel/PokeShopViewModel.kt
package com.example.pokeshop.viewmodel

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

    // Flows para datos
    val allUsers = userRepository.getAllUsers()
    val allRoles = rolRepository.getAllRoles()
    val allCategories = categoryRepository.getAllCategories()
    val allProducts = productRepository.getAllProducts()
    val allSales = saleRepository.getAllSales()
    val availableProducts = productRepository.getAvailableProducts()
    val activeProducts = productRepository.getActiveProducts()

    // Funciones de roles
    fun insertRol(rol: RolEntity) {
        viewModelScope.launch {
            try {
                rolRepository.insertRol(rol)
                _uiState.value = UiState.Success("Rol agregado correctamente")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al agregar rol: ${e.message}")
            }
        }
    }

    fun updateRol(rol: RolEntity) {
        viewModelScope.launch {
            try {
                rolRepository.updateRol(rol)
                _uiState.value = UiState.Success("Rol actualizado correctamente")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al actualizar rol: ${e.message}")
            }
        }
    }

    suspend fun getRolByName(name: String): RolEntity? {
        return rolRepository.getRolByName(name)
    }

    // Funciones de usuarios
    fun insertUser(user: UserEntity) {
        viewModelScope.launch {
            try {
                userRepository.insertUser(user)
                _uiState.value = UiState.Success("Usuario creado correctamente")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al crear usuario: ${e.message}")
            }
        }
    }

    fun updateUser(user: UserEntity) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(user)
                _uiState.value = UiState.Success("Usuario editado correctamente")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al editar usuario: ${e.message}")
            }
        }
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        return userRepository.getUserByEmail(email)
    }

    fun updateUserStatus(userId: Long, status: Boolean) {
        viewModelScope.launch {
            try {
                userRepository.updateUserStatus(userId, status)
                _uiState.value = UiState.Success("Estado de usuario actualizado")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al actualizar estado: ${e.message}")
            }
        }
    }

    // funciones de categorias
    fun insertCategory(category: CategoryEntity) {
        viewModelScope.launch {
            try {
                categoryRepository.insertCategory(category)
                _uiState.value = UiState.Success("Categoría agregada correctamente")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al agregar categoría: ${e.message}")
            }
        }
    }

    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch {
            try {
                categoryRepository.updateCategory(category)
                _uiState.value = UiState.Success("Categoría actualizada correctamente")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al actualizar categoría: ${e.message}")
            }
        }
    }

    // funciones de productos
    fun insertProduct(product: ProductEntity) {
        viewModelScope.launch {
            try {
                productRepository.insertProduct(product)
                _uiState.value = UiState.Success("Producto agregado correctamente")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al agregar producto: ${e.message}")
            }
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            try {
                productRepository.updateProduct(product)
                _uiState.value = UiState.Success("Producto actualizado correctamente")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al actualizar producto: ${e.message}")
            }
        }
    }

    fun decreaseStock(productId: Long, quantity: Int) {
        viewModelScope.launch {
            try {
                productRepository.decreaseStock(productId, quantity)
                _uiState.value = UiState.Success("Stock actualizado correctamente")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al actualizar stock: ${e.message}")
            }
        }
    }

    fun increaseStock(productId: Long, quantity: Int) {
        viewModelScope.launch {
            try {
                productRepository.increaseStock(productId, quantity)
                _uiState.value = UiState.Success("Stock aumentado correctamente")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al aumentar stock: ${e.message}")
            }
        }
    }

    fun updateProductStatus(productId: Long, status: Boolean) {
        viewModelScope.launch {
            try {
                productRepository.updateProductStatus(productId, status)
                _uiState.value = UiState.Success("Estado de producto actualizado")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al actualizar estado: ${e.message}")
            }
        }
    }

    fun getProductsByCategory(categoryId: Long): Flow<List<ProductEntity>> {
        return productRepository.getProductsByCategory(categoryId)
    }

    fun searchProducts(searchQuery: String): Flow<List<ProductEntity>> {
        return productRepository.searchProducts(searchQuery)
    }

    // funciones de ventas
    fun insertSale(sale: SaleEntity) {
        viewModelScope.launch {
            try {
                saleRepository.insertSale(sale)
                _uiState.value = UiState.Success("Venta registrada correctamente")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al registrar venta: ${e.message}")
            }
        }
    }

    fun updateSaleStatus(saleId: Long, status: String) {
        viewModelScope.launch {
            try {
                saleRepository.updateSaleStatus(saleId, status)
                _uiState.value = UiState.Success("Estado de venta actualizado")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al actualizar estado: ${e.message}")
            }
        }
    }

    // funciones de detalle venta
    fun insertSaleDetail(saleDetail: SaleDetailEntity) {
        viewModelScope.launch {
            try {
                saleDetailRepository.insertSaleDetail(saleDetail)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al agregar detalle: ${e.message}")
            }
        }
    }

    fun getSaleDetailsBySaleId(saleId: Long): Flow<List<SaleDetailEntity>> {
        return saleDetailRepository.getSaleDetailsBySaleId(saleId)
    }

    // === OPERACIONES COMPUESTAS ===
    fun processSale(sale: SaleEntity, saleDetails: List<SaleDetailEntity>) {
        viewModelScope.launch {
            try {
                // Insertar la venta
                val saleId = saleRepository.insertSale(sale)

                // Insertar los detalles de la venta
                saleDetails.forEach { detail ->
                    val saleDetailWithId = detail.copy(saleId = saleId)
                    saleDetailRepository.insertSaleDetail(saleDetailWithId)

                    // Disminuir el stock de los productos
                    productRepository.decreaseStock(detail.productId, detail.quantity)
                }

                _uiState.value = UiState.Success("Venta procesada correctamente")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al procesar venta: ${e.message}")
            }
        }
    }

    // datos de prueba
    fun insertSampleData() {
        viewModelScope.launch {
            try {
                // Insertar roles de ejemplo
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