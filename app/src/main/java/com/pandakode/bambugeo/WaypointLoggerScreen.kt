package com.pandakode.bambugeo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandakode.bambugeo.model.GeoMathEngine
import com.pandakode.bambugeo.model.WaypointItem
import com.pandakode.bambugeo.ui.theme.*
import com.pandakode.bambugeo.utils.GeoExportUtils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaypointLoggerScreen() {
    val context = LocalContext.current

    // Data awal contoh di sekitar Pekanbaru, Riau
    var waypoints by remember {
        mutableStateOf(
            listOf(
                WaypointItem(
                    id = 1,
                    name = "BM-01 Patok Utama Utara",
                    category = "Patok BPN",
                    latitude = 0.507068,
                    longitude = 101.447779,
                    altitude = 31.5,
                    note = "Patok beton batas lahan Jl. Sudirman",
                    timestamp = "10:15 WIB"
                ),
                WaypointItem(
                    id = 2,
                    name = "Singkapan Batupasir Formasi Minas",
                    category = "Geologi",
                    latitude = 0.512410,
                    longitude = 101.451200,
                    altitude = 42.0,
                    note = "Strike/Dip N 125° E / 18°",
                    timestamp = "11:02 WIB"
                )
            )
        )
    }

    var wpName by remember { mutableStateOf("") }
    var wpCategory by remember { mutableStateOf("Patok BPN") }
    var wpLat by remember { mutableStateOf("0.507068") }
    var wpLon by remember { mutableStateOf("101.447779") }
    var wpAlt by remember { mutableStateOf("30.0") }
    var wpNote by remember { mutableStateOf("") }

    val categories = listOf("Patok BPN", "Geologi", "Konstruksi Sipil", "Drainase / Air", "Perkebunan")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LimestoneBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. FORMULIR LOGGING WAYPOINT BARU
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "CATAT TITIK KOORDINAT (WAYPOINT LOGGER)",
                        fontWeight = FontWeight.ExtraBold,
                        color = TerraForestDark,
                        fontSize = 14.sp
                    )

                    OutlinedTextField(
                        value = wpName,
                        onValueChange = { wpName = it },
                        label = { Text("Nama Titik / Benchmark (Misal: BM-02)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Pilihan Kategori
                    Text("Kategori Titik:", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.take(3).forEach { cat ->
                            FilterChip(
                                selected = wpCategory == cat,
                                onClick = { wpCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = wpLat,
                            onValueChange = { wpLat = it },
                            label = { Text("Latitude") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = wpLon,
                            onValueChange = { wpLon = it },
                            label = { Text("Longitude") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = wpAlt,
                            onValueChange = { wpAlt = it },
                            label = { Text("Elevasi (mdpl)") },
                            singleLine = true,
                            modifier = Modifier.weight(0.5f)
                        )
                        OutlinedTextField(
                            value = wpNote,
                            onValueChange = { wpNote = it },
                            label = { Text("Catatan Lapangan") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Button(
                        onClick = {
                            val latD = wpLat.toDoubleOrNull() ?: 0.507068
                            val lonD = wpLon.toDoubleOrNull() ?: 101.447779
                            val altD = wpAlt.toDoubleOrNull() ?: 25.0
                            val timeStr = SimpleDateFormat("HH:mm 'WIB'", Locale.getDefault()).format(Date())
                            val finalName = if (wpName.isBlank()) "WP-0${waypoints.size + 1}" else wpName

                            waypoints = listOf(
                                WaypointItem(
                                    name = finalName,
                                    category = wpCategory,
                                    latitude = latD,
                                    longitude = lonD,
                                    altitude = altD,
                                    note = if (wpNote.isBlank()) "Titik survei lapangan" else wpNote,
                                    timestamp = timeStr
                                )
                            ) + waypoints
                            wpName = ""
                            wpNote = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TerraForestDark),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = SafetyAmber)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan Waypoint ke Database Lokal", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. TOMBOL EKSPOR GIS (.GPX & .KML)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AmberSoftBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "EKSPOR DATA GIS STANDAR INTERNASIONAL",
                        color = SafetyOrangeDark,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Buka langsung di QGIS, ArcGIS, Garmin BaseCamp (.GPX), atau Google Earth Pro (.KML)",
                        color = SlateText,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { GeoExportUtils.exportToGPX(context, waypoints) },
                            colors = ButtonDefaults.buttonColors(containerColor = SafetyOrangeDark),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export .GPX", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { GeoExportUtils.exportToKML(context, waypoints) },
                            colors = ButtonDefaults.buttonColors(containerColor = BlueprintCyan),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Public, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export .KML", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. DAFTAR WAYPOINT TERSIMPAN + FORMAT DMS
        item {
            Text(
                text = "LOG WAYPOINT TERSIMPAN (${waypoints.size})",
                fontWeight = FontWeight.Bold,
                color = TerraForestDark,
                fontSize = 13.sp
            )
        }

        items(waypoints, key = { it.id }) { wp ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(wp.name, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = TerraForestDark)
                            Text("${wp.category} • ${wp.timestamp}", fontSize = 11.sp, color = SafetyOrangeDark, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { waypoints = waypoints.filter { it.id != wp.id } }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = Color.Gray)
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                    Text(
                        text = "DMS: ${GeoMathEngine.toDMS(wp.latitude, true)} | ${GeoMathEngine.toDMS(wp.longitude, false)}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = BlueprintCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = String.format("Dec: %.6f, %.6f | Elev: %.1f mdpl", wp.latitude, wp.longitude, wp.altitude),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Catatan: ${wp.note}", fontSize = 12.sp, color = Color.DarkGray)
                }
            }
        }
    }
}