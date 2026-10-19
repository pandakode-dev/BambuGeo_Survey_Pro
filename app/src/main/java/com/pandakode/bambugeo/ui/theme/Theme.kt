package com.pandakode.bambugeo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Palet Warna: Terra Forest, Safety Theodolite Amber & Blueprint Slate
val TerraForestDark = Color(0xFF1B4332)      // Hijau Geodesi Tua (Header & Primary)
val TerraForestMedium = Color(0xFF2D6A4F)    // Hijau Topografi (Card Accent)
val TerraMintLight = Color(0xFFD8F3DC)       // Hijau Muda Lembut (Chip / Badge)

val SafetyAmber = Color(0xFFFF9F1C)          // Kuning-Oranye Theodolite (Tombol Utama / FAB)
val SafetyOrangeDark = Color(0xFFF77F00)     // Oranye Konstruksi (Warning / Highlight)
val AmberSoftBg = Color(0xFFFFF3E0)          // Latar Belakang Kartu Aksen

val BlueprintCyan = Color(0xFF0077B6)        // Biru Cetak Biru Teknik Sipil
val BlueprintLight = Color(0xFFCAF0F8)       // Biru Muda Grid

val LimestoneBg = Color(0xFFF4F6F4)          // Abu-Krem Batu Kapur (Background Aplikasi)
val BasaltSurface = Color(0xFF121A16)        // Hitam Basal untuk Kanvas Radar/Peta
val SlateText = Color(0xFF1E2923)            // Teks Utama Kontras Tinggi

private val BambuGeoColorScheme = lightColorScheme(
    primary = TerraForestDark,
    onPrimary = Color.White,
    primaryContainer = TerraMintLight,
    onPrimaryContainer = TerraForestDark,
    secondary = SafetyOrangeDark,
    onSecondary = Color.White,
    secondaryContainer = AmberSoftBg,
    onSecondaryContainer = SafetyOrangeDark,
    tertiary = BlueprintCyan,
    background = LimestoneBg,
    surface = Color.White,
    onBackground = SlateText,
    onSurface = SlateText
)

@Composable
fun BambuGeoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BambuGeoColorScheme,
        content = content
    )
}