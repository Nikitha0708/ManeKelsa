package com.manekelsa.app.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.manekelsa.app.navigation.Screen
import com.manekelsa.app.ui.theme.OrangePrimary
import com.manekelsa.app.utils.Constants
import com.manekelsa.app.utils.LanguageManager

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val lang by LanguageManager.isKannada

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf(Constants.USER_TYPE_WORKER) }
    var isLoading by remember { mutableStateOf(false) }

    fun t(kn: String, en: String) = LanguageManager.t(kn, en)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // Lang toggle top right
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End) {
                LangButton(onDark = false)
            }

            Text("🏠", fontSize = 56.sp)
            Text("ಮನೆ-ಕೆಲ್ಸ", fontSize = 32.sp,
                fontWeight = FontWeight.Bold, color = OrangePrimary)
            Text(
                t("ಲಾಗಿನ್ ಮಾಡಿ", "Login"),
                fontSize = 16.sp, color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            Text(t("ನೀವು ಯಾರು?", "Who are you?"),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                UserTypeButton(
                    label = t("👷 ಕೆಲಸಗಾರ", "👷 Worker"),
                    selected = userType == Constants.USER_TYPE_WORKER,
                    onClick = { userType = Constants.USER_TYPE_WORKER }
                )
                UserTypeButton(
                    label = t("🏠 ಉದ್ಯೋಗದಾತ", "🏠 Employer"),
                    selected = userType == Constants.USER_TYPE_EMPLOYER,
                    onClick = { userType = Constants.USER_TYPE_EMPLOYER }
                )
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(t("ಇಮೇಲ್", "Email")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(t("ಪಾಸ್‌ವರ್ಡ್", "Password")) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context,
                            t("ಎಲ್ಲಾ ವಿವರಗಳನ್ನು ನಮೂದಿಸಿ", "Please fill all details"),
                            Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    auth.signInWithEmailAndPassword(email.trim(), password.trim())
                        .addOnSuccessListener { result ->
                            val uid = result.user?.uid ?: ""
                            context.getSharedPreferences(
                                Constants.PREF_NAME, Context.MODE_PRIVATE)
                                .edit()
                                .putString(Constants.KEY_USER_ID, uid)
                                .putString(Constants.KEY_USER_TYPE, userType)
                                .apply()
                            isLoading = false
                            val dest = if (userType == Constants.USER_TYPE_WORKER)
                                Screen.WorkerHome.route else Screen.EmployerHome.route
                            navController.navigate(dest) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        .addOnFailureListener {
                            isLoading = false
                            Toast.makeText(context,
                                t("ಲಾಗಿನ್ ವಿಫಲ", "Login failed") + ": ${it.message}",
                                Toast.LENGTH_LONG).show()
                        }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                enabled = !isLoading
            ) {
                if (isLoading)
                    CircularProgressIndicator(color = Color.White,
                        modifier = Modifier.size(24.dp))
                else Text(t("ಲಾಗಿನ್", "Login"),
                    fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))
            TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                Text(t("ಖಾತೆ ಇಲ್ಲವೇ? ನೋಂದಣಿ ಮಾಡಿ",
                    "No account? Register here"), color = OrangePrimary)
            }
        }
    }
}

@Composable
fun UserTypeButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) OrangePrimary else Color.White,
            contentColor = if (selected) Color.White else OrangePrimary
        ),
        shape = RoundedCornerShape(10.dp),
        border = if (!selected)
            androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary) else null
    ) {
        Text(label, fontSize = 14.sp)
    }
}