package com.pandakode.bambugeo
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.CircleShape
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandakode.bambugeo.ui.theme.*
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BambuGeoTheme {
                SplashScreenContent(
                    onTimeout = {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun SplashScreenContent(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2400L) // Tampil selama 2.4 detik
        onTimeout()
    }

    // Animasi Radar Berputar & Denyut Halus
    val infiniteTransition = rememberInfiniteTransition(label = "splash_anim")
    val radarRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BasaltSurface,
                        TerraForestDark,
                        BasaltSurface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // 1. LOGO VECTOR CANVAS (THEODOLITE + POLYGON + BAMBOO)
            Image(
                painter = painterResource(id = R.drawable.bambugeo_logo),
                contentDescription = "Logo Panda Geo Survey Pro",
                modifier = Modifier
                    .size(150.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 2. NAMA APLIKASI & SUBTITLE
            Text(
                text = "BambuGeo",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            Text(
                text = "SURVEY & CIVIL ENGINEERING PRO",
                color = SafetyAmber,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Badge Fitur Offline
            Surface(
                color = TerraForestMedium.copy(alpha = 0.6f),
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "GPS Polygon • Brunton Compass • GPX/KML • SNI Calc",
                    color = TerraMintLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Loading Indicator
            LinearProgressIndicator(
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = SafetyAmber,
                trackColor = TerraForestMedium
            )
        }

        // 3. FOOTER BRANDING PANDAKODE
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crafted by PandaKode",
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = "Muhammad Saputra • Pekanbaru, Indonesia",
                color = TerraMintLight.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun BambuGeoLogoCanvas(rotationDegrees: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r = size.minDimension / 2f - 10f

        // Lingkaran Dasar Gelap Berbingkai Hijau Topografi
        drawCircle(
            color = BasaltSurface,
            radius = r,
            center = Offset(cx, cy)
        )
        drawCircle(
            color = TerraForestMedium,
            radius = r,
            center = Offset(cx, cy),
            style = Stroke(width = 6f)
        )

        // Ring Derajat Theodolite yang Berputar Halus
        rotate(degrees = rotationDegrees, pivot = Offset(cx, cy)) {
            drawCircle(
                color = BlueprintCyan.copy(alpha = 0.55f),
                radius = r - 14f,
                center = Offset(cx, cy),
                style = Stroke(width = 3f)
            )
            for (angle in 0 until 360 step 30) {
                rotate(degrees = angle.toFloat(), pivot = Offset(cx, cy)) {
                    drawLine(
                        color = if (angle % 90 == 0) SafetyAmber else BlueprintLight.copy(alpha = 0.5f),
                        start = Offset(cx, cy - r + 14f),
                        end = Offset(cx, cy - r + 26f),
                        strokeWidth = if (angle % 90 == 0) 5f else 2.5f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // Garis Bidik Crosshair Theodolite
        drawLine(
            color = TerraMintLight.copy(alpha = 0.25f),
            start = Offset(cx - r + 25f, cy),
            end = Offset(cx + r - 25f, cy),
            strokeWidth = 2f
        )
        drawLine(
            color = TerraMintLight.copy(alpha = 0.25f),
            start = Offset(cx, cy - r + 25f),
            end = Offset(cx, cy + r - 25f),
            strokeWidth = 2f
        )

        // Bentuk Poligon Lahan Survei (Pentagon Asimetris Amber)
        val polyPath = Path().apply {
            moveTo(cx, cy - r * 0.55f)
            lineTo(cx + r * 0.52f, cy - r * 0.15f)
            lineTo(cx + r * 0.35f, cy + r * 0.48f)
            lineTo(cx - r * 0.40f, cy + r * 0.42f)
            lineTo(cx - r * 0.50f, cy - r * 0.20f)
            close()
        }
        drawPath(polyPath, color = SafetyAmber.copy(alpha = 0.20f))
        drawPath(polyPath, color = SafetyAmber, style = Stroke(width = 4.5f))

        // Ikon Batang Bambu Emas di Pusat Bidikan (Identitas PandaKode)
        val bambooWidth = 14f
        val segmentHeight = 22f
        // Ruas Atas
        drawRoundRect(
            color = TerraMintLight,
            topLeft = Offset(cx - bambooWidth / 2f, cy - segmentHeight - 3f),
            size = Size(bambooWidth, segmentHeight),
            cornerRadius = CornerRadius(4f, 4f)
        )
        // Ruas Bawah
        drawRoundRect(
            color = TerraMintLight,
            topLeft = Offset(cx - bambooWidth / 2f, cy + 3f),
            size = Size(bambooWidth, segmentHeight),
            cornerRadius = CornerRadius(4f, 4f)
        )
        // Titik Laser Bidik Tengah (Merah-Oranye)
        drawCircle(
            color = SafetyOrangeDark,
            radius = 6f,
            center = Offset(cx, cy)
        )
    }
}