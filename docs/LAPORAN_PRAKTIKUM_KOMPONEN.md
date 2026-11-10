# Laporan Hasil Implementasi Komponen Praktikum (Modul Pertemuan 9) — Proyek AZENKIVO

**Nama Proyek:** AZENKIVO (Aplikasi Siaga & Tanggap Darurat)  
**Platform:** Android Native (Kotlin, XML, ViewBinding, Material Components 3)  
**Acuan:** Modul Praktikum Pemrograman Mobile · Pertemuan 9

---

## 0. Persiapan Dependency (`app/build.gradle.kts` — Modul Tahap 0.2)

Tiga library dari Modul Pertemuan 9 Tahap 0.2 telah ditambahkan pada `app/build.gradle.kts`:
```kotlin
// Modul P9: GridLayout, ViewPager2 (Tutorial Screen), DotsIndicator
implementation("androidx.gridlayout:gridlayout:1.0.0")
implementation("androidx.viewpager2:viewpager2:1.1.0")
implementation("com.tbuonomo:dotsindicator:5.1.0")
```

---

## 1. Implementasi `Chip` & `ChipGroup` (Modul Tahap 2)

### A. Lokasi File
1. `app/src/main/res/layout/fragment_panduan.xml` & `app/src/main/java/com/example/azenkivo/Panduan/PanduanFragment.kt`
2. `app/src/main/res/layout/activity_detail.xml`

### B. Cuplikan Kode XML (`fragment_panduan.xml`)
```xml
<com.google.android.material.chip.ChipGroup
    android:id="@+id/chipGroupKategori"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:checkedChip="@+id/chipSemua"
    app:chipSpacingHorizontal="8dp"
    app:selectionRequired="true"
    app:singleLine="true"
    app:singleSelection="true">

    <com.google.android.material.chip.Chip
        android:id="@+id/chipSemua"
        style="@style/Widget.Material3.Chip.Filter"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:checked="true"
        android:fontFamily="@font/jakarta_semibold"
        android:text="@string/pand_chip_all" />

    <com.google.android.material.chip.Chip
        android:id="@+id/chipP3k"
        style="@style/Widget.Material3.Chip.Filter"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:fontFamily="@font/jakarta_semibold"
        android:text="@string/pand_chip_p3k" />

    <com.google.android.material.chip.Chip
        android:id="@+id/chipBencana"
        style="@style/Widget.Material3.Chip.Filter"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:fontFamily="@font/jakarta_semibold"
        android:text="@string/pand_chip_bencana" />
</com.google.android.material.chip.ChipGroup>
```

### C. Cuplikan Kode Kotlin (`PanduanFragment.kt` — Sesuai Modul Tahap 2.2)
```kotlin
// Filter daftar SOP berdasarkan ChipGroup kategori (Modul P9 2.2)
binding.chipGroupKategori.setOnCheckedStateChangeListener { group, checkedIds ->
    val selectedChipId = checkedIds.firstOrNull() // ambil id chip yang dipilih
    if (selectedChipId != null) {
        val chip = group.findViewById<Chip>(selectedChipId)
        Toast.makeText(requireContext(), "Filter: ${chip.text}", Toast.LENGTH_SHORT).show()
    }
    val kategori = when (selectedChipId) {
        R.id.chipP3k -> "Pertolongan Pertama (P3K)"
        R.id.chipBencana -> "Mitigasi Bencana"
        else -> null
    }
    tampilkanDaftarSop(kategori)
}
```

### D. Penjelasan
- **`ChipGroup`** (`com.google.android.material.chip.ChipGroup`) digunakan sebagai container untuk mengatur komponen `Chip` dengan atribut `app:singleSelection="true"` agar hanya satu chip yang aktif dalam satu waktu.
- Sesuai Modul Tahap 2.2, `setOnCheckedStateChangeListener` mengambil ID chip terpilih melalui `checkedIds.firstOrNull()`, menampilkan `Toast` `"Filter: ${chip.text}"`, sekaligus memfilter daftar SOP pada `ListView` berdasarkan kategori (**Semua**, **Pertolongan Pertama (P3K)**, atau **Mitigasi Bencana**).
- Selain itu, `ChipGroup` juga diterapkan di `activity_detail.xml` untuk menampilkan tag teknologi proyek (`Android`, `Kotlin`, `Material 3`).

---

## 2. Implementasi `bg_header_wafe` / `bg_header_wave` (Modul Tahap 3.2 & 3.3)

### A. Lokasi File
1. `app/src/main/res/drawable/bg_header_wafe.xml` (serta `bg_header_wave.xml`)
2. `app/src/main/res/layout/fragment_home.xml`
3. `app/src/main/res/layout/activity_tutorial.xml`

### B. Cuplikan Kode Drawable (`res/drawable/bg_header_wafe.xml`)
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="412dp"
    android:height="240dp"
    android:viewportWidth="412"
    android:viewportHeight="240">

    <!-- Latar utama: Deep Slate Navy (@color/brand) dengan bagian bawah bergelombang -->
    <path
        android:fillColor="#0F172A"
        android:pathData="M0,0 H412 V188 C340,232 268,236 196,204 C124,172 60,178 0,212 Z" />

    <!-- Lapisan gelombang kedua: Slate Navy terang (@color/brand_pressed) -->
    <path
        android:fillAlpha="0.65"
        android:fillColor="#1E293B"
        android:pathData="M0,96 C92,138 176,126 264,92 C328,68 376,78 412,102 V196 C344,234 264,228 184,196 C108,166 48,176 0,208 Z" />

    <!-- Aksen gelombang biru keselamatan (@color/accent) di tepi bawah -->
    <path
        android:fillAlpha="0.35"
        android:fillColor="#2563EB"
        android:pathData="M0,164 C84,132 164,148 246,184 C316,214 368,212 412,182 V204 C352,238 272,236 192,202 C116,170 52,180 0,214 Z" />
</vector>
```

### C. Penerapan pada `fragment_home.xml` (Sesuai Modul Tahap 3.3)
```xml
<!-- Latar gelombang header paling atas di dalam ConstraintLayout sebelum Toolbar -->
<View
    android:id="@+id/background_header"
    android:layout_width="0dp"
    android:layout_height="220dp"
    android:background="@drawable/bg_header_wafe"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent" />

<androidx.appcompat.widget.Toolbar
    android:id="@+id/toolbar"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:background="@android:color/transparent"
    ... />
```

### D. Penjelasan
- **`bg_header_wafe.xml`** dibuat menggunakan *Vector Drawable* (`<vector>`) dengan instruksi kurva Bezier (`C` pada `pathData`) sehingga menghasilkan lengkungan gelombang berlapis yang tajam di semua ukuran layar.
- Sesuai Modul Tahap 3.3, komponen `<View android:id="@+id/background_header" />` diletakkan paling atas di dalam `ConstraintLayout` pada `fragment_home.xml` (sebelum `Toolbar`), dan `android:background` pada `Toolbar` diubah menjadi `@android:color/transparent` agar gelombang header terlihat jelas di belakang sapaan pengguna.

---

## 3. Implementasi `GridLayout` (Modul Tahap 4)

### A. Lokasi File
- `app/src/main/res/layout/fragment_home.xml` (Bagian **Panggilan Cepat / Nomor Darurat**)

### B. Cuplikan Kode XML (`fragment_home.xml`)
```xml
<androidx.gridlayout.widget.GridLayout
    android:id="@+id/gridHotline"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginTop="8dp"
    app:columnCount="2"
    app:rowCount="2">

    <!-- 112 (Baris 0, Kolom 0) -->
    <LinearLayout
        android:id="@+id/card112"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_row="0"
        app:layout_column="0"
        app:layout_columnWeight="1"
        app:layout_gravity="fill_horizontal"
        android:layout_marginEnd="6dp"
        android:layout_marginBottom="12dp"
        android:background="@drawable/bg_group_surface"
        ... />
    <!-- 119 (0,1), 113 (1,0), 115 (1,1) -->
</androidx.gridlayout.widget.GridLayout>
```

### C. Penjelasan
- Menggunakan `androidx.gridlayout.widget.GridLayout` sesuai Modul Tahap 4 untuk menyusun 4 tombol panggilan cepat darurat (**112 - Darurat Umum**, **119 - Ambulans & Medis**, **113 - Pemadam Kebakaran**, **115 - Basarnas SAR**) dalam petak **2 baris × 2 kolom** (`app:columnCount="2"`, `app:rowCount="2"`).
- Sesuai aturan penting di Modul Halaman 13, setiap item di dalam `GridLayout` diberi atribut `app:layout_columnWeight="1"` dan `app:layout_gravity="fill_horizontal"` agar keempat tombol memiliki lebar yang sama persis dan rapi saat layar diputar.

---

## 4. Implementasi Tutorial Screen + `DotsIndicator` (Modul Tahap 5 & 6)

### A. Letak & Alur Navigasi
- Sesuai instruksi dosen, **Tutorial Screen diletakkan tepat setelah `SplashActivity`**:
  `SplashActivity` (2 detik) $\rightarrow$ **`TutorialActivity` (3 Fragment + `ViewPager2` + `DotsIndicator`)** $\rightarrow$ `LoginActivity` / `MainActivity`.
- Selain muncul setelah SplashScreen, Tutorial Screen juga dapat dibuka kembali kapan saja melalui ikon **Info (`action_tutorial`)** di Toolbar tab **Panduan SOP** menggunakan `MenuProvider` sesuai Modul Tahap 5.4.

### B. Daftar File
1. `SplashActivity.kt` — Mengarahkan intent setelah *delay* 2 detik ke `TutorialActivity`.
2. `TutorialActivity.kt` & `res/layout/activity_tutorial.xml` — Memuat `ViewPager2` (`@+id/viewPagerTutorial`), `DotsIndicator` (`@+id/dot_indicator`), serta tombol navigasi **Lewati**, **Kembali**, dan **Selanjutnya / Mulai Sekarang**.
3. `Tutorial/TutorialFragmentAdapter.kt` — Kelas `FragmentStateAdapter` sesuai Modul Tahap 5.2:
   ```kotlin
   class TutorialFragmentAdapter(
       activity: FragmentActivity,
       private val fragments: List<Fragment>
   ) : FragmentStateAdapter(activity) {
       override fun getItemCount(): Int = fragments.size
       override fun createFragment(position: Int): Fragment = fragments[position]
   }
   ```
4. **3 Fragment Tutorial (Konten Sesuai Judul Proyek AZENKIVO):**
   - **`Tutorial1Fragment.kt` (`fragment_tutorial_1.xml`):** *Sinyal SOS & Hotline Satu Sentuhan* — Menjelaskan fitur tombol SOS tahan 2 detik dan panggilan cepat 112, 119, 113, 115.
   - **`Tutorial2Fragment.kt` (`fragment_tutorial_2.xml`):** *Live Tracking Unit Responder* — Menjelaskan fitur pemantauan posisi, jarak, dan waktu tiba (ETA) unit responder terdekat.
   - **`Tutorial3Fragment.kt` (`fragment_tutorial_3.xml`):** *Panduan SOP P3K & Siaga Bencana* — Menjelaskan fitur 8 panduan SOP P3K & mitigasi bencana, info BMKG, serta identitas medis darurat.

### C. Cuplikan Kode Pemasangan `ViewPager2` & `DotsIndicator` (`TutorialActivity.kt` — Modul Tahap 5.3 & 6.3)
```kotlin
// Setup ViewPager2 dengan adapter 3 Fragment (Modul P9 5.3)
val fragmentsList = listOf(
    Tutorial1Fragment(),
    Tutorial2Fragment(),
    Tutorial3Fragment()
)
pagerAdapter = TutorialFragmentAdapter(this, fragmentsList)
binding.viewPagerTutorial.adapter = pagerAdapter

// DotsIndicator: dipasang SETELAH adapter (Modul P9 6.3)
binding.dotIndicator.attachTo(binding.viewPagerTutorial)
```

### D. Penjelasan
- `ViewPager2` dikombinasikan dengan `TutorialFragmentAdapter` untuk menampilkan 3 Fragment informasi fitur aplikasi **AZENKIVO** yang dapat digeser ke kiri/kanan (*swipe*).
- Komponen `com.tbuonomo.viewpagerdotsindicator.DotsIndicator` (`@+id/dot_indicator`) dihubungkan ke `ViewPager2` melalui `binding.dotIndicator.attachTo(binding.viewPagerTutorial)` tepat **setelah** adapter dipasang (sesuai peringatan pada Modul Tahap 6.3), sehingga indikator titik otomatis bergerak mengikuti perpindahan halaman.
