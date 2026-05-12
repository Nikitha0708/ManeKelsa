package com.manekelsa.app.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manekelsa.app.utils.LanguageManager

@Composable
fun LangButton(onDark: Boolean = false) {
    val isKannada by LanguageManager.isKannada
    val color = if (onDark) Color.White else Color(0xFFE65100)

    TextButton(
        onClick = { LanguageManager.toggle() },
        modifier = Modifier
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .padding(horizontal = 2.dp)
    ) {
        Text(
            text = if (isKannada) "EN" else "ಕನ್ನಡ",
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}