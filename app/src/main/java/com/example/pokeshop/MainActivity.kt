package com.example.pokeshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.pokeshop.navigation.AppNavGraph
import com.example.pokeshop.ui.components.SplashScreen
import com.example.pokeshop.ui.theme.PokeShopTheme
import com.example.pokeshop.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokeShopTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Obtenemos la instancia de la Aplicación (donde están los repositorios)
                    val context = LocalContext.current
                    val app = context.applicationContext as PokeShopApplication

                    // 2. Controlador de navegación
                    val navController = rememberNavController()

                    // 3. Instanciamos el ViewModel usando la Factory
                    // OJO: Usamos 'app' para acceder a los repositorios, no 'application'
                    val viewModel: PokeShopViewModel = viewModel(
                        factory = PokeShopViewModelFactory(
                            userRepository = app.userRepository,
                            rolRepository = app.rolRepository,
                            categoryRepository = app.categoryRepository,
                            productRepository = app.productRepository,
                            saleRepository = app.saleRepository
                            // Ya NO pasamos saleDetailRepository
                        )
                    )

                    var showSplash by remember { mutableStateOf(true) }

                    if (showSplash) {
                        SplashScreen {
                            showSplash = false
                        }
                    } else {

                        // 4. Llamamos a tu grafo de navegación principal
                        AppNavGraph(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}