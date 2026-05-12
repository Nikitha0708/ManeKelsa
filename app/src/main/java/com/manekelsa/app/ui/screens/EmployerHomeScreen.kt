package com.manekelsa.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
fun EmployerHomeScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    val lang by LanguageManager.isKannada

    fun t(kn: String, en: String) = LanguageManager.t(kn, en)

    var allWorkers by remember { mutableStateOf<List<Worker>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSkill by remember { mutableStateOf("all") }
    var showAvailableOnly by remember { mutableStateOf(false) }

    val skillsEN = listOf(
        "Cleaning", "Gardening", "Cooking", "Security",
        "Construction", "Driver", "Housework", "Plumbing", "Electrical"
    )

    val dbRef = remember {
        FirebaseDatabase.getInstance().getReference("workers")
    }

    DisposableEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Worker>()
                for (ds in snapshot.children) {
                    val w = ds.getValue(Worker::class.java)
                    if (w != null) list.add(w.copy(id = ds.key ?: ""))
                }
                allWorkers = list
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        dbRef.addValueEventListener(listener)
        onDispose { dbRef.removeEventListener(listener) }
    }

    val filtered = allWorkers.filter { w ->
        val matchSearch = searchQuery.isBlank() ||
                w.name.contains(searchQuery, true) ||
                w.area.contains(searchQuery, true)
        val matchSkill = selectedSkill == "all" || w.skill == selectedSkill
        val matchAvail = !showAvailableOnly || w.isAvailable
        matchSearch && matchSkill && matchAvail
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
                .padding(top = 48.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "ಮನೆ-ಕೆಲ್ಸ 🏠",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "${filtered.size} ${t("ಕಾರ್ಮಿಕರು ಕಂಡುಬಂದಿದ್ದಾರೆ", "workers found")}",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LangButton(onDark = true)
                        TextButton(onClick = {
                            prefs.edit().clear().apply()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.EmployerHome.route) { inclusive = true }
                            }
                        }) {
                            Text(t("ಔಟ್", "Out"), color = Color.White)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            t("ಹೆಸರು ಅಥವಾ ಏರಿಯಾ ಹುಡುಕಿ...",
                                "Search by name or area..."),
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null,
                            tint = Color.White)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    ),
                    singleLine = true
                )
            }
        }

        // Filter chips
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChipItem(
                    label = if (showAvailableOnly)
                        t("✅ ಲಭ್ಯ ಮಾತ್ರ", "✅ Available only")
                    else t("ಎಲ್ಲಾ", "All"),
                    selected = showAvailableOnly,
                    onClick = { showAvailableOnly = !showAvailableOnly }
                )
            }
            item {
                FilterChipItem(
                    label = t("ಎಲ್ಲಾ ಕೆಲಸ", "All Skills"),
                    selected = selectedSkill == "all",
                    onClick = { selectedSkill = "all" }
                )
            }
            items(Constants.SKILLS.indices.toList()) { index ->
                FilterChipItem(
                    label = if (lang) Constants.SKILLS[index] else skillsEN[index],
                    selected = selectedSkill == Constants.SKILLS[index],
                    onClick = { selectedSkill = Constants.SKILLS[index] }
                )
            }
        }

        // Worker list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered) { worker ->
                WorkerCard(
                    worker = worker,
                    onClick = {
                        navController.navigate(
                            Screen.WorkerDetail.createRoute(worker.id))
                    },
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL,
                            Uri.parse("tel:${worker.phone}"))
                        context.startActivity(intent)
                    },
                    onThumbsUp = {
                        val newCount = worker.thumbsUp + 1
                        FirebaseDatabase.getInstance()
                            .getReference("workers/${worker.id}/thumbsUp")
                            .setValue(newCount)
                        Toast.makeText(context,
                            t("👍 ಧನ್ಯವಾದಗಳು!", "👍 Thank you!"),
                            Toast.LENGTH_SHORT).show()
                    }
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun FilterChipItem(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 13.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = OrangePrimary,
            selectedLabelColor = Color.White
        )
    )
}

@Composable
fun WorkerCard(
    worker: Worker,
    onClick: () -> Unit,
    onCall: () -> Unit,
    onThumbsUp: () -> Unit
) {
    fun t(kn: String, en: String) = LanguageManager.t(kn, en)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(OrangePrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("👷", fontSize = 28.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(worker.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (worker.isAvailable)
                                    GreenAvailable.copy(alpha = 0.15f)
                                else RedUnavailable.copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (worker.isAvailable)
                                t("ಲಭ್ಯ", "Available")
                            else t("ಲಭ್ಯವಿಲ್ಲ", "Unavailable"),
                            fontSize = 11.sp,
                            color = if (worker.isAvailable) GreenAvailable
                            else RedUnavailable,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Text(worker.skill, color = OrangePrimary,
                    fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text("📍 ${worker.area}", color = Color.Gray, fontSize = 12.sp)
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("₹${worker.dailyRate.toInt()}/${t("ದಿನ", "day")}",
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("👍 ${worker.thumbsUp}", fontSize = 13.sp, color = Color.Gray)
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onCall) { Text("📞", fontSize = 24.sp) }
                IconButton(onClick = onThumbsUp) { Text("👍", fontSize = 22.sp) }
            }
        }
    }
}