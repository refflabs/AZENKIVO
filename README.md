# AZENKIVO — Sistem Keselamatan & Tanggap Darurat

Aplikasi Android sisi **pengguna** untuk kesiapsiagaan dan tanggap darurat: akses cepat hotline nasional, tombol SOS, panduan SOP P3K & mitigasi bencana, serta simulasi pelacakan unit responder.

Dibuat untuk mata kuliah **Pemrograman Mobile (TI432228)** — Politeknik Caltex Riau.

> Sisi dispatch, responder, dan admin **disimulasikan dengan data dummy/lokal**. Aplikasi ini tidak memakai backend.

---

## Alur Aplikasi

```
SplashActivity ──(cek isLogin di SharedPreferences)──┬──> LoginActivity ──> BaseActivity
                                                     └──> BaseActivity

BaseActivity (Bottom Navigation)
├── Beranda   → HomeFragment
├── Tracking  → TrackingFragment
├── Panduan   → PanduanFragment ──(klik SOP)──> DetailActivity
└── Keluar    → AlertDialog logout ──> LoginActivity
```

## Fitur

### Splash Screen
- Logo perisai, label *Edisi Siaga Indonesia*, dan hotline 112 • 119 • 113 • 115.
- Menunggu 2 detik dengan `lifecycleScope` + `delay()`, lalu membaca `isLogin` dari SharedPreferences untuk menentukan tujuan (Login atau Beranda).

### Login
- Login mode demo: berhasil jika **ID Tim sama dengan kata sandi**.
- Tombol lihat/sembunyikan kata sandi.
- **Akses Cepat Tamu (Darurat)**: masuk tanpa akun dengan username `Tamu`.
- *Lupa Sandi* dan *Daftar Relawan* menampilkan dialog informasi.
- Status login dan username disimpan di SharedPreferences `user_pref` (key `isLogin`, `username`).

### Beranda
- Sapaan "Halo, *username*!" yang dibaca dari SharedPreferences.
- Banner peringatan gempa → membuka halaman **Info BMKG** (WebView).
- Tombol **SOS**: tekan & tahan → dialog konfirmasi → pindah ke tab Tracking.
- 4 kartu hotline (112, 119, 113, 115) yang membuka aplikasi telepon dengan `Intent.ACTION_DIAL`.
- *Semua (4)*: dialog daftar nomor darurat memakai `ArrayAdapter` + `simple_list_item_1`.
- Banner SOP P3K → pindah ke tab Panduan.

### Tracking (simulasi)
- Status GPS, ETA, dan jarak unit terdekat.
- Daftar unit responder memakai **ListView + Custom Adapter** (`ResponderAdapter`) dan **Glide** untuk foto petugas.
- Klik unit → dialog detail dengan tombol hubungi.
- Tombol *Hubungi Ambulans* (119) dan *Batal SOS*.

### Panduan SOP
- 8 SOP (CPR, pendarahan, luka bakar, tersedak, patah tulang, gempa, kebakaran, banjir) memakai **ListView + `SimpleAdapter` + `simple_list_item_2`** (judul + deskripsi).
- Klik item → `DetailActivity`, data dikirim lewat `Intent.putExtra()`.

### Detail SOP
- Kartu utama, banner *Prinsip 3A*, 4 langkah penanganan, dan tombol *Panggil [nomor] Sekarang* sesuai jenis SOP.

### Toolbar & Option Menu
- Notifikasi (dialog), **Info BMKG** (WebView), dan Tentang Aplikasi.

### Logout
- Tab **Keluar** menampilkan `AlertDialog`. Jika "Ya": SharedPreferences dihapus dengan `clear()` lalu kembali ke Login dengan flag `NEW_TASK | CLEAR_TASK`.

---

## Materi Kuliah yang Diterapkan

| Materi | Penerapan | File |
|---|---|---|
| Activity & Intent | Perpindahan halaman, `putExtra` ke Detail | `LoginActivity`, `PanduanFragment`, `DetailActivity` |
| Intent implisit | Telepon darurat `ACTION_DIAL` | `HomeFragment`, `TrackingFragment`, `DetailActivity` |
| ViewBinding | Semua Activity, Fragment, dan adapter | — |
| AlertDialog | Login gagal, logout, SOS, notifikasi | `LoginActivity`, `BaseActivity`, `HomeFragment` |
| SharedPreferences | Sesi login (`isLogin`, `username`) | `SplashActivity`, `LoginActivity`, `BaseActivity` |
| Splash Screen | `lifecycleScope` + `delay()` | `SplashActivity` |
| Fragment | Halaman Beranda, Tracking, Panduan | `Home/`, `Tracking/`, `Panduan/` |
| Bottom Navigation | 4 menu + `replaceFragment()` | `BaseActivity`, `menu/bottom_nav_menu.xml` |
| Toolbar & Option Menu | Notifikasi, Info BMKG, Tentang | `BaseActivity`, `menu/toolbar_menu.xml` |
| ListView – `ArrayAdapter` | `simple_list_item_1` | `HomeFragment` (dialog *Semua*) |
| ListView – `SimpleAdapter` | `simple_list_item_2` | `PanduanFragment` |
| ListView – Custom Adapter | Model + layout item + `getView()` + Glide | `Tracking/ResponderModel`, `ResponderAdapter`, `item_responder.xml` |
| WebView | Info BMKG | `WebViewActivity` |
| CardView | Kartu hotline, SOP, unit responder | layout XML |

---

## Struktur Proyek

```
app/src/main/java/com/example/azenkivo/
├── SplashActivity.kt
├── LoginActivity.kt
├── BaseActivity.kt          # Bottom Navigation + Option Menu
├── DetailActivity.kt        # Detail SOP
├── WebViewActivity.kt       # Info BMKG
├── Home/
│   └── HomeFragment.kt
├── Tracking/
│   ├── TrackingFragment.kt
│   ├── ResponderModel.kt
│   └── ResponderAdapter.kt
└── Panduan/
    └── PanduanFragment.kt

app/src/main/res/
├── layout/      # activity_*, fragment_*, item_responder, item_langkah
├── menu/        # bottom_nav_menu, toolbar_menu
├── values/      # colors, themes, styles (design system)
├── drawable/    # ikon vector & background
└── font/        # Poppins
```

---

## Tech Stack

| Komponen | Versi / Keterangan |
|---|---|
| Bahasa | Kotlin |
| Min SDK / Target SDK | 29 (Android 10) / 37 |
| Build | Gradle Kotlin DSL + version catalog (`libs.versions.toml`), AGP 9.3.2 |
| UI | XML Layout + ViewBinding, Material Components 1.14.0 (tema Material3), ConstraintLayout 2.2.2, CardView 1.0.0 |
| Lifecycle | `lifecycle-runtime-ktx` 2.9.4 (`lifecycleScope`) |
| Gambar | Glide 4.16.0 |
| Font | Poppins |

**Permission:** `INTERNET` (Glide dan WebView).

---

## Cara Menjalankan

1. Clone repo:
   ```bash
   git clone https://github.com/refflabs/AZENKIVO.git
   ```
2. Buka folder proyek di Android Studio, lalu klik **Sync Now**.
3. Jalankan di emulator atau HP (Android 10 ke atas). Koneksi internet dibutuhkan untuk foto unit responder dan Info BMKG.
4. Login demo: isi **ID Tim dan kata sandi dengan teks yang sama** (contoh `tim1` / `tim1`), atau pilih **Akses Cepat Tamu**.

---

## Rencana Pengembangan

- [ ] SOS aktif hanya jika ditahan minimal 2 detik
- [ ] Simpan insiden & laporan akhir ke **Room**
- [ ] Peta live tracking (Maps SDK)
- [ ] Data gempa terkini dari BMKG dengan **Retrofit**
- [ ] Notifikasi peringatan darurat

---

Dibuat oleh **Reffael** — 3 TI, Politeknik Caltex Riau.
