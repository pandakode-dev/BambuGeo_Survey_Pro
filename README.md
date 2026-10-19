<div align="center">

<img src="app/src/main/res/drawable/bambugeo_logo.png" alt="Logo Bambu Geo " width="150" />

# 🐼 BambuGeo — Pro Field GIS, Geodesy & Civil Engineering Suite
**An Advanced Native Android Land Polygon Plotter, Geological Brunton Compass, Geodesic UTM/COGO & Civil Estimator**

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20(Material%203)-0F172A?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Map OSM](https://img.shields.io/badge/Maps-OpenStreetMap%20(Leaflet.js)-06B6D4?style=for-the-badge&logo=openstreetmap&logoColor=white)
![RF Engine](https://img.shields.io/badge/Physics-Link%20Budget%20%26%20Fresnel-0D9488?style=for-the-badge&logo=physics&logoColor=white)

**BambuGeo** adalah aplikasi pemetaan geospasial lapangan (*Field GIS*), survei batas kadastral/perkebunan, kompas geologi struktur, dan kalkulator konstruksi teknik sipil *native* Android yang dirancang khusus untuk surveyor tanah, insinyur sipil, praktisi kehutanan/perkebunan sawit, dan peneliti geologi lapangan (*indoor/outdoor*). Aplikasi ini membawa identitas visual instrumen survei *Total Station* modern dengan tema **Terra Forest, Safety Theodolite Amber, dan Blueprint Slate**.
</div>
---

## 📸 Tampilan Utama (Features Preview)

| CAD Polygon & Sawit | Kompas Geologi & Lereng | UTM & COGO Bidik | Cut & Fill Kontur | Waypoint GPX/KML | Kalkulator Sipil SNI |
| :---: | :---: | :---: | :---: | :---: | :---: |
| *(Tarik SS 1)* | *(Tarik SS 2)* | *(Tarik SS 3)* | *(Tarik SS 4)* | *(Tarik SS 5)* | *(Tarik SS 6)* |

---

## ✨ Fitur-Fitur "Super Field GIS & Civil Suite" (Core Features)

### 1. 📐 GPS Land Polygon & Plantation Plotter (Ukur Luas Lahan)
- **Spherical Shoelace Geodesic Engine:** Menghitung luas poligon lahan tertutup secara presisi mengikuti kelengkungan permukaan bumi (WGS84) beserta total keliling (*perimeter*) dan jarak antar-patok (Haversine).
- **Multi-Satuan Nasional & Lokal Sumatera:** Konversi otomatis ke **Meter Persegi ($\text{m}^2$)**, **Hektar ($\text{Ha}$)**, dan **Rante** ($1\text{ Rante} = 400\text{ m}^2$, satuan populer lahan/kebun di Riau & Sumatera).
- **Kalkulator Populasi Sawit (Pola Mata Lima):** Menghitung otomatis daya tampung jumlah pokok pohon kelapa sawit/karet/akasia berdasarkan rumus segitiga sama sisi ($9\times9\text{ m}$).
- **Interactive CAD Blueprint Plotter & Simulator:** Visualisasi kanvas poligon patok BPN secara *real-time* lengkap dengan tombol **Demo 1 Ha** untuk simulasi pengujian di dalam ruangan tanpa harus berjalan kaki mengelilingi lahan.
- **Export Polygon .KML & Tabel UTM .CSV:** Ekspor patok batas lahan langsung menjadi poligon berwarna di Google Earth Pro (`.KML`) atau tabel koordinat UTM siap olah di Excel/AutoCAD (`.CSV`).

### 2. 🧭 Geological Brunton Compass & Clinometer (Kompas Geologi & Lereng)
- **Brunton Strike & Dip Mode:** Menampilkan Azimuth ($0^\circ - 360^\circ$), arah mata angin, serta notasi geologi struktur (`N xxx° E / xx°`) untuk pemetaan kemiringan singkapan batuan (*outcrop*).
- **Slope Grade & Bullseye Bubble Waterpass:** Mengukur sudut kemiringan lereng dalam derajat ($^\circ$), persentase kelandaian jalan/lereng sipil ($\text{\% Grade}$), serta *Pitch & Roll* dengan visualisasi gelembung *waterpass* interaktif.
- **Hold / Lock Reading:** Fitur pengunci angka sensor secara instan ketika perangkat ditempelkan pada permukaan batuan atau lereng terjal.

### 3. 🌍 Geodesic Lab: UTM Converter, COGO & Cut-Fill Earthwork
- **WGS84 to UTM & DMS Projection:** Mengonversi koordinat Lintang/Bujur ke sistem proyeksi **UTM (Zona 47N/48N, Easting $\text{mE}$, Northing $\text{mN}$)** dan **DMS (*Degrees, Minutes, Seconds*)** sesuai standar pemetaan BPN dan KLHK.
- **COGO Forward Survey (Tembak Patok Jauh):** Menghitung koordinat patok target yang terhalang sungai/jurang hanya dengan memasukkan koordinat berdiri, jarak bidik meteran/laser ($\text{m}$), dan sudut Azimuth ($^\circ$).
- **Cut & Fill Earthwork & Contour Profile:** Visualisasi grafik penampang melintang kontur tanah (*Cross-Section Canvas*) serta perhitungan kubikasi galian (*Cut*), timbunan (*Fill*), dan estimasi jumlah ritase *Dump Truck*.

### 4. 📍 GIS Waypoint Logger (.GPX & .KML Exporter)
- **Multi-Category Field Benchmarking:** Pencatatan titik koordinat penting (*Patok BPN, Geologi, Konstruksi Sipil, Drainase, Perkebunan*) lengkap dengan elevasi ($\text{mdpl}$), waktu survei, dan catatan lapangan.
- **Direct GIS File Export:** Menghasilkan berkas standar internasional **`.GPX`** (untuk Garmin BaseCamp, QGIS, ArcGIS) dan **`.KML`** (untuk Google Earth Pro) langsung ke folder `Downloads/BambuGeo` secara 100% *offline*.

### 5. 🏗️ SNI Civil Engineering Material Suite (5-in-1 Kalkulator Proyek)
- **Estimator Cor Beton (Mutu K-225 / 1:2:3):** Menghitung kubikasi beton basah ($\text{m}^3$), jumlah Sak Semen ($50\text{ kg}$), Pasir Beton ($\text{m}^3$), dan Batu Split ($\text{m}^3$).
- **Kalkulator Dinding Bata Merah & Bata Ringan (Hebel):** Estimasi jumlah biji bata merah, blok hebel ($10\text{ cm}$), semen pasangan, dan pasir pasang.
- **Kalkulator Pondasi Batu Kali (Trapesium):** Menghitung volume pasangan pondasi, kubikasi batu kali belah, estimasi truk pengangkut, dan sak semen.
- **Kalkulator Besi Beton (SNI Rebar Weight):** Menghitung berat besi beton per meter ($\text{kg/m}$), berat per batang ($12\text{ m}$), kebutuhan jumlah lonjor, dan total tonase proyek berdasarkan diameter ($\varnothing\text{ mm}$).
- **Kalkulator Rangka Atap Baja Ringan:** Menghitung luas miring atap berdasarkan sudut kemiringan kuda-kuda ($^\circ$), kebutuhan batang Kanal C75, Reng Baja, dan lembar Genteng Metal.

---

## 🛠️ Stack Teknologi & Dependensi
- **Sistem & UI:** Kotlin, Jetpack Compose, Material Design 3 (`material-icons-extended`), Custom Vector Canvas.
- **Geodesi & Sensor Hardware:** `LocationManager` (GPS Satellite 3D Fix), `SensorManager` (`TYPE_ACCELEROMETER` & `TYPE_MAGNETIC_FIELD`).
- **Engine Matematika & Geospasial:** Spherical Shoelace Polygon Formula, Haversine Distance, Transverse Mercator (UTM WGS84), Polar COGO Trigonometry, SNI Construction Coefficients.
- **Perpustakaan Ekspor:** `MediaStore` Scoped Storage Exporter (`.GPX`, `.KML`, `.CSV` Offline Generator).

---

## 👨‍💻 Kontributor
Dikembangkan oleh **Muhammad Saputra**  
🏢 **Founder of PandaKode** (Pekanbaru, Riau, Indonesia)  
🔗 GitHub: [@breto1](https://github.com/pandakode-dev)
