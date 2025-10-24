// MainActivity.kt
package com.example.pokeshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.pokeshop.navigation.AppNavGraph
import com.example.pokeshop.ui.theme.PokeShopTheme
import com.example.pokeshop.viewmodel.PokeShopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokeShopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val app = context.applicationContext as PokeShopApplication
                    val navController = rememberNavController()

                    val viewModel = remember {
                        PokeShopViewModel(
                            userRepository = app.userRepository,
                            rolRepository = app.rolRepository,
                            categoryRepository = app.categoryRepository,
                            productRepository = app.productRepository,
                            saleRepository = app.saleRepository,
                            saleDetailRepository = app.saleDetailRepository
                        )
                    }

                    AppNavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}