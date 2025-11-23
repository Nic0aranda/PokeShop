package com.example.pokeshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pokeshop.data.repository.*

// Esta clase sirve para inyectar los repositorios en el ViewModel
class PokeShopViewModelFactory(
    private val userRepository: UserRepository,
    private val rolRepository: RolRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val saleRepository: SaleRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PokeShopViewModel::class.java)) {
            return PokeShopViewModel(
                userRepository,
                rolRepository,
                categoryRepository,
                productRepository,
                saleRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}