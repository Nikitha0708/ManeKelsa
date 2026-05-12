package com.manekelsa.app.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.manekelsa.app.navigation.Screen
import com.manekelsa.app.ui.theme.OrangePrimary
import com.manekelsa.app.utils.Constants
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(2000)
        val prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
        val userId   = prefs.getString(Constants.KEY_USER_ID, null)
        val userType = prefs.getString(Constants.KEY_USER_TYPE, null)

        if (userId != null && userType != null) {
            val dest = if (userType == Constants.USER_TYPE_WORKER)
                Screen.WorkerHome.route else Screen.EmployerHome.route
            navController.navigate(dest) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OrangePrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🏠", fontSize = 72.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "ಮನೆ-ಕೆಲ್ಸ",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text("Mane-Kelsa", fontSize = 20.sp, color = Color.White.copy(alpha = 0.8f))
            Text(
                text = "ನಿಮ್ಮ ಸ್ಥಳೀಯ ಕೆಲಸಗಾರರ ನಕ",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(Modifier.height(48.dp))
            CircularProgressIndicator(color = Color.White)
        }
    }
}