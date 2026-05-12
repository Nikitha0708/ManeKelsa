package com.manekelsa.app.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.*
import com.manekelsa.app.model.Worker
import com.manekelsa.app.navigation.Screen
import com.manekelsa.app.ui.theme.GreenAvailable
import com.manekelsa.app.ui.theme.OrangePrimary
import com.manekelsa.app.ui.theme.RedUnavailable
import com.manekelsa.app.utils.Constants
import com.manekelsa.app.utils.LanguageManager

@Composable
fun WorkerHomeScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    val workerId = prefs.getString(Constants.KEY_USER_ID, "") ?: ""
    val lang by LanguageManager.isKannada

    fun t(kn: String, en: String) = LanguageManager.t(kn, en)

    var worker by remember { mutableStateOf<Worker?>(null) }
    var isAvailable by remember { mutableStateOf(false) }

    val dbRef = remember {
        FirebaseDatabase.getInstance().getReference("workers").child(workerId)
    }

    DisposableEffect(workerId) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val w = snapshot.getValue(Worker::class.java)
                if (w != null) {
                    worker = w.copy(id = workerId)
                    isAvailable = w.isAvailable
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        dbRef.addValueEventListener(listener)
        onDispose { dbRef.removeEventListener(listener) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // Lang toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                LangButton(onDark = false)
            }

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(OrangePrimary),
                contentAlignment = Alignment.Center
            ) {
                Text("👷", fontSize = 40.sp)
            }

            Spacer(Modifier.height(16.dp))

            worker?.let { w ->
                Text(w.name, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text(w.skill, fontSize = 16.sp, color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp))
                Text("📍 ${w.area}", fontSize = 14.sp, color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp))

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard("💰", "₹${w.dailyRate.toInt()}", t("ದಿನಕ್ಕೆ", "Per day"))
                    StatCard("👍", "${w.thumbsUp}", t("ಶ್ಲಾಘನೆ", "Ratings"))
                    StatCard("📞", w.phone, t("ಸಂಪರ್ಕ", "Contact"))
                }

                Spacer(Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isAvailable)
                            GreenAvailable.copy(alpha = 0.1f)
                        else RedUnavailable.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isAvailable)
                                t("✅ ಇಂದು ಕೆಲಸಕ್ಕೆ ಲಭ್ಯ", "✅ Available today")
                            else t("❌ ಇಂದು ಲಭ್ಯವಿಲ್ಲ", "❌ Not available today"),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAvailable) GreenAvailable else RedUnavailable
                        )
                        Spacer(Modifier.height(12.dp))
                        Switch(
                            checked = isAvailable,
                            onCheckedChange = { checked ->
                                isAvailable = checked
                                dbRef.child("available").setValue(checked)
                                    .addOnSuccessListener {
                                        val msg = if (checked)
                                            t("ಇಂದು ಲಭ್ಯ ಎಂದು ತೋರಿಸಲಾಗಿದೆ",
                                                "Marked as available today")
                                        else t("ಇಂದು ಲಭ್ಯವಿಲ್ಲ ಎಂದು ತೋರಿಸಲಾಗಿದೆ",
                                            "Marked as unavailable today")
                                        Toast.makeText(context, msg,
                                            Toast.LENGTH_SHORT).show()
                                    }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GreenAvailable
                            )
                        )
                        Text(
                            t("ಇಂದು ಕೆಲಸ ಮಾಡಲು ಸಿದ್ಧ ಇದ್ದರೆ ಆನ್ ಮಾಡಿ",
                                "Turn ON if ready to work today"),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } ?: CircularProgressIndicator(color = OrangePrimary)

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        prefs.edit().clear().apply()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.WorkerHome.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(t("ಲಾಗ್ ಔಟ್", "Logout"), color = OrangePrimary)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatCard(emoji: String, value: String, label: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 24.sp)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}