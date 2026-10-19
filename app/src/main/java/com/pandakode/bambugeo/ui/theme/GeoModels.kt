package com.pandakode.bambugeo.model

import kotlin.math.*

data class GeoPoint(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val label: String = "P$id"
)

data class WaypointItem(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val note: String,
    val timestamp: String
)

data class UtmCoordinate(
    val zoneNumber: Int,
    val zoneLetter: Char,
    val hemisphere: String,
    val easting: Double,
    val northing: Double
)

object GeoMathEngine {
    private const val EARTH_RADIUS = 6378137.0 // WGS84 Major Axis (a)
    private const val WGS84_E2 = 0.00669437999014 // Eccentricity squared

    // 1. Jarak 2 Titik GPS (Haversine) dalam Meter
    fun calculateDistance(p1: GeoPoint, p2: GeoPoint): Double {
        return calculateDistanceCoord(p1.latitude, p1.longitude, p2.latitude, p2.longitude)
    }

    fun calculateDistanceCoord(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS * c
    }

    // 2. Menghitung Sudut Azimuth (Bearing 0-360°) dari Titik 1 ke Titik 2 (Inverse COGO)
    fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val dLon = Math.toRadians(lon2 - lon1)
        val y = sin(dLon) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(dLon)
        val theta = atan2(y, x)
        return (Math.toDegrees(theta) + 360.0) % 360.0
    }

    // 3. Forward COGO (Menembak Titik Koordinat Baru dari Jarak Meter & Sudut Azimuth)
    fun calculateForwardPoint(startLat: Double, startLon: Double, distanceMeters: Double, azimuthDeg: Double): Pair<Double, Double> {
        val angDist = distanceMeters / EARTH_RADIUS
        val brng = Math.toRadians(azimuthDeg)
        val lat1 = Math.toRadians(startLat)
        val lon1 = Math.toRadians(startLon)

        val lat2 = asin(sin(lat1) * cos(angDist) + cos(lat1) * sin(angDist) * cos(brng))
        val lon2 = lon1 + atan2(
            sin(brng) * sin(angDist) * cos(lat1),
            cos(angDist) - sin(lat1) * sin(lat2)
        )
        return Pair(Math.toDegrees(lat2), Math.toDegrees(lon2))
    }

    // 4. Konversi WGS84 (Lat/Lon) ke Proyeksi UTM (Standar BPN & Kehutanan)
    fun latLonToUTM(lat: Double, lon: Double): UtmCoordinate {
        val zoneNumber = floor((lon + 180.0) / 6.0).toInt() + 1
        val latRad = Math.toRadians(lat)
        val lonRad = Math.toRadians(lon)
        val lonOrigin = (zoneNumber - 1) * 6 - 180 + 3
        val lonOriginRad = Math.toRadians(lonOrigin.toDouble())

        val k0 = 0.9996
        val ePrimeSquared = WGS84_E2 / (1.0 - WGS84_E2)

        val n = EARTH_RADIUS / sqrt(1.0 - WGS84_E2 * sin(latRad) * sin(latRad))
        val t = tan(latRad) * tan(latRad)
        val c = ePrimeSquared * cos(latRad) * cos(latRad)
        val a = cos(latRad) * (lonRad - lonOriginRad)

        val m = EARTH_RADIUS * (
                (1.0 - WGS84_E2 / 4.0 - 3.0 * WGS84_E2 * WGS84_E2 / 64.0) * latRad
                        - (3.0 * WGS84_E2 / 8.0 + 3.0 * WGS84_E2 * WGS84_E2 / 32.0) * sin(2.0 * latRad)
                        + (15.0 * WGS84_E2 * WGS84_E2 / 256.0) * sin(4.0 * latRad)
                )

        val easting = k0 * n * (a + (1.0 - t + c) * a.pow(3.0) / 6.0 +
                (5.0 - 18.0 * t + t * t + 72.0 * c - 58.0 * ePrimeSquared) * a.pow(5.0) / 120.0) + 500000.0

        var northing = k0 * (m + n * tan(latRad) * (a * a / 2.0 +
                (5.0 - t + 9.0 * c + 4.0 * c * c) * a.pow(4.0) / 24.0 +
                (61.0 - 58.0 * t + t * t + 600.0 * c - 330.0 * ePrimeSquared) * a.pow(6.0) / 720.0))

        val hemisphere = if (lat < 0) {
            northing += 10000000.0
            "South (S)"
        } else {
            "North (N)"
        }

        val zoneLetter = if (lat >= 0) 'N' else 'M'
        return UtmCoordinate(zoneNumber, zoneLetter, hemisphere, easting, northing)
    }

    // 5. Total Keliling Poligon (Perimeter) dalam Meter
    fun calculatePerimeter(points: List<GeoPoint>): Double {
        if (points.size < 2) return 0.0
        var perimeter = 0.0
        for (i in points.indices) {
            val nextIndex = (i + 1) % points.size
            if (points.size == 2 && i == 1) break
            perimeter += calculateDistance(points[i], points[nextIndex])
        }
        return perimeter
    }

    // 6. Luas Poligon GPS (Spherical Shoelace Projection) dalam m2
    fun calculatePolygonAreaSqMeters(points: List<GeoPoint>): Double {
        if (points.size < 3) return 0.0
        val originLat = Math.toRadians(points[0].latitude)
        val originLon = Math.toRadians(points[0].longitude)

        val xyPoints = points.map { pt ->
            val latRad = Math.toRadians(pt.latitude)
            val lonRad = Math.toRadians(pt.longitude)
            val x = (lonRad - originLon) * cos(originLat) * EARTH_RADIUS
            val y = (latRad - originLat) * EARTH_RADIUS
            Pair(x, y)
        }

        var area = 0.0
        for (i in xyPoints.indices) {
            val j = (i + 1) % xyPoints.size
            area += xyPoints[i].first * xyPoints[j].second
            area -= xyPoints[j].first * xyPoints[i].second
        }
        return abs(area) / 2.0
    }

    // 7. Estimasi Jumlah Pohon Perkebunan (Pola Segitiga Sama Sisi / Mata Lima Sawit)
    fun estimatePlantPopulation(areaSqMeters: Double, spacingMeters: Double = 9.0): Int {
        if (areaSqMeters <= 0 || spacingMeters <= 0) return 0
        // Rumus Mata Lima (Hexagonal/Equilateral Triangle): Area per pohon = S^2 * sin(60°) = S^2 * 0.866
        val areaPerTree = spacingMeters * spacingMeters * 0.8660254
        return (areaSqMeters / areaPerTree).toInt()
    }

    // 8. Konversi Desimal ke DMS (Degrees Minutes Seconds)
    fun toDMS(coord: Double, isLatitude: Boolean): String {
        val dir = if (isLatitude) {
            if (coord >= 0) "LU (N)" else "LS (S)"
        } else {
            if (coord >= 0) "BT (E)" else "BB (W)"
        }
        val absCoord = abs(coord)
        val deg = absCoord.toInt()
        val minFull = (absCoord - deg) * 60
        val min = minFull.toInt()
        val sec = (minFull - min) * 60
        return String.format("%d° %02d' %05.2f\" %s", deg, min, sec, dir)
    }
}