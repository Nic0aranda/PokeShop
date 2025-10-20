package com.example.pokeshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.pokeshop.ui.screen.HomePS
import com.example.pokeshop.ui.theme.PokeShopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeShopTheme {
                HomePS()
                }
            }
        }
    }


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PokeShopTheme {
        HomePS()
    }
}