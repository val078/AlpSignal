package com.example.iclock.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.iclock.viewmodel.GlacierViewModel

data class GlacierInfo(
    val id: String,
    val name: String,
    val country: String
)

val glacierList = listOf(
    GlacierInfo("mer-de-glace", "Mer de Glace", "France"),
    GlacierInfo("aletsch", "Aletsch", "Switzerland"),
    GlacierInfo("pasterze", "Pasterze", "Austria"),
    GlacierInfo("rodano", "Rhône", "Switzerland"),
    GlacierInfo("adamello", "Adamello", "Italy"),
)

@Composable
fun ProjectionsScreen(
    navController: NavController,
    vm: GlacierViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

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
                Text("Projections up to 2100", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                Text("Mer de Glace • France", fontSize = 12.sp, color = Color(0xFF6B7280))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF2563EB))
            }
            return@Column
        }

        if (state.error != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
            ) {
                Text("Error: ${state.error}", modifier = Modifier.padding(16.dp), color = Color(0xFFDC2626), fontSize = 13.sp)
            }
            return@Column
        }

        GlacierSelector()

        Spacer(modifier = Modifier.height(20.dp))

        val firstYear = state.historical.firstOrNull()
        val lastYear  = state.historical.lastOrNull()
        val areaLoss  = if (firstYear != null && lastYear != null && firstYear.area_km2 > 0)
            ((firstYear.area_km2 - lastYear.area_km2) / firstYear.area_km2 * 100).toInt() else 0

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Area ${firstYear?.year ?: 2000}", "${firstYear?.area_km2 ?: "-"} km²", Color(0xFF2563EB), Modifier.weight(1f))
            MetricCard("Area ${lastYear?.year ?: 2024}", "${lastYear?.area_km2 ?: "-"} km²", Color(0xFFDC2626), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Area loss", "-$areaLoss%", Color(0xFFDC2626), Modifier.weight(1f))
            MetricCard("Critical year", "~${state.yearCritical}", Color(0xFFEA580C), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (state.historical.isNotEmpty()) {
            ChartCard(
                title = "Snow coverage (NDSI)",
                subtitle = "MODIS satellite data 2000–2024",
                warningText = "⚠️ NDSI dropped 43% since the year 2000",
                warningColor = Color(0xFFFEF2F2),
                warningTextColor = Color(0xFF991B1B)
            ) {
                SimpleLineChart(
                    data = state.historical.map { it.ndsi },
                    labels = state.historical.map { it.year.toString() },
                    lineColor = Color(0xFF2563EB),
                    fillColor = Color(0xFF2563EB).copy(alpha = 0.1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ChartCard(
                title = "Volume vs Temperature",
                subtitle = "Correlation between ice loss and warming",
                warningText = "🌡️ Volume loss accelerates with every degree of warming",
                warningColor = Color(0xFFFFF7ED),
                warningTextColor = Color(0xFF92400E)
            ) {
                DualLineChart(
                    data1 = state.historical.map { it.vol_km3 },
                    data2 = state.historical.map { it.temp_celsius },
                    labels = state.historical.map { it.year.toString() },
                    color1 = Color(0xFF0891B2),
                    color2 = Color(0xFFDC2626)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (state.predictions.isNotEmpty()) {
            ChartCard(
                title = "NDSI Prediction 2030–2100",
                subtitle = "Model: ${state.modelUsed} (R²=${state.rfR2})",
                warningText = "🔴 The glacier is projected to disappear around ${state.yearCritical}",
                warningColor = Color(0xFFFEF2F2),
                warningTextColor = Color(0xFF991B1B)
            ) {
                SimpleLineChart(
                    data = state.predictions.map { it.ndsi },
                    labels = state.predictions.map { it.year.toString() },
                    lineColor = Color(0xFFEA580C),
                    fillColor = Color(0xFFEA580C).copy(alpha = 0.1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Methodology", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
                Spacer(modifier = Modifier.height(8.dp))
                listOf(
                    "MODIS MOD10A1 satellite data (NASA)",
                    "Landsat 5, 8 and 9 imagery (USGS)",
                    "Processing in Google Earth Engine",
                    "Random Forest model (R² = ${state.rfR2})",
                    "Hybrid prediction: RF + Linear Regression"
                ).forEach {
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text("• ", fontSize = 12.sp, color = Color(0xFF2563EB))
                        Text(it, fontSize = 12.sp, color = Color(0xFF374151))
                    }
                }
            }
        }
    }
}

@Composable
fun GlacierSelector() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Select a glacier:", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF374151))
            Spacer(modifier = Modifier.height(12.dp))
            val chunked = glacierList.chunked(2)
            chunked.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    row.forEach { glacier ->
                        val isSelected = glacier.id == "mer-de-glace"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF2563EB) else Color(0xFFE5E7EB),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .background(if (isSelected) Color(0xFFEFF6FF) else Color.White)
                                .alpha(if (isSelected) 1f else 0.5f)
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Landscape, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(glacier.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(glacier.country, fontSize = 11.sp, color = Color(0xFF6B7280))
                            }
                        }
                    }
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun MetricCard(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 11.sp, color = Color(0xFF6B7280))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
fun ChartCard(
    title: String,
    subtitle: String,
    warningText: String,
    warningColor: Color,
    warningTextColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
            Text(subtitle, fontSize = 12.sp, color = Color(0xFF6B7280))
            Spacer(modifier = Modifier.height(12.dp))
            content()
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = warningColor)
            ) {
                Text(text = warningText, fontSize = 12.sp, color = warningTextColor, modifier = Modifier.padding(10.dp))
            }
        }
    }
}

@Composable
fun SimpleLineChart(data: List<Float>, labels: List<String>, lineColor: Color, fillColor: Color) {
    val maxVal = if (data.isEmpty()) 1f else data.max()
    val minVal = if (data.isEmpty()) 0f else data.min().coerceAtMost(0f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(start = 8.dp, end = 8.dp, bottom = 24.dp)
    ) {
        val w = size.width
        val h = size.height
        val range = if (maxVal - minVal == 0f) 1f else maxVal - minVal
        val stepX = if (data.size > 1) w / (data.size - 1).toFloat() else w

        fun xOf(i: Int) = i * stepX
        fun yOf(v: Float) = h - ((v - minVal) / range) * h

        repeat(4) { i -> drawLine(Color(0xFFE5E7EB), Offset(0f, h * i / 3f), Offset(w, h * i / 3f), strokeWidth = 1f) }

        val fillPath = Path().apply {
            moveTo(xOf(0), h)
            data.forEachIndexed { i, v -> lineTo(xOf(i), yOf(v)) }
            lineTo(xOf(data.size - 1), h)
            close()
        }
        drawPath(fillPath, fillColor)

        val linePath = Path().apply {
            data.forEachIndexed { i, v ->
                if (i == 0) moveTo(xOf(i), yOf(v)) else lineTo(xOf(i), yOf(v))
            }
        }
        drawPath(linePath, lineColor, style = Stroke(width = 3f))

        data.forEachIndexed { i, v ->
            drawCircle(lineColor, 5f, Offset(xOf(i), yOf(v)))
            drawCircle(Color.White, 3f, Offset(xOf(i), yOf(v)))
        }

        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#6B7280")
            textSize = 24f
            textAlign = android.graphics.Paint.Align.CENTER
        }
        labels.forEachIndexed { i, label ->
            // Muestra solo cada 4 años para no amontonar
            if (i % 4 == 0) {
                drawContext.canvas.nativeCanvas.drawText(label, xOf(i), h + 20f, paint)
            }
        }
    }
}

@Composable
fun DualLineChart(data1: List<Float>, data2: List<Float>, labels: List<String>, color1: Color, color2: Color) {
    val max1 = if (data1.isEmpty()) 1f else data1.max()
    val min1 = if (data1.isEmpty()) 0f else data1.min().coerceAtMost(0f)
    val max2 = if (data2.isEmpty()) 1f else data2.max()
    val min2 = if (data2.isEmpty()) 0f else data2.min().coerceAtMost(0f)

    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(bottom = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).background(color1, RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Volume (km³)", fontSize = 11.sp, color = Color(0xFF6B7280))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).background(color2, RoundedCornerShape(2.dp)))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Temperature (°C)", fontSize = 11.sp, color = Color(0xFF6B7280))
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(start = 8.dp, end = 8.dp, bottom = 24.dp)
        ) {
            val w = size.width
            val h = size.height
            val stepX = if (data1.size > 1) w / (data1.size - 1).toFloat() else w

            fun xOf(i: Int) = i * stepX
            fun yOf(v: Float, min: Float, max: Float): Float {
                val range = if (max - min == 0f) 1f else max - min
                return h - ((v - min) / range) * h
            }

            repeat(4) { i -> drawLine(Color(0xFFE5E7EB), Offset(0f, h * i / 3f), Offset(w, h * i / 3f), strokeWidth = 1f) }

            val path1 = Path().apply {
                data1.forEachIndexed { i, v ->
                    if (i == 0) moveTo(xOf(i), yOf(v, min1, max1)) else lineTo(xOf(i), yOf(v, min1, max1))
                }
            }
            drawPath(path1, color1, style = Stroke(width = 3f))
            data1.forEachIndexed { i, v ->
                drawCircle(color1, 5f, Offset(xOf(i), yOf(v, min1, max1)))
                drawCircle(Color.White, 3f, Offset(xOf(i), yOf(v, min1, max1)))
            }

            val path2 = Path().apply {
                data2.forEachIndexed { i, v ->
                    if (i == 0) moveTo(xOf(i), yOf(v, min2, max2)) else lineTo(xOf(i), yOf(v, min2, max2))
                }
            }
            drawPath(path2, color2, style = Stroke(width = 3f))
            data2.forEachIndexed { i, v ->
                drawCircle(color2, 5f, Offset(xOf(i), yOf(v, min2, max2)))
                drawCircle(Color.White, 3f, Offset(xOf(i), yOf(v, min2, max2)))
            }

            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#6B7280")
                textSize = 24f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            labels.forEachIndexed { i, label ->
                if (i % 4 == 0) {
                    drawContext.canvas.nativeCanvas.drawText(label, xOf(i), h + 20f, paint)
                }
            }
        }
    }
}