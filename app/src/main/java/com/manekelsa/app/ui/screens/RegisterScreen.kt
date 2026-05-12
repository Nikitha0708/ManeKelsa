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
import com.google.firebase.database.FirebaseDatabase
import com.manekelsa.app.model.Worker
import com.manekelsa.app.navigation.Screen
import com.manekelsa.app.ui.theme.OrangePrimary
import com.manekelsa.app.utils.Constants
import com.manekelsa.app.utils.LanguageManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val lang by LanguageManager.isKannada

    fun t(kn: String, en: String) = LanguageManager.t(kn, en)

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf(Constants.USER_TYPE_WORKER) }
    var selectedSkill by remember { mutableStateOf(Constants.SKILLS[0]) }
    var skillExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val skillsEN = listOf(
        "Cleaning", "Gardening", "Cooking", "Security",
        "Construction", "Driver", "Housework", "Plumbing", "Electrical"
    )

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
            Spacer(Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                LangButton(onDark = false)
            }

            Text(
                t("ನೋಂದಣಿ", "Register"),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = OrangePrimary
            )
            Text(
                t("ಹೊಸ ಖಾತೆ ತೆರೆಯಿರಿ", "Create a new account"),
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                t("ನೀವು ಯಾರು?", "Who are you?"),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RegTypeButton(
                    label = t("👷 ಕೆಲಸಗಾರ", "👷 Worker"),
                    selected = userType == Constants.USER_TYPE_WORKER,
                    onClick = { userType = Constants.USER_TYPE_WORKER }
                )
                RegTypeButton(
                    label = t("🏠 ಉದ್ಯೋಗದಾತ", "🏠 Employer"),
                    selected = userType == Constants.USER_TYPE_EMPLOYER,
                    onClick = { userType = Constants.USER_TYPE_EMPLOYER }
                )
            }
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text(t("ಹೆಸರು *", "Name *")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = phone, onValueChange = { phone = it },
                label = { Text(t("ಫೋನ್ ಸಂಖ್ಯೆ *", "Phone *")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = area, onValueChange = { area = it },
                label = { Text(t("ಏರಿಯಾ / ಬೀದಿ", "Area / Street")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Spacer(Modifier.height(10.dp))

            if (userType == Constants.USER_TYPE_WORKER) {
                val displaySkills = if (lang) Constants.SKILLS else skillsEN
                ExposedDropdownMenuBox(
                    expanded = skillExpanded,
                    onExpandedChange = { skillExpanded = !skillExpanded }
                ) {
                    val skillIndex = Constants.SKILLS.indexOf(selectedSkill).coerceAtLeast(0)
                    OutlinedTextField(
                        value = if (lang) selectedSkill else skillsEN[skillIndex],
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(t("ಕೌಶಲ್ಯ", "Skill")) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = skillExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = skillExpanded,
                        onDismissRequest = { skillExpanded = false }
                    ) {
                        displaySkills.forEachIndexed { index, skill ->
                            DropdownMenuItem(
                                text = { Text(skill) },
                                onClick = {
                                    selectedSkill = Constants.SKILLS[index]
                                    skillExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = rate, onValueChange = { rate = it },
                    label = { Text(t("ದಿನದ ದರ (₹)", "Daily Rate (₹)")) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), singleLine = true
                )
                Spacer(Modifier.height(10.dp))
            }

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text(t("ಇಮೇಲ್ *", "Email *")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text(t("ಪಾಸ್‌ವರ್ಡ್ *", "Password *")) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp), singleLine = true
            )
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isBlank() || phone.isBlank() ||
                        email.isBlank() || password.isBlank()) {
                        Toast.makeText(
                            context,
                            t("ಕಡ್ಡಾಯ ವಿವರಗಳನ್ನು ನಮೂದಿಸಿ", "Please fill required fields"),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    isLoading = true
                    auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                        .addOnSuccessListener { result ->
                            val uid = result.user?.uid ?: ""
                            context.getSharedPreferences(
                                Constants.PREF_NAME, Context.MODE_PRIVATE
                            ).edit()
                                .putString(Constants.KEY_USER_ID, uid)
                                .putString(Constants.KEY_USER_TYPE, userType)
                                .apply()
                            val db = FirebaseDatabase.getInstance().reference
                            if (userType == Constants.USER_TYPE_WORKER) {
                                val worker = Worker(
                                    id = uid, name = name.trim(),
                                    skill = selectedSkill, phone = phone.trim(),
                                    area = area.trim(),
                                    dailyRate = rate.toDoubleOrNull() ?: 0.0,
                                    isAvailable = false, thumbsUp = 0, userId = uid
                                )
                                db.child("workers").child(uid).setValue(worker)
                            } else {
                                val emp = mapOf(
                                    "name" to name, "phone" to phone,
                                    "area" to area, "type" to userType
                                )
                                db.child("users").child(uid).setValue(emp)
                            }
                            isLoading = false
                            val dest = if (userType == Constants.USER_TYPE_WORKER)
                                Screen.WorkerHome.route else Screen.EmployerHome.route
                            navController.navigate(dest) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        }
                        .addOnFailureListener {
                            isLoading = false
                            Toast.makeText(
                                context,
                                t("ನೋಂದಣಿ ವಿಫಲ", "Registration failed") + ": ${it.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                enabled = !isLoading
            ) {
                if (isLoading)
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else
                    Text(t("ನೋಂದಣಿ", "Register"), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))
            TextButton(onClick = { navController.popBackStack() }) {
                Text(
                    t("ಈಗಾಗಲೇ ಖಾತೆ ಇದೆಯೇ? ಲಾಗಿನ್ ಮಾಡಿ", "Already have an account? Login"),
                    color = OrangePrimary
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun RegTypeButton(label: String, selected: Boolean, onClick: () -> Unit) {
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