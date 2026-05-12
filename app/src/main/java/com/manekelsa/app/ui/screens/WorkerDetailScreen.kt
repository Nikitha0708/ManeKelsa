package com.manekelsa.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.manekelsa.app.ui.theme.GreenAvailable
import com.manekelsa.app.ui.theme.OrangePrimary
import com.manekelsa.app.ui.theme.RedUnavailable
import com.manekelsa.app.utils.LanguageManager

@Composable
fun WorkerDetailScreen(navController: NavController, workerId: String) {
    val context = LocalContext.current
    val lang by LanguageManager.isKannada
    var worker by remember { mutableStateOf<Worker?>(null) }

    fun t(kn: String, en: String) = LanguageManager.t(kn, en)

    DisposableEffect(workerId) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("workers/$workerId")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                worker = snapshot.getValue(Worker::class.java)
                    ?.copy(id = workerId)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addListenerForSingleValueEvent(listener)
        onDispose {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F0))
    ) {
        // Top bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(OrangePrimary)
                .padding(top = 48.dp, bottom = 20.dp,
                    start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack,
                            contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        t("ಕಾರ್ಮಿಕರ ವಿವರ", "Worker Details"),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                LangButton(onDark = true)
            }
        }

        worker?.let { w ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(OrangePrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👷", fontSize = 52.sp)
                }

                Spacer(Modifier.height(16.dp))
                Text(w.name, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(w.skill, fontSize = 16.sp, color = OrangePrimary,
                    fontWeight = FontWeight.Medium)

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (w.isAvailable)
                                GreenAvailable.copy(alpha = 0.15f)
                            else RedUnavailable.copy(alpha = 0.15f)
                        )
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (w.isAvailable)
                            t("✅ ಇಂದು ಕೆಲಸಕ್ಕೆ ಲಭ್ಯ", "✅ Available today")
                        else t("❌ ಇಂದು ಲಭ್ಯವಿಲ್ಲ", "❌ Not available today"),
                        color = if (w.isAvailable) GreenAvailable else RedUnavailable,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        DetailRow("📍", t("ಏರಿಯಾ", "Area"), w.area)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow("💰", t("ದಿನದ ದರ", "Daily Rate"),
                            "₹${w.dailyRate.toInt()} / ${t("ದಿನ", "day")}")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow("📞", t("ಫೋನ್", "Phone"), w.phone)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow("👍", t("ಶ್ಲಾಘನೆಗಳು", "Ratings"),
                            "${w.thumbsUp} ${t("ಜನ", "people")}")
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL,
                            Uri.parse("tel:${w.phone}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenAvailable)
                ) {
                    Text(
                        t("📞  ಈಗ ಕರೆ ಮಾಡಿ", "📞  Call Now"),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = OrangePrimary)
        }
    }
}

@Composable
fun DetailRow(emoji: String, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(emoji, fontSize = 22.sp)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}