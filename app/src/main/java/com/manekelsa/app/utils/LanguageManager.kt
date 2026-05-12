package com.manekelsa.app.utils

import androidx.compose.runtime.mutableStateOf

object LanguageManager {
    val isKannada = mutableStateOf(true)

    fun toggle() {
        isKannada.value = !isKannada.value
    }

    fun t(kannada: String, english: String): String {
        return if (isKannada.value) kannada else english
    }
}