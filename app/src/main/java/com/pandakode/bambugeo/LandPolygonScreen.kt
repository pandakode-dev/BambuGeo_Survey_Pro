package com.pandakode.bambugeo.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.pandakode.bambugeo.model.GeoMathEngine
import com.pandakode.bambugeo.model.GeoPoint
import com.pandakode.bambugeo.ui.theme.*
import com.pandakode.bambugeo.utils.GeoExportUtils
import kotlin.random.Random

@Composable
fun LandPolygonScreen() {
    val context = LocalContext.current
    var points by remember { mutableStateOf(listOf<GeoPoint>()) }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var isGpsActive by remember { mutableStateOf(false) }

    // Perhitungan Otomatis Geodesi & Perkebunan
    val areaSqM = remember(points) { GeoMathEngine.calculatePolygonAreaSqMeters(points) }
    val areaHectare = areaSqM / 10000.0
    val areaRante = areaSqM / 400.0 // 1 Rante Sumatera = 400 m2
    val perimeterM = remember(points) { GeoMathEngine.calculatePerimeter(points) }
    val palmTrees9x9 = remember(areaSqM) { GeoMathEngine.estimatePlantPopulation(areaSqM, 9.0) }

    val locationManager = remember {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    val locationListener = remember {
        object : LocationListener {
            override fun onLocationChanged(loc: Location) {
                currentLocation = loc
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (granted) {
            startLocationUpdates(context, locationManager, locationListener)
            isGpsActive = true
        }
    }

    DisposableEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates(context, locationManager, locationListener)
            isGpsActive = true
        }
        onDispose {
            locationManager.removeUpdates(locationListener)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LimestoneBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. STATUS GPS & PROYEKSI UTM LIVE
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TerraForestDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SatelliteAlt,
                                contentDescription = null,
                                tint = SafetyAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (currentLocation != null) "GPS 3D FIX + UTM PROJECTION" else "STANDBY SINYAL SATELIT GPS...",
                                color = SafetyAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        if (currentLocation != null) {
                            val utm = GeoMathEngine.latLonToUTM(currentLocation!!.latitude, currentLocation!!.longitude)
                            Text(
                                text = String.format("WGS84: %.6f, %.6f", currentLocation!!.latitude, currentLocation!!.longitude),
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                            Text(
                                text = String.format("UTM %d%c: %.0f mE, %.0f mN", utm.zoneNumber, utm.zoneLetter, utm.easting, utm.northing),
                                color = BlueprintLight,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = String.format("Elev: %.1f mdpl | Akurasi: ±%.1f m", currentLocation!!.altitude, currentLocation!!.accuracy),
                                color = TerraMintLight,
                                fontSize = 11.sp
                            )
                        } else {
                            Text(
                                text = "Berjalan 100% Offline. Klik 'Demo 1 Ha' untuk simulasi kebun/lahan di dalam ruangan.",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (!isGpsActive) {
                        Button(
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SafetyAmber)
                        ) {
                            Text("Aktifkan GPS", color = BasaltSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 2. KANVAS BLUEPRINT POLIGON LAHAN
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BasaltSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .border(2.dp, TerraForestMedium, RoundedCornerShape(16.dp))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    PolygonBlueprintCanvas(points = points)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "CAD POLYGON PLOTTER (WGS84 / UTM)",
                            color = BlueprintLight,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${points.size} Patok BPN",
                            color = SafetyAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 3. HASIL PERHITUNGAN LUAS, RANTE, & POPULASI BIBIT SAWIT
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "HASIL UKUR GEODESI & AGRONOMI LAHAN",
                        fontWeight = FontWeight.ExtraBold,
                        color = TerraForestDark,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricBox("LUAS (m²)", String.format("%,.1f m²", areaSqM), TerraForestDark, Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        MetricBox("HEKTAR (Ha)", String.format("%.4f Ha", areaHectare), SafetyOrangeDark, Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricBox("RANTE (Sumatera)", String.format("%.2f Rante", areaRante), BlueprintCyan, Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        MetricBox("KELILING", String.format("%,.1f m", perimeterM), TerraForestMedium, Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Fitur Baru: Estimasi Populasi Sawit Pola Segitiga Mata Lima (9x9m)
                    MetricBox(
                        title = "ESTIMASI POPULASI SAWIT (Pola Mata Lima 9x9m ~ 143 Pkk/Ha)",
                        value = "$palmTrees9x9 Pokok Pohon",
                        accentColor = TerraForestDark,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 4. TOMBOL AKSI SURVEYOR & EKSPOR POLIGON (.KML / .CSV)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val loc = currentLocation
                            if (loc != null) {
                                val newId = points.size + 1
                                points = points + GeoPoint(newId, loc.latitude, loc.longitude, loc.altitude, loc.accuracy)
                            } else {
                                val baseLat = 0.507068
                                val baseLon = 101.447779
                                val offsetLat = (Random.nextDouble() - 0.5) * 0.0012
                                val offsetLon = (Random.nextDouble() - 0.5) * 0.0012
                                val newId = points.size + 1
                                points = points + GeoPoint(newId, baseLat + offsetLat, baseLon + offsetLon, 32.0, 3.5f)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SafetyAmber),
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AddLocationAlt, contentDescription = null, tint = BasaltSurface)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tembak Patok", color = BasaltSurface, fontWeight = FontWeight.ExtraBold)
                    }

                    OutlinedButton(
                        onClick = {
                            val lat0 = 0.507000
                            val lon0 = 101.447000
                            val d = 0.0009 // ~100 meter (1 Ha)
                            points = listOf(
                                GeoPoint(1, lat0, lon0, 28.0, 2.5f, "P1"),
                                GeoPoint(2, lat0 + d, lon0 + 0.0001, 29.2, 2.1f, "P2"),
                                GeoPoint(3, lat0 + d, lon0 + d, 30.5, 2.4f, "P3"),
                                GeoPoint(4, lat0 - 0.0001, lon0 + d, 27.8, 2.8f, "P4")
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AutoGraph, contentDescription = null, tint = TerraForestDark)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Demo 1 Ha", color = TerraForestDark, fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = { points = emptyList() },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFEBEE))
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Reset", tint = Color(0xFFD32F2F))
                    }
                }

                // Baris Tombol Export Poligon ke KML & CSV UTM
                if (points.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { GeoExportUtils.exportPolygonToKML(context, points, areaSqM, perimeterM) },
                            colors = ButtonDefaults.buttonColors(containerColor = BlueprintCyan),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Polygon .KML", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { GeoExportUtils.exportPolygonToCSV(context, points) },
                            colors = ButtonDefaults.buttonColors(containerColor = TerraForestMedium),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tabel UTM .CSV", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 5. DAFTAR KOORDINAT PATOK (WGS84 + UTM + AZIMUTH)
        item {
            Text(
                text = "DAFTAR KOORDINAT PATOK BATAS (${points.size})",
                fontWeight = FontWeight.Bold,
                color = TerraForestDark,
                fontSize = 13.sp
            )
        }

        itemsIndexed(points) { index, pt ->
            val utm = GeoMathEngine.latLonToUTM(pt.latitude, pt.longitude)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(TerraForestDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(pt.label, color = SafetyAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = String.format("Lat: %.6f | Lon: %.6f", pt.latitude, pt.longitude),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = String.format("UTM %d%c: %.1f mE, %.1f mN", utm.zoneNumber, utm.zoneLetter, utm.easting, utm.northing),
                                fontFamily = FontFamily.Monospace,
                                color = BlueprintCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            val nextPt = if (points.size > 1) points[(index + 1) % points.size] else null
                            val distToNext = if (nextPt != null) GeoMathEngine.calculateDistance(pt, nextPt) else 0.0
                            val bearingToNext = if (nextPt != null) GeoMathEngine.calculateBearing(pt.latitude, pt.longitude, nextPt.latitude, nextPt.longitude) else 0.0
                            Text(
                                text = String.format("Ke patok berikutnya: %.1f m (Azimuth: %03.0f°)", distToNext, bearingToNext),
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = {
                        points = points.filterIndexed { i, _ -> i != index }
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Hapus Patok", tint = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricBox(title: String, value: String, accentColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LimestoneBg)
            .padding(12.dp)
    ) {
        Text(text = title, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 16.sp, color = accentColor, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun PolygonBlueprintCanvas(points: List<GeoPoint>) {
    Canvas(modifier = Modifier.fillMaxSize().padding(28.dp)) {
        val w = size.width
        val h = size.height

        val gridCount = 6
        val stepX = w / gridCount
        val stepY = h / gridCount
        val gridColor = Color(0xFF1E3A2F)

        for (i in 0..gridCount) {
            drawLine(gridColor, Offset(i * stepX, 0f), Offset(i * stepX, h), strokeWidth = 1f)
            drawLine(gridColor, Offset(0f, i * stepY), Offset(w, i * stepY), strokeWidth = 1f)
        }

        if (points.isEmpty()) return@Canvas

        val minLat = points.minOf { it.latitude }
        val maxLat = points.maxOf { it.latitude }
        val minLon = points.minOf { it.longitude }
        val maxLon = points.maxOf { it.longitude }

        val latRange = (maxLat - minLat).coerceAtLeast(0.00005)
        val lonRange = (maxLon - minLon).coerceAtLeast(0.00005)

        val screenOffsets = points.map { pt ->
            val x = ((pt.longitude - minLon) / lonRange).toFloat() * (w * 0.8f) + (w * 0.1f)
            val y = h - (((pt.latitude - minLat) / latRange).toFloat() * (h * 0.8f) + (h * 0.1f))
            Offset(x, y)
        }

        if (screenOffsets.size >= 3) {
            val polyPath = Path().apply {
                moveTo(screenOffsets.first().x, screenOffsets.first().y)
                for (i in 1 until screenOffsets.size) {
                    lineTo(screenOffsets[i].x, screenOffsets[i].y)
                }
                close()
            }
            drawPath(polyPath, color = SafetyAmber.copy(alpha = 0.25f))
            drawPath(
                polyPath,
                color = SafetyAmber,
                style = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 8f), 0f))
            )
        } else if (screenOffsets.size == 2) {
            drawLine(SafetyAmber, screenOffsets[0], screenOffsets[1], strokeWidth = 4f)
        }

        screenOffsets.forEach { offset ->
            drawCircle(color = Color.White, radius = 10f, center = offset)
            drawCircle(color = SafetyOrangeDark, radius = 6f, center = offset)
        }
    }
}

@SuppressLint("MissingPermission")
private fun startLocationUpdates(
    context: Context,
    locationManager: LocationManager,
    listener: LocationListener
) {
    try {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1500L, 1f, listener)
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1500L, 1f, listener)
        }
    } catch (_: Exception) {}
}