# MyFocus - System Architecture & Engineering Guidelines

## 1. Aturan Wajib Arsitektur (Mandatory Core Rules)
1. **Zero Internet & Zero Tracking**: JANGAN tambah izin `android.permission.INTERNET` atau library analytics/iklan/jaringan (seperti Retrofit, Ktor client, Firebase Analytics, AdMob). Semua data tersimpan 100% lokal di perangkat pengguna.
2. **Notification Privacy**: JANGAN simpan isi notifikasi (judul/teks pesan/body), HANYA simpan `packageName`, kategori notifikasi, dan timestamp pencatatan.
3. **Cheat-Proof Elapsed Time**: Penghitungan durasi dan hitung mundur fokus WAJIB memakai `SystemClock.elapsedRealtime()`, BUKAN jam dinding (`System.currentTimeMillis()`), agar tidak bisa dicurangi lewat pengubahan tanggal/waktu sistem perangkat.
4. **Resilient Persistence**: State penting (sesi fokus aktif, target waktu selesai, preferensi pengguna) WAJIB disimpan di Room / DataStore, BUKAN hanya di memori, karena Background Service / Process dapat sewaktu-waktu dimatikan oleh sistem operasi Android.
5. **Self-Documenting Project**: Jelaskan fungsi singkat tiap file yang dibuat dalam dokumentasi ini dan laporan progres.

---

## 2. Tech Stack & Dependencies
- **Bahasa**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose dengan Material 3
- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK / Compile SDK**: 35 (Android 15)
- **Arsitektur**: Clean Architecture / MVVM (ViewModel + StateFlow + Repository Pattern)
- **Database Lokal**: Room Database 2.6.1 (menggunakan Kotlin Symbol Processing - KSP 2.0.21-1.0.28)
- **Key-Value Storage**: Jetpack DataStore Preferences 1.1.1
- **Build System**: Gradle 8.10.2 + Android Gradle Plugin 8.7.2
- **CI/CD**: GitHub Actions (.github/workflows/build.yml) dengan JDK 17

---

## 3. Struktur Modul & Penjelasan Tiap File (`com.myfocus.app`)

### Konfigurasi Proyek & Build
- [.gitignore](file:///home/lenovo/coding/MyFocus/.gitignore): Aturan ignore standar Android untuk build artifacts, cache gradle, `.idea/`, file dex/apk, dan `local.properties`.
- [settings.gradle.kts](file:///home/lenovo/coding/MyFocus/settings.gradle.kts): Konfigurasi root plugin repositories dan dependency management.
- [build.gradle.kts](file:///home/lenovo/coding/MyFocus/build.gradle.kts): Root build script mendeklarasikan plugin Android, Kotlin, Compose Compiler, dan KSP.
- [gradle.properties](file:///home/lenovo/coding/MyFocus/gradle.properties): Konfigurasi JVM args (2GB heap) dan opsi AndroidX.
- [gradle/libs.versions.toml](file:///home/lenovo/coding/MyFocus/gradle/libs.versions.toml): Version catalog terpusat untuk dependensi Room, DataStore, Compose, Lifecycle, dan plugin.
- [app/build.gradle.kts](file:///home/lenovo/coding/MyFocus/app/build.gradle.kts): Konfigurasi modul aplikasi (minSdk 26, targetSdk 35, Java 17, dependensi Jetpack Compose, Room KSP, DataStore).
- [.github/workflows/build.yml](file:///home/lenovo/coding/MyFocus/.github/workflows/build.yml): Otomasi CI GitHub Actions (setup JDK 17, cache gradle, build `./gradlew assembleDebug`, upload artifact debug APK).

### Lapisan Data (Data Layer)
- [FocusSessionEntity.kt](file:///home/lenovo/coding/MyFocus/app/src/main/java/com/myfocus/app/data/local/entity/FocusSessionEntity.kt): Entitas Room penyimpan riwayat sesi fokus selesai (durasi, status, timestamp).
- [NotificationLogEntity.kt](file:///home/lenovo/coding/MyFocus/app/src/main/java/com/myfocus/app/data/local/entity/NotificationLogEntity.kt): Entitas Room pencatat notifikasi sesuai Aturan #2 (HANYA `packageName`, `category`, `timestamp`; dilarang menyimpan judul/isi pesan).
- [FocusDao.kt](file:///home/lenovo/coding/MyFocus/app/src/main/java/com/myfocus/app/data/local/dao/FocusDao.kt): DAO Room untuk operasi query dan insert sesi fokus serta log notifikasi dalam bentuk Flow.
- [AppDatabase.kt](file:///home/lenovo/coding/MyFocus/app/src/main/java/com/myfocus/app/data/local/AppDatabase.kt): RoomDatabase singleton lokal berlabel `myfocus.db`.
- [DataStoreManager.kt](file:///home/lenovo/coding/MyFocus/app/src/main/java/com/myfocus/app/data/local/DataStoreManager.kt): Pengelola persistensi DataStore Preferences untuk state aktif sesi fokus (Aturan #4) yang tahan terhadap penghentian service/aplikasi oleh OS.
- [FocusRepository.kt](file:///home/lenovo/coding/MyFocus/app/src/main/java/com/myfocus/app/data/repository/FocusRepository.kt): Repository sebagai single source of truth yang menghubungkan Room dan DataStore ke ViewModel.

### Lapisan Timer & Domain (Timer Layer)
- [FocusTimerHelper.kt](file:///home/lenovo/coding/MyFocus/app/src/main/java/com/myfocus/app/timer/FocusTimerHelper.kt): Helper penghitungan waktu dan hitung mundur berbasis `SystemClock.elapsedRealtime()` sesuai Aturan #3 untuk mencegah manipulasi jam perangkat.

### Lapisan Presentasi (UI Layer)
- [Color.kt](file:///home/lenovo/coding/MyFocus/app/src/main/java/com/myfocus/app/ui/theme/Color.kt): Definisi palet warna Material 3 MyFocus (tema gelap fokus, aksen biru dan hijau).
- [Type.kt](file:///home/lenovo/coding/MyFocus/app/src/main/java/com/myfocus/app/ui/theme/Type.kt): Definisi tipografi Material 3 modern.
- [Theme.kt](file:///home/lenovo/coding/MyFocus/app/src/main/java/com/myfocus/app/ui/theme/Theme.kt): Tema MaterialTheme Compose dengan dukungan skema warna gelap/terang.
- [MainUiState.kt](file:///home/lenovo/coding/MyFocus/app/src/main/java/com/myfocus/app/ui/viewmodel/MainUiState.kt): Data class state UI immutable yang memuat sisa waktu, progress bar, streak, dan status sesi.
- [MainViewModel.kt](file:///home/lenovo/coding/MyFocus/app/src/main/java/com/myfocus/app/ui/viewmodel/MainViewModel.kt): ViewModel yang mengamati Flow dari repository, mengelola timer berbasis `SystemClock.elapsedRealtime()`, dan memaparkan `StateFlow<MainUiState>`.
- [HomeScreen.kt](file:///home/lenovo/coding/MyFocus/app/src/main/java/com/myfocus/app/ui/screen/HomeScreen.kt): Layar Compose Material 3 menampilkan judul "MyFocus", visual indikator progres lingkaran, statistik, dan tombol aksi fokus.
- [MainActivity.kt](file:///home/lenovo/coding/MyFocus/app/src/main/java/com/myfocus/app/ui/MainActivity.kt): Single Activity entry point yang menginjeksi ViewModel dan me-render `HomeScreen`.
- [MyFocusApp.kt](file:///home/lenovo/coding/MyFocus/app/src/main/java/com/myfocus/app/MyFocusApp.kt): Inisialisasi dependensi lokal saat aplikasi dimulai (tanpa SDK pihak ketiga/iklan/analitik).

---

## 4. Log Perkembangan Tahap (Stage Progress Log)

### Tahap 1: Inisialisasi Proyek, Gradle Wrapper, Skrip CI/CD, & Setup Arsitektur Dasar
- **Status**: SELESAI (Completed & Verified)
- **Hasil Verifikasi**:
  - [x] Manifest bersih tanpa permission `android.permission.INTERNET`.
  - [x] Room Entity `NotificationLogEntity` HANYA menyimpan `packageName`, `category`, `timestamp` tanpa isi teks notifikasi.
  - [x] `FocusTimerHelper` sepenuhnya mengandalkan `SystemClock.elapsedRealtime()`.
  - [x] `DataStoreManager` & `AppDatabase` Room menyimpan state aktif sehingga aman saat service di-kill OS.
  - [x] ViewModel memaparkan `StateFlow<MainUiState>` dan dikonsumsi reaktif oleh Jetpack Compose Material 3.
  - [x] Gradle wrapper (`./gradlew`) terpasang dan berfungsi sempurna.
  - [x] `.gitignore` standar Android terkonfigurasi rapi.
  - [x] Workflow CI `.github/workflows/build.yml` (JDK 17, gradle cache, assembleDebug, upload artifact) siap.
  - [x] Build lokal `./gradlew assembleDebug` berhasil dieksekusi (`BUILD SUCCESSFUL in 8m 6s`, menghasilkan APK debug berukuran 17MB di `app/build/outputs/apk/debug/app-debug.apk`).
