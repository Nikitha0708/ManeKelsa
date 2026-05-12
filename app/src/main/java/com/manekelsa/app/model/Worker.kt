package com.manekelsa.app.model

data class Worker(
    val id: String = "",
    val name: String = "",
    val skill: String = "",
    val phone: String = "",
    val area: String = "",
    val photoUrl: String = "",
    val dailyRate: Double = 0.0,
    val isAvailable: Boolean = false,
    val thumbsUp: Int = 0,
    val userId: String = ""
)