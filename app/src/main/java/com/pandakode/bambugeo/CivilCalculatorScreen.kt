package com.pandakode.bambugeo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandakode.bambugeo.ui.theme.*
import kotlin.math.ceil
import kotlin.math.cos

@Composable
fun CivilCalculatorScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Beton", "Bata", "Pondasi", "Besi SNI", "Atap")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LimestoneBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = TerraForestDark,
            contentColor = SafetyAmber,
            edgePadding = 8.dp
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (selectedTab == index) SafetyAmber else Color.White
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> ConcreteCalculatorCard()
            1 -> BrickWallCalculatorCard()
            2 -> FoundationCalculatorCard()
            3 -> RebarWeightCalculatorCard()
            4 -> RoofTrussCalculatorCard()
        }
    }
}

@Composable
fun ConcreteCalculatorCard() {
    var length by remember { mutableStateOf("10.0") }
    var width by remember { mutableStateOf("4.0") }
    var thickness by remember { mutableStateOf("0.15") }

    val p = length.toDoubleOrNull() ?: 0.0
    val l = width.toDoubleOrNull() ?: 0.0
    val t = thickness.toDoubleOrNull() ?: 0.0

    val volumeM3 = p * l * t
    val cementSacks = ceil(volumeM3 * 7.4).toInt()
    val sandM3 = volumeM3 * 0.54
    val gravelM3 = volumeM3 * 0.81

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row {
                Icon(Icons.Default.Construction, contentDescription = null, tint = SafetyOrangeDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ESTIMATOR COR BETON (MUTU K-225 / 1:2:3)",
                    fontWeight = FontWeight.ExtraBold,
                    color = TerraForestDark,
                    fontSize = 14.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = length, onValueChange = { length = it }, label = { Text("Panjang (m)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = width, onValueChange = { width = it }, label = { Text("Lebar (m)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = thickness, onValueChange = { thickness = it }, label = { Text("Tebal (m)") }, modifier = Modifier.weight(1f))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
            MetricBox("VOLUME BETON BASAH", String.format("%.2f m³", volumeM3), TerraForestDark, Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricBox("SEMEN (50 kg)", "$cementSacks Sak", SafetyOrangeDark, Modifier.weight(1f))
                MetricBox("PASIR BETON", String.format("%.2f m³", sandM3), BlueprintCyan, Modifier.weight(1f))
                MetricBox("BATU SPLIT", String.format("%.2f m³", gravelM3), TerraForestMedium, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun BrickWallCalculatorCard() {
    var wallLength by remember { mutableStateOf("12.0") }
    var wallHeight by remember { mutableStateOf("3.5") }

    val p = wallLength.toDoubleOrNull() ?: 0.0
    val t = wallHeight.toDoubleOrNull() ?: 0.0
    val areaM2 = p * t

    val redBricks = ceil(areaM2 * 70).toInt()
    val hebelBlocks = ceil(areaM2 * 8.5).toInt()
    val cementMortarSacks = ceil(areaM2 * 0.25).toInt()
    val sandMortarM3 = areaM2 * 0.045

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row {
                Icon(Icons.Default.Architecture, contentDescription = null, tint = SafetyOrangeDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text("KALKULATOR DINDING BATA MERAH & HEBEL", fontWeight = FontWeight.ExtraBold, color = TerraForestDark, fontSize = 14.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = wallLength, onValueChange = { wallLength = it }, label = { Text("Panjang Dinding (m)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = wallHeight, onValueChange = { wallHeight = it }, label = { Text("Tinggi Dinding (m)") }, modifier = Modifier.weight(1f))
            }

            MetricBox("LUAS BIDANG DINDING", String.format("%.2f m²", areaM2), TerraForestDark, Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricBox("OPSI BATA MERAH", "$redBricks Biji", SafetyOrangeDark, Modifier.weight(1f))
                MetricBox("OPSI HEBEL (10cm)", "$hebelBlocks Buah", BlueprintCyan, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricBox("SEMEN PASANGAN", "$cementMortarSacks Sak", TerraForestMedium, Modifier.weight(1f))
                MetricBox("PASIR PASANG", String.format("%.2f m³", sandMortarM3), TerraForestDark, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun FoundationCalculatorCard() {
    var topWidth by remember { mutableStateOf("0.30") }
    var bottomWidth by remember { mutableStateOf("0.70") }
    var height by remember { mutableStateOf("0.80") }
    var totalLength by remember { mutableStateOf("25.0") }

    val a = topWidth.toDoubleOrNull() ?: 0.0
    val b = bottomWidth.toDoubleOrNull() ?: 0.0
    val h = height.toDoubleOrNull() ?: 0.0
    val l = totalLength.toDoubleOrNull() ?: 0.0

    val volM3 = ((a + b) / 2.0) * h * l
    val riverStoneM3 = volM3 * 1.2
    val cementSacks = ceil(volM3 * 3.26).toInt()
    val dumpTrucks = ceil(riverStoneM3 / 6.0).toInt()

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row {
                Icon(Icons.Default.Foundation, contentDescription = null, tint = SafetyOrangeDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text("PONDASI BATU KALI (TRAPESIUM)", fontWeight = FontWeight.ExtraBold, color = TerraForestDark, fontSize = 14.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = topWidth, onValueChange = { topWidth = it }, label = { Text("Lebar Atas (m)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = bottomWidth, onValueChange = { bottomWidth = it }, label = { Text("Lebar Bawah (m)") }, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Tinggi Pondasi (m)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = totalLength, onValueChange = { totalLength = it }, label = { Text("Panjang Total (m)") }, modifier = Modifier.weight(1f))
            }

            MetricBox("VOLUME PASANGAN PONDASI", String.format("%.2f m³", volM3), TerraForestDark, Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricBox("BATU KALI BELAH", String.format("%.1f m³ (~%d Truk)", riverStoneM3, dumpTrucks), SafetyOrangeDark, Modifier.weight(1f))
                MetricBox("SEMEN (50 kg)", "$cementSacks Sak", BlueprintCyan, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun RebarWeightCalculatorCard() {
    var diameterMm by remember { mutableStateOf("12.0") }
    var totalNeedMeters by remember { mutableStateOf("180.0") }

    val d = diameterMm.toDoubleOrNull() ?: 0.0
    val totalLen = totalNeedMeters.toDoubleOrNull() ?: 0.0

    // Rumus SNI Berat Besi Beton: W = 0.006165 * d^2 (kg/meter)
    val weightPerMeter = 0.006165 * d * d
    val weightPerBar12m = weightPerMeter * 12.0
    val totalBars12m = ceil(totalLen / 12.0).toInt()
    val totalWeightKg = totalLen * weightPerMeter

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row {
                Icon(Icons.Default.GridOn, contentDescription = null, tint = SafetyOrangeDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text("KALKULATOR BESI BETON (SNI REBAR WEIGHT)", fontWeight = FontWeight.ExtraBold, color = TerraForestDark, fontSize = 14.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = diameterMm,
                    onValueChange = { diameterMm = it },
                    label = { Text("Diameter Besi (mm)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = totalNeedMeters,
                    onValueChange = { totalNeedMeters = it },
                    label = { Text("Total Panjang (m)") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricBox("BERAT / METER", String.format("%.2f kg/m", weightPerMeter), TerraForestMedium, Modifier.weight(1f))
                MetricBox("BERAT / BATANG (12m)", String.format("%.2f kg/btg", weightPerBar12m), BlueprintCyan, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricBox("KEBUTUHAN LONJOR", "$totalBars12m Batang (12m)", SafetyOrangeDark, Modifier.weight(1f))
                MetricBox("TOTAL TONASE BESI", String.format("%,.1f kg", totalWeightKg), TerraForestDark, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun RoofTrussCalculatorCard() {
    var buildingLength by remember { mutableStateOf("12.0") }
    var buildingWidth by remember { mutableStateOf("8.0") }
    var overhangMeters by remember { mutableStateOf("0.8") } // Overstek keliling
    var roofPitchDeg by remember { mutableStateOf("30.0") }

    val p = (buildingLength.toDoubleOrNull() ?: 0.0) + 2 * (overhangMeters.toDoubleOrNull() ?: 0.0)
    val l = (buildingWidth.toDoubleOrNull() ?: 0.0) + 2 * (overhangMeters.toDoubleOrNull() ?: 0.0)
    val deg = (roofPitchDeg.toDoubleOrNull() ?: 30.0).coerceIn(5.0, 65.0)

    val flatArea = p * l
    val slopedAreaM2 = flatArea / cos(Math.toRadians(deg))
    val canalC75Bars = ceil((slopedAreaM2 * 4.0) / 6.0).toInt() // Kanal C75 panjang 6m
    val rengBars = ceil((slopedAreaM2 * 3.0) / 6.0).toInt()
    val metalRoofSheets = ceil(slopedAreaM2 * 1.62).toInt() // Genteng metal 2x4 daun (~0.62 m2 efektif)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row {
                Icon(Icons.Default.Roofing, contentDescription = null, tint = SafetyOrangeDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text("KALKULATOR RANGKA ATAP BAJA RINGAN", fontWeight = FontWeight.ExtraBold, color = TerraForestDark, fontSize = 14.sp)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = buildingLength, onValueChange = { buildingLength = it }, label = { Text("Panjang Bangunan (m)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = buildingWidth, onValueChange = { buildingWidth = it }, label = { Text("Lebar Bangunan (m)") }, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = overhangMeters, onValueChange = { overhangMeters = it }, label = { Text("Overstek / Tritisan (m)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = roofPitchDeg, onValueChange = { roofPitchDeg = it }, label = { Text("Sudut Kemiringan (°)") }, modifier = Modifier.weight(1f))
            }

            MetricBox("LUAS MIRING ATAP EFEKTIF", String.format("%.1f m²", slopedAreaM2), TerraForestDark, Modifier.fillMaxWidth())

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricBox("KANAL C75 (6m)", "$canalC75Bars Batang", SafetyOrangeDark, Modifier.weight(1f))
                MetricBox("RENG BAJA (6m)", "$rengBars Batang", BlueprintCyan, Modifier.weight(1f))
            }
            MetricBox("GENTENG METAL (2x4 Daun)", "$metalRoofSheets Lembar", TerraForestMedium, Modifier.fillMaxWidth())
        }
    }
}