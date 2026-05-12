package com.manekelsa.app.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object WorkerHome : Screen("worker_home")
    object EmployerHome : Screen("employer_home")
    object WorkerDetail : Screen("worker_detail/{workerId}") {
        fun createRoute(workerId: String) = "worker_detail/$workerId"
    }
}