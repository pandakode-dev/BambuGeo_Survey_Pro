package com.pandakode.bambugeo.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandakode.bambugeo.ui.theme.*
import kotlin.math.*

@Composable
fun ClinometerCompassScreen() {
    val context = LocalContext.current
    var azimuth by remember { mutableFloatStateOf(0f) }
    var pitch by remember { mutableFloatStateOf(0f) } // Kemiringan Depan-Belakang
    var roll by remember { mutableFloatStateOf(0f) }  // Kemiringan Kiri-Kanan
    var isHoldLocked by remember { mutableStateOf(false) }

    DisposableEffect(isHoldLocked) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (isHoldLocked) return

                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    System.arraycopy(event.values, 0, gravity, 0, 3)
                }
                if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                }

                val R = FloatArray(9)
                val I = FloatArray(9)
                if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(R, orientation)
                    val azDeg = (Math.toDegrees(orientation[0].toDouble()).toFloat() + 360) % 360
                    val pitchDeg = Math.toDegrees(orientation[1].toDouble()).toFloat()
                    val rollDeg = Math.toDegrees(orientation[2].toDouble()).toFloat()

                    azimuth = azDeg
                    pitch = pitchDeg
                    roll = rollDeg
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, accelSensor, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(listener, magSensor, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    // Perhitungan Geologi & Teknik Sipil
    val totalDipAngle = sqrt(pitch * pitch + roll * roll).coerceAtMost(90f)
    val slopePercent = tan(Math.toRadians(totalDipAngle.toDouble())) * 100.0
    val strikeAngle = (azimuth + 90f) % 360f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LimestoneBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. HEADER DATA GEOLOGI (STRIKE / DIP & AZIMUTH)
        Card(
            colors = CardDefaults.cardColors(containerColor = TerraForestDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("KOMPAS GEOLOGI BRUNTON MODE", color = SafetyAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format("Azimuth: %03.0f° (%s)", azimuth, getCardinalDirection(azimuth)),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = String.format("Notasi Geologi: N %03.0f° E / %02.0f°", strikeAngle, totalDipAngle),
                        color = TerraMintLight,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }

                // Tombol HOLD / Kunci Bacaan Saat Mengukur Singkapan Batuan
                FilledTonalButton(
                    onClick = { isHoldLocked = !isHoldLocked },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (isHoldLocked) SafetyOrangeDark else SafetyAmber
                    )
                ) {
                    Icon(
                        imageVector = if (isHoldLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = BasaltSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHoldLocked) "LOCKED" else "HOLD",
                        color = BasaltSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        // 2. DIAL KOMPAS & WATERPASS BUBBLE LEVEL (CANVAS)
        Card(
            colors = CardDefaults.cardColors(containerColor = BasaltSurface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                GeologicalCompassCanvas(azimuth = azimuth, pitch = pitch, roll = roll)
            }
        }

        // 3. KLINOMETER KEMIRINGAN LERENG & WATERPASS SIPIL
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "KLINOMETER LERENG & WATERPASS SIPIL",
                    fontWeight = FontWeight.ExtraBold,
                    color = TerraForestDark,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricBox(
                        title = "SUDUT MIRING (DIP)",
                        value = String.format("%.1f°", totalDipAngle),
                        accentColor = TerraForestDark,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "GRADE LERENG (%)",
                        value = String.format("%.1f %%", slopePercent.coerceAtMost(999.0)),
                        accentColor = SafetyOrangeDark,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricBox(
                        title = "PITCH (Depan/Blk)",
                        value = String.format("%+.1f°", pitch),
                        accentColor = BlueprintCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "ROLL (Kiri/Kanan)",
                        value = String.format("%+.1f°", roll),
                        accentColor = TerraForestMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun GeologicalCompassCanvas(azimuth: Float, pitch: Float, roll: Float) {
    Canvas(modifier = Modifier.size(260.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 - 16f

        // Lingkaran Luar Theodolite
        drawCircle(color = TerraForestMedium, radius = radius, center = center, style = Stroke(width = 8f))
        drawCircle(color = SafetyAmber.copy(alpha = 0.3f), radius = radius - 14f, center = center, style = Stroke(width = 2f))

        // Garis Bidik Crosshair
        drawLine(Color.DarkGray, Offset(center.x - radius, center.y), Offset(center.x + radius, center.y), strokeWidth = 2f)
        drawLine(Color.DarkGray, Offset(center.x, center.y - radius), Offset(center.x, center.y + radius), strokeWidth = 2f)

        // Putaran Jarum Kompas mengikuti Azimuth
        rotate(degrees = -azimuth, pivot = center) {
            // Tick Marks Derajat setiap 15 derajat
            for (angle in 0 until 360 step 15) {
                rotate(degrees = angle.toFloat(), pivot = center) {
                    val tickLength = if (angle % 90 == 0) 24f else 12f
                    val tickColor = if (angle == 0) SafetyOrangeDark else Color.LightGray
                    drawLine(
                        color = tickColor,
                        start = Offset(center.x, center.y - radius + 4f),
                        end = Offset(center.x, center.y - radius + 4f + tickLength),
                        strokeWidth = if (angle % 90 == 0) 5f else 2f
                    )
                }
            }

            // Jarum Utara (Merah/Amber)
            drawLine(
                color = SafetyOrangeDark,
                start = center,
                end = Offset(center.x, center.y - radius + 35f),
                strokeWidth = 10f
            )
            // Jarum Selatan (Putih)
            drawLine(
                color = Color.White,
                start = center,
                end = Offset(center.x, center.y + radius - 35f),
                strokeWidth = 6f
            )
        }

        // Lingkaran Target Waterpass di Tengah
        val bullseyeRadius = 45f
        drawCircle(color = BlueprintCyan, radius = bullseyeRadius, center = center, style = Stroke(width = 3f))

        // Posisi Gelembung Waterpass (Bubble Level) berdasarkan Roll & Pitch
        val maxTilt = 45f
        val bubbleX = (center.x + (roll / maxTilt) * bullseyeRadius).coerceIn(center.x - bullseyeRadius, center.x + bullseyeRadius)
        val bubbleY = (center.y + (pitch / maxTilt) * bullseyeRadius).coerceIn(center.y - bullseyeRadius, center.y + bullseyeRadius)

        val isLevel = abs(pitch) < 2.0f && abs(roll) < 2.0f
        drawCircle(
            color = if (isLevel) Color(0xFF00E676) else SafetyAmber,
            radius = 14f,
            center = Offset(bubbleX, bubbleY)
        )
    }
}

private fun getCardinalDirection(azimuth: Float): String {
    return when {
        azimuth >= 337.5 || azimuth < 22.5 -> "UTARA (N)"
        azimuth >= 22.5 && azimuth < 67.5 -> "TIMUR LAUT (NE)"
        azimuth >= 67.5 && azimuth < 112.5 -> "TIMUR (E)"
        azimuth >= 112.5 && azimuth < 157.5 -> "TENGGARA (SE)"
        azimuth >= 157.5 && azimuth < 202.5 -> "SELATAN (S)"
        azimuth >= 202.5 && azimuth < 247.5 -> "BARAT DAYA (SW)"
        azimuth >= 247.5 && azimuth < 292.5 -> "BARAT (W)"
        else -> "BARAT LAUT (NW)"
    }
}