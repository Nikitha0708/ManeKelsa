package com.manekelsa.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.manekelsa.app.ui.screens.SplashScreen
import com.manekelsa.app.ui.screens.LoginScreen
import com.manekelsa.app.ui.screens.RegisterScreen
import com.manekelsa.app.ui.screens.WorkerHomeScreen
import com.manekelsa.app.ui.screens.EmployerHomeScreen
import com.manekelsa.app.ui.screens.WorkerDetailScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Register.route) { RegisterScreen(navController) }
        composable(Screen.WorkerHome.route) { WorkerHomeScreen(navController) }
        composable(Screen.EmployerHome.route) { EmployerHomeScreen(navController) }
        composable(Screen.WorkerDetail.route) { backStackEntry ->
            val workerId = backStackEntry.arguments?.getString("workerId") ?: ""
            WorkerDetailScreen(navController, workerId)
        }
    }
}