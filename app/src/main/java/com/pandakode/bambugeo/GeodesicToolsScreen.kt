package com.pandakode.bambugeo.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandakode.bambugeo.model.GeoMathEngine
import com.pandakode.bambugeo.ui.theme.*
import kotlin.math.abs
import kotlin.math.ceil

@Composable
fun GeodesicToolsScreen() {
    var selectedTool by remember { mutableIntStateOf(0) }
    val toolTabs = listOf("UTM & DMS", "COGO Bidik", "Cut & Fill Tanah")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LimestoneBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTool,
            containerColor = TerraForestDark,
            contentColor = SafetyAmber
        ) {
            toolTabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTool == idx,
                    onClick = { selectedTool = idx },
                    text = {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (selectedTool == idx) SafetyAmber else Color.White
                        )
                    }
                )
            }
        }

        when (selectedTool) {
            0 -> UtmConverterCard()
            1 -> CogoSurveyCard()
            2 -> CutAndFillContourCard()
        }
    }
}

@Composable
fun UtmConverterCard() {
    // Default koordinat Pekanbaru, Riau (Zona UTM 47N)
    var latInput by remember { mutableStateOf("0.507068") }
    var lonInput by remember { mutableStateOf("101.447779") }

    val lat = latInput.toDoubleOrNull() ?: 0.0
    val lon = lonInput.toDoubleOrNull() ?: 0.0
    val utm = GeoMathEngine.latLonToUTM(lat, lon)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row {
                Icon(Icons.Default.MyLocation, contentDescription = null, tint = SafetyOrangeDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "KONVERTER KOORDINAT WGS84 ➔ UTM & DMS",
                    fontWeight = FontWeight.ExtraBold,
                    color = TerraForestDark,
                    fontSize = 14.sp
                )
            }
            Text(
                text = "Standar Peta BPN, Kehutanan (KLHK), dan Konsesi Tambang/Kebun.",
                fontSize = 11.sp,
                color = Color.Gray
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = latInput,
                    onValueChange = { latInput = it },
                    label = { Text("Latitude (Desimal)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = lonInput,
                    onValueChange = { lonInput = it },
                    label = { Text("Longitude (Desimal)") },
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            MetricBox(
                title = "ZONA PROYEKSI UTM",
                value = "Zone ${utm.zoneNumber}${utm.zoneLetter} — ${utm.hemisphere}",
                accentColor = TerraForestDark,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricBox(
                    title = "EASTING (X)",
                    value = String.format("%,.2f mE", utm.easting),
                    accentColor = SafetyOrangeDark,
                    modifier = Modifier.weight(1f)
                )
                MetricBox(
                    title = "NORTHING (Y)",
                    value = String.format("%,.2f mN", utm.northing),
                    accentColor = BlueprintCyan,
                    modifier = Modifier.weight(1f)
                )
            }
            MetricBox(
                title = "FORMAT DMS (DEGREES MINUTES SECONDS)",
                value = "${GeoMathEngine.toDMS(lat, true)}\n${GeoMathEngine.toDMS(lon, false)}",
                accentColor = TerraForestMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CogoSurveyCard() {
    var startLat by remember { mutableStateOf("0.507000") }
    var startLon by remember { mutableStateOf("101.447000") }
    var distanceM by remember { mutableStateOf("150.0") }
    var azimuthDeg by remember { mutableStateOf("45.0") }

    val sLat = startLat.toDoubleOrNull() ?: 0.0
    val sLon = startLon.toDoubleOrNull() ?: 0.0
    val dist = distanceM.toDoubleOrNull() ?: 0.0
    val az = azimuthDeg.toDoubleOrNull() ?: 0.0

    val targetCoord = GeoMathEngine.calculateForwardPoint(sLat, sLon, dist, az)
    val targetUtm = GeoMathEngine.latLonToUTM(targetCoord.first, targetCoord.second)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row {
                Icon(Icons.Default.Calculate, contentDescription = null, tint = SafetyOrangeDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "COGO FORWARD SURVEY (TEMBAK PATOK JAUH)",
                    fontWeight = FontWeight.ExtraBold,
                    color = TerraForestDark,
                    fontSize = 14.sp
                )
            }
            Text(
                text = "Hitung koordinat patok di seberang sungai/jurang menggunakan jarak bidik laser/meteran dan sudut Azimuth kompas.",
                fontSize = 11.sp,
                color = Color.Gray
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = startLat,
                    onValueChange = { startLat = it },
                    label = { Text("Lat Titik Berdiri") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = startLon,
                    onValueChange = { startLon = it },
                    label = { Text("Lon Titik Berdiri") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = distanceM,
                    onValueChange = { distanceM = it },
                    label = { Text("Jarak Bidik (Meter)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = azimuthDeg,
                    onValueChange = { azimuthDeg = it },
                    label = { Text("Sudut Azimuth (°)") },
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text("KOORDINAT PATOK TARGET HASIL BIDIKAN:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TerraForestDark)
            MetricBox(
                title = "KOORDINAT TARGET (WGS84)",
                value = String.format("Lat: %.6f\nLon: %.6f", targetCoord.first, targetCoord.second),
                accentColor = SafetyOrangeDark,
                modifier = Modifier.fillMaxWidth()
            )
            MetricBox(
                title = "KOORDINAT TARGET (UTM ${targetUtm.zoneNumber}${targetUtm.zoneLetter})",
                value = String.format("%.2f mE | %.2f mN", targetUtm.easting, targetUtm.northing),
                accentColor = BlueprintCyan,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CutAndFillContourCard() {
    var landAreaM2 by remember { mutableStateOf("400.0") } // 1 Rante
    var existingElevStart by remember { mutableStateOf("34.5") }
    var existingElevEnd by remember { mutableStateOf("29.0") }
    var targetDesignElev by remember { mutableStateOf("31.0") }

    val area = landAreaM2.toDoubleOrNull() ?: 0.0
    val el1 = existingElevStart.toDoubleOrNull() ?: 0.0
    val el2 = existingElevEnd.toDoubleOrNull() ?: 0.0
    val elTarget = targetDesignElev.toDoubleOrNull() ?: 0.0

    val avgExisting = (el1 + el2) / 2.0
    val deltaH = avgExisting - elTarget
    val earthVolumeM3 = abs(deltaH) * area
    val swellVolumeM3 = earthVolumeM3 * 1.25 // Swell factor tanah gembur 25%
    val dumpTruckTrips = ceil(swellVolumeM3 / 7.0).toInt() // 1 Dump truck = 7 m3

    val statusAction = if (deltaH >= 0) "GALIAN (CUT / BUANG TANAH)" else "TIMBUNAN (FILL / URUG TANAH)"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row {
                Icon(Icons.Default.Landscape, contentDescription = null, tint = SafetyOrangeDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CUT & FILL EARTHWORK + PROFIL KONTUR",
                    fontWeight = FontWeight.ExtraBold,
                    color = TerraForestDark,
                    fontSize = 14.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = landAreaM2,
                    onValueChange = { landAreaM2 = it },
                    label = { Text("Luas Lahan (m²)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = targetDesignElev,
                    onValueChange = { targetDesignElev = it },
                    label = { Text("Elevasi Rencana (m)") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = existingElevStart,
                    onValueChange = { existingElevStart = it },
                    label = { Text("Elevasi Titik A (m)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = existingElevEnd,
                    onValueChange = { existingElevEnd = it },
                    label = { Text("Elevasi Titik B (m)") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Visualisasi Penampang Melintang Kontur (Cross-Section Canvas)
            Card(
                colors = CardDefaults.cardColors(containerColor = BasaltSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    ContourCrossSectionCanvas(el1 = el1.toFloat(), el2 = el2.toFloat(), targetEl = elTarget.toFloat())
                    Text(
                        text = "CROSS-SECTION ELEVATION PROFILE",
                        color = BlueprintLight,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            MetricBox("STATUS PEKERJAAN TANAH", statusAction, SafetyOrangeDark, Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricBox("VOLUME PADAT", String.format("%,.1f m³", earthVolumeM3), TerraForestDark, Modifier.weight(1f))
                MetricBox("ESTIMASI RITASE", "$dumpTruckTrips Dump Truck", BlueprintCyan, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ContourCrossSectionCanvas(el1: Float, el2: Float, targetEl: Float) {
    Canvas(modifier = Modifier.fillMaxSize().padding(top = 18.dp, bottom = 8.dp)) {
        val w = size.width
        val h = size.height

        val minEl = minOf(el1, el2, targetEl) - 2f
        val maxEl = maxOf(el1, el2, targetEl) + 2f
        val range = (maxEl - minEl).coerceAtLeast(1f)

        fun toY(elev: Float): Float = h - ((elev - minEl) / range) * h

        val y1 = toY(el1)
        val y2 = toY(el2)
        val yTarget = toY(targetEl)

        // Gambar Area Tanah Eksisting
        val terrainPath = Path().apply {
            moveTo(0f, y1)
            lineTo(w, y2)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(terrainPath, color = TerraForestMedium.copy(alpha = 0.45f))
        drawLine(SafetyAmber, Offset(0f, y1), Offset(w, y2), strokeWidth = 5f)

        // Garis Elevasi Rencana (Design Grade)
        drawLine(BlueprintCyan, Offset(0f, yTarget), Offset(w, yTarget), strokeWidth = 4f)
    }
}