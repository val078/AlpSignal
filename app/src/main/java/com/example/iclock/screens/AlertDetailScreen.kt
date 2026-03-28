package com.example.iclock.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.iclock.navigation.Screen

@Composable
fun AlertDetailScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 48.dp, bottom = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.AlertDetail.route) { inclusive = true }
                }
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF374151))
            }
            Column {
                Text("2035 Alert", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                Text("Mer de Glace • Satellite projection", fontSize = 12.sp, color = Color(0xFF6B7280))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEA580C), modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Alert level", fontSize = 12.sp, color = Color(0xFF6B7280))
                    Text("⚠️ WARNING", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEA580C))
                    Text("Projection for year 2035", fontSize = 12.sp, color = Color(0xFF6B7280))
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Satellite, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Predicted satellite data", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DataChip("NDSI", "7.21", Color(0xFFFFF7ED), Color(0xFFEA580C), Modifier.weight(1f))
                    DataChip("Temp", "16.2°C", Color(0xFFFEF2F2), Color(0xFFDC2626), Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DataChip("Area", "2.31 km²", Color(0xFFEFF6FF), Color(0xFF2563EB), Modifier.weight(1f))
                    DataChip("Volume", "0.12 km³", Color(0xFFEFF6FF), Color(0xFF0891B2), Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingDown, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Projected loss from today: -26% snow coverage", fontSize = 12.sp, color = Color(0xFF991B1B))
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Expected consequences by 2035", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
                }
                Spacer(modifier = Modifier.height(12.dp))

                listOf(
                    Triple(Icons.Default.Water, "Water scarcity", "Communities in the Arve valley will rely less on the glacier as a summer water reserve, increasing the risk of local droughts."),
                    Triple(Icons.Default.Warning, "Land instability", "Recently deglaciated areas will be exposed to rockfalls and landslides, putting hiking routes and structures at risk."),
                    Triple(Icons.Default.Thermostat, "Climate feedback loop", "The loss of white surface reduces the albedo effect — the glacier will absorb more solar heat, accelerating its own melting."),
                    Triple(Icons.Default.AttachMoney, "Economic impact", "Glacier tourism in Chamonix generates millions annually. By 2035, the ice cave will require costly interventions to remain accessible."),
                    Triple(Icons.Default.Eco, "Biodiversity loss", "Cold-water ecosystems that depend on the glacier will see their conditions altered, affecting unique species of the Alpine region.")
                ).forEach { (icon, title, desc) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFECACA)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(desc, fontSize = 12.sp, color = Color(0xFF374151), lineHeight = 17.sp)
                            }
                        }
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("What can you do today?", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
                }
                Spacer(modifier = Modifier.height(12.dp))

                listOf(
                    Triple("🚲", "Use sustainable transport", "Every km by bike saves 150g of CO₂"),
                    Triple("🌱", "Reduce your carbon footprint", "Small daily changes make a big difference"),
                    Triple("📢", "Share this information", "Fight climate misinformation"),
                    Triple("✈️", "Cut short-haul flights", "One flight emits more CO₂ than a month of driving"),
                    Triple("💡", "Switch to renewable energy", "Change to a green energy tariff")
                ).forEach { (emoji, title, desc) ->
                    TipCard(emoji, title, desc)
                    Spacer(modifier = Modifier.height(8.dp))
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
                Text("Data source", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Prediction generated using a Random Forest + Linear Regression hybrid model (R²=0.906) trained on 24 years of MODIS and Landsat satellite data processed in Google Earth Engine.",
                    fontSize = 12.sp, color = Color(0xFF374151), lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
fun DataChip(label: String, value: String, bgColor: Color, textColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, fontSize = 11.sp, color = Color(0xFF6B7280))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}

@Composable
fun TipCard(emoji: String, title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827))
                Text(description, fontSize = 11.sp, color = Color(0xFF6B7280), lineHeight = 16.sp)
            }
        }
    }
}