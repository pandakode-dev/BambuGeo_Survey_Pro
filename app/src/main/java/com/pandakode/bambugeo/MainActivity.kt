package com.pandakode.bambugeo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pandakode.bambugeo.ui.screens.*
import com.pandakode.bambugeo.ui.theme.BambuGeoTheme
import com.pandakode.bambugeo.ui.theme.SafetyAmber
import com.pandakode.bambugeo.ui.theme.TerraForestDark
import com.pandakode.bambugeo.ui.theme.TerraMintLight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BambuGeoTheme {
                BambuGeoMainApp()
            }
        }
    }
}

enum class GeoNavTab(val title: String, val icon: ImageVector) {
    POLYGON("Poligon", Icons.Default.Pentagon),
    COMPASS("Kompas", Icons.Default.Explore),
    GEODESY("Geodesi", Icons.Default.Public),
    WAYPOINT("GPX/KML", Icons.Default.PinDrop),
    CIVIL("Sipil SNI", Icons.Default.Architecture)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BambuGeoMainApp() {
    var currentTab by remember { mutableStateOf(GeoNavTab.POLYGON) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Terrain,
                            contentDescription = null,
                            tint = SafetyAmber,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "BambuGeo Survey Pro",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Field GIS, Geodesy & Civil Suite • by PandaKode",
                                color = TerraMintLight,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TerraForestDark
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = TerraForestDark,
                tonalElevation = 8.dp
            ) {
                GeoNavTab.entries.forEach { tab ->
                    val selected = currentTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = if (selected) TerraForestDark else Color.White.copy(alpha = 0.7f)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                color = if (selected) SafetyAmber else Color.White.copy(alpha = 0.7f),
                                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Normal,
                                fontSize = 10.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = SafetyAmber
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                GeoNavTab.POLYGON -> LandPolygonScreen()
                GeoNavTab.COMPASS -> ClinometerCompassScreen()
                GeoNavTab.GEODESY -> GeodesicToolsScreen()
                GeoNavTab.WAYPOINT -> WaypointLoggerScreen()
                GeoNavTab.CIVIL -> CivilCalculatorScreen()
            }
        }
    }
}