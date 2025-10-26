package com.example.pokeshop

import android.app.Application
import com.example.pokeshop.data.database.AppDatabase
import com.example.pokeshop.data.entities.CategoryEntity
import com.example.pokeshop.data.entities.ProductEntity
import com.example.pokeshop.data.entities.RolEntity
import com.example.pokeshop.data.entities.UserEntity
import com.example.pokeshop.data.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first


class PokeShopApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database by lazy { AppDatabase.getInstance(this) }

    val userRepository by lazy { UserRepository(database.userDao()) }
    val rolRepository by lazy { RolRepository(database.rolDao()) }
    val categoryRepository by lazy { CategoryRepository(database.categoryDao()) }
    val productRepository by lazy { ProductRepository(database.productDao()) }
    val saleRepository by lazy { SaleRepository(database.saleDao()) }
    val saleDetailRepository by lazy { SaleDetailRepository(database.saleDetailDao()) }

    override fun onCreate() {
        super.onCreate()
        // Llamamos a la función para inicializar los datos de prueba.
        initializeSampleData()
    }

    private fun initializeSampleData() {
        // Usamos el scope que creamos en lugar de viewModelScope.
        applicationScope.launch {
            // Inserta Roles si la tabla está vacía.
            if (rolRepository.getAllRoles().first().isEmpty()) {
                val roles = listOf(
                    RolEntity(name = "Vendedor"),
                    RolEntity(name = "Cliente")
                )
                roles.forEach { rolRepository.insertRol(it) }
            }

            // Inserta Usuarios si la tabla está vacía.
            if (userRepository.getAllUsers().first().isEmpty()) {
                val users = listOf(
                    // usuario vendedor
                    UserEntity(
                        names = "Vendedor",
                        lastNames = "Principal",
                        email = "vendedor@pokeshop.com",
                        password = "vendedor123",
                        rolId = 1 // ID del rol Vendedor
                    ),
                    // usuario cliente
                    UserEntity(
                        names = "Cliente",
                        lastNames = "Fiel",
                        email = "cliente@pokeshop.com",
                        password = "cliente123",
                        rolId = 2 // ID del rol Cliente
                    )
                )
                users.forEach { userRepository.insertUser(it) }
            }

            // Inserta Categorías si la tabla está vacía.
            if (categoryRepository.getAllCategories().first().isEmpty()) {
                val categories = listOf(
                    CategoryEntity(name = "Booster Packs"),
                    CategoryEntity(name = "Sobres"),
                    CategoryEntity(name = "Cartas Sueltas")
                )
                categories.forEach { categoryRepository.insertCategory(it) }
            }

            // Inserta Productos si la tabla está vacía.
            if (productRepository.getAllProducts().first().isEmpty()) {
                val products = listOf(
                    ProductEntity(name = "Booster Pack 1", price = 19.99, stock = 20, description = "Booster Pack 1", categoryId = 1),
                    ProductEntity(name = "Booster Pack 2", price = 24.99, stock = 25,description = "Booster Pack 2", categoryId = 1),
                    ProductEntity(name = "Booster Pack 3", price = 29.99, stock = 30, description = "Booster Pack 3", categoryId = 1),
                    ProductEntity(name = "Sobre 1", price = 9.99, stock = 10, description = "Sobre 1", categoryId = 2),
                    ProductEntity(name = "Sobre 2", price = 14.99, stock = 50, description = "sobre 2", categoryId = 2),
                    ProductEntity(name = "Sobre 3", price = 19.99, stock = 70, description = "sobre 3", categoryId = 2),
                    ProductEntity(name = "Carta Solitaria 1", price = 4.99, stock = 5, description = "Carta Solitaria 1", categoryId = 3),
                    ProductEntity(name = "Pikachu Illustrator", price = 9999999999.99, stock = 1, description = "Pikachu Illustrator", categoryId = 3)
                )
                products.forEach { productRepository.insertProduct(it) }
            }
        }
    }
}
