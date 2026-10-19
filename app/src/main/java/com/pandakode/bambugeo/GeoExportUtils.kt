package com.pandakode.bambugeo.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.pandakode.bambugeo.model.GeoMathEngine
import com.pandakode.bambugeo.model.GeoPoint
import com.pandakode.bambugeo.model.WaypointItem
import java.io.OutputStream

object GeoExportUtils {

    // Ekspor Poligon Lahan Tertutup ke Google Earth (.KML)
    fun exportPolygonToKML(context: Context, points: List<GeoPoint>, areaSqM: Double, perimeterM: Double): Boolean {
        if (points.size < 3) {
            Toast.makeText(context, "Minimal 3 titik patok untuk ekspor Poligon KML!", Toast.LENGTH_SHORT).show()
            return false
        }

        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n")
        sb.append("  <Document>\n")
        sb.append("    <name>BambuGeo Land Polygon Survey</name>\n")
        sb.append("    <Style id=\"bambuPolyStyle\">\n")
        sb.append("      <LineStyle><color>ff007ff7</color><width>3</width></LineStyle>\n")
        sb.append("      <PolyStyle><color>661c9fff</color></PolyStyle>\n")
        sb.append("    </Style>\n")
        sb.append("    <Placemark>\n")
        sb.append("      <name>Lahan Terukur (${String.format("%.2f", areaSqM / 10000.0)} Ha)</name>\n")
        sb.append("      <description>Luas: ${String.format("%.1f", areaSqM)} m2 | Keliling: ${String.format("%.1f", perimeterM)} m | By PandaKode</description>\n")
        sb.append("      <styleUrl>#bambuPolyStyle</styleUrl>\n")
        sb.append("      <Polygon>\n")
        sb.append("        <outerBoundaryIs>\n")
        sb.append("          <LinearRing>\n")
        sb.append("            <coordinates>\n")
        for (pt in points) {
            sb.append("              ${pt.longitude},${pt.latitude},${pt.altitude}\n")
        }
        // Tutup poligon kembali ke titik pertama
        val first = points.first()
        sb.append("              ${first.longitude},${first.latitude},${first.altitude}\n")
        sb.append("            </coordinates>\n")
        sb.append("          </LinearRing>\n")
        sb.append("        </outerBoundaryIs>\n")
        sb.append("      </Polygon>\n")
        sb.append("    </Placemark>\n")
        sb.append("  </Document>\n")
        sb.append("</kml>")

        val fileName = "BambuGeo_Polygon_${System.currentTimeMillis() / 1000}.kml"
        return saveFileToDownloads(context, fileName, "application/vnd.google-earth.kml+xml", sb.toString())
    }

    // Ekspor Patok Poligon ke Tabel Excel (.CSV) lengkap dengan Koordinat UTM
    fun exportPolygonToCSV(context: Context, points: List<GeoPoint>): Boolean {
        if (points.isEmpty()) {
            Toast.makeText(context, "Belum ada patok untuk diekspor ke CSV!", Toast.LENGTH_SHORT).show()
            return false
        }

        val sb = StringBuilder()
        sb.append("No_Patok,Latitude_WGS84,Longitude_WGS84,UTM_Zone,Easting_mE,Northing_mN,Elevasi_mdpl,Akurasi_m\n")
        for (pt in points) {
            val utm = GeoMathEngine.latLonToUTM(pt.latitude, pt.longitude)
            sb.append("${pt.label},${pt.latitude},${pt.longitude},${utm.zoneNumber}${utm.zoneLetter},${String.format("%.2f", utm.easting)},${String.format("%.2f", utm.northing)},${pt.altitude},${pt.accuracy}\n")
        }

        val fileName = "BambuGeo_Patok_UTM_${System.currentTimeMillis() / 1000}.csv"
        return saveFileToDownloads(context, fileName, "text/csv", sb.toString())
    }

    fun exportToGPX(context: Context, waypoints: List<WaypointItem>): Boolean {
        if (waypoints.isEmpty()) {
            Toast.makeText(context, "Belum ada Waypoint untuk diekspor!", Toast.LENGTH_SHORT).show()
            return false
        }

        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append("<gpx version=\"1.1\" creator=\"BambuGeo Survey Pro - PandaKode (Muhammad Saputra)\" xmlns=\"http://www.topografix.com/GPX/1/1\">\n")
        sb.append("  <metadata>\n")
        sb.append("    <name>BambuGeo Field Survey Waypoints</name>\n")
        sb.append("    <author><name>PandaKode - Pekanbaru</name></author>\n")
        sb.append("  </metadata>\n")

        for (wp in waypoints) {
            sb.append("  <wpt lat=\"${wp.latitude}\" lon=\"${wp.longitude}\">\n")
            sb.append("    <ele>${wp.altitude}</ele>\n")
            sb.append("    <name>${escapeXml(wp.name)}</name>\n")
            sb.append("    <type>${escapeXml(wp.category)}</type>\n")
            sb.append("    <desc>${escapeXml(wp.note)} (Waktu: ${wp.timestamp})</desc>\n")
            sb.append("  </wpt>\n")
        }
        sb.append("</gpx>")

        val fileName = "BambuGeo_Waypoints_${System.currentTimeMillis() / 1000}.gpx"
        return saveFileToDownloads(context, fileName, "application/gpx+xml", sb.toString())
    }

    fun exportToKML(context: Context, waypoints: List<WaypointItem>): Boolean {
        if (waypoints.isEmpty()) {
            Toast.makeText(context, "Belum ada Waypoint untuk diekspor!", Toast.LENGTH_SHORT).show()
            return false
        }

        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n")
        sb.append("  <Document>\n")
        sb.append("    <name>BambuGeo Survey Pro - KML Export</name>\n")
        sb.append("    <description>Generated by BambuGeo (PandaKode - Muhammad Saputra)</description>\n")

        for (wp in waypoints) {
            sb.append("    <Placemark>\n")
            sb.append("      <name>${escapeXml(wp.name)} [${escapeXml(wp.category)}]</name>\n")
            sb.append("      <description>${escapeXml(wp.note)} | Elev: ${wp.altitude}m | ${wp.timestamp}</description>\n")
            sb.append("      <Point>\n")
            sb.append("        <coordinates>${wp.longitude},${wp.latitude},${wp.altitude}</coordinates>\n")
            sb.append("      </Point>\n")
            sb.append("    </Placemark>\n")
        }
        sb.append("  </Document>\n")
        sb.append("</kml>")

        val fileName = "BambuGeo_GoogleEarth_${System.currentTimeMillis() / 1000}.kml"
        return saveFileToDownloads(context, fileName, "application/vnd.google-earth.kml+xml", sb.toString())
    }

    private fun saveFileToDownloads(context: Context, fileName: String, mimeType: String, content: String): Boolean {
        return try {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/BambuGeo")
                }
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                val outputStream: OutputStream? = resolver.openOutputStream(uri)
                outputStream?.use { it.write(content.toByteArray()) }
                Toast.makeText(context, "Tersimpan di Downloads/BambuGeo/$fileName", Toast.LENGTH_LONG).show()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal menyimpan: ${e.message}", Toast.LENGTH_LONG).show()
            false
        }
    }

    private fun escapeXml(input: String): String {
        return input.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
    }
}