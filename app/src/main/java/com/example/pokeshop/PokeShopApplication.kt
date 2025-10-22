package com.example.pokeshop

import android.app.Application
import com.example.pokeshop.data.database.AppDatabase
import com.example.pokeshop.data.repository.*

class PokeShopApplication : Application() {

    val database by lazy { AppDatabase.getInstance(this) }

    // Repositorios
    val UserRepository by lazy { UserRepository(database.userDao()) }
    val rolRepository by lazy { RolRepository(database.rolDao()) }
    val categoryRepository by lazy { CategoryRepository(database.categoryDao()) }
    val productRepository by lazy { ProductRepository(database.productDao()) }
    val saleRepository by lazy { SaleRepository(database.saleDao()) }
    val saleDetailRepository by lazy { SaleDetailRepository(database.saleDetailDao()) }
}