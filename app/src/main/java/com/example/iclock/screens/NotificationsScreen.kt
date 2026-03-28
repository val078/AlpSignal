package com.example.iclock.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.iclock.notifications.NotificationHelper

@Composable
fun NotificationsScreen(navController: NavController) {
    var notificationsEnabled by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            notificationsEnabled = true
            NotificationHelper.createChannel(context)
            NotificationHelper.sendGlacierAlert(context)
        }
    }

    fun handleToggle() {
        if (!notificationsEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val granted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    notificationsEnabled = true
                    NotificationHelper.createChannel(context)
                    NotificationHelper.sendGlacierAlert(context)
                } else {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                notificationsEnabled = true
                NotificationHelper.createChannel(context)
                NotificationHelper.sendGlacierAlert(context)
            }
        } else {
            notificationsEnabled = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 48.dp, bottom = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF374151))
            }
            Column {
                Text("Weekly Alerts", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                Text("Stay up to date on the glacier", fontSize = 12.sp, color = Color(0xFF6B7280))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFDBEAFE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Nearest glacier", fontSize = 12.sp, color = Color(0xFF6B7280))
                        Text("Mer de Glace", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                        Text("Chamonix, France", fontSize = 12.sp, color = Color(0xFF6B7280))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { handleToggle() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (notificationsEnabled) Color(0xFF16A34A) else Color(0xFF2563EB)
                    )
                ) {
                    Icon(
                        imageVector = if (notificationsEnabled) Icons.Default.CheckCircle else Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (notificationsEnabled) "Notifications Enabled" else "Enable Notifications",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "You'll receive a weekly summary featuring scientific data presented in a clear and accessible way",
                            fontSize = 12.sp,
                            color = Color(0xFF1E40AF)
                        )
                    }
                }
            }
        }

        if (notificationsEnabled) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2563EB), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Last report", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                Text("March 24–30, 2026", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Preview", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Weekly summary", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "The Mer de Glace glacier continues its steady retreat. Warmer-than-normal spring temperatures are accelerating surface melting. The glacier tongue has retreated approximately 120 meters over the past year.",
                        fontSize = 12.sp, color = Color(0xFF374151), lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ChangeCard("Area change", "-2.8%", "12 months", Modifier.weight(1f))
                        ChangeCard("Volume change", "-3.4%", "12 months", Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEA580C), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Identified risks", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    listOf(
                        "Increased risk of rockfalls in recently deglaciated areas",
                        "Potential flooding from glacial lake overflow during summer",
                        "Loss of stability in fractured ice zones",
                        "Impact on water supply for downstream communities during autumn"
                    ).forEachIndexed { index, risk ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED))
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFEA580C)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${index + 1}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(risk, fontSize = 12.sp, color = Color(0xFF374151), lineHeight = 17.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Recommendation", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Caution is advised when visiting the ice cave. Tourist infrastructure requires ongoing adjustments due to the glacier's retreat.",
                                fontSize = 12.sp, color = Color(0xFF374151), lineHeight = 17.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Settings", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
                    Spacer(modifier = Modifier.height(12.dp))
                    ConfigRow("Frequency", "Weekly")
                    ConfigRow("Send day", "Monday")
                    ConfigRow("Format", "Simple")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { notificationsEnabled = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.NotificationsOff, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Disable notifications", fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Why Mer de Glace?", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Mer de Glace (Sea of Ice) is the largest glacier in France and one of the most accessible in the Alps.", fontSize = 12.sp, color = Color(0xFF374151), lineHeight = 17.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Continuous monitoring since 1900 makes it a key indicator of climate change in the Alpine region.", fontSize = 12.sp, color = Color(0xFF374151), lineHeight = 17.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("It has lost over 120 meters in length in the past year alone, demonstrating the real impact of global warming.", fontSize = 12.sp, color = Color(0xFF374151), lineHeight = 17.sp)
            }
        }
    }
}

@Composable
fun ChangeCard(label: String, value: String, period: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TrendingDown, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
            Text(period, fontSize = 11.sp, color = Color(0xFF6B7280))
        }
    }
}

@Composable
fun ConfigRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF9FAFB))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827))
        Text(value, fontSize = 13.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Medium)
    }
    Spacer(modifier = Modifier.height(8.dp))
}