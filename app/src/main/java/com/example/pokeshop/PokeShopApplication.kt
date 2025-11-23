package com.example.pokeshop

import android.app.Application
import com.example.pokeshop.data.repository.*

class PokeShopApplication : Application() {

    // Instanciamos los repositorios directamente.
    // Al no pasar argumentos, usarán el RetrofitClient por defecto que configuramos.
    val userRepository by lazy { UserRepository() }
    val productRepository by lazy { ProductRepository() }
    val categoryRepository by lazy { CategoryRepository() }
    val rolRepository by lazy { RolRepository() }
    val saleRepository by lazy { SaleRepository() }

}