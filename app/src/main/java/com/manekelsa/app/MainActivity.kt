package com.manekelsa.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.manekelsa.app.navigation.NavGraph
import com.manekelsa.app.ui.theme.ManeKelsaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManeKelsaTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}