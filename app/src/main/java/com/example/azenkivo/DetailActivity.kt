package com.example.azenkivo

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.azenkivo.databinding.ActivityDetailBinding
import com.example.azenkivo.databinding.ItemLangkahBinding

// Halaman Detail SOP: menampilkan data yang dikirim dari PanduanFragment lewat Intent
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ikon status bar putih karena Toolbar berwarna merah
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))

        // Inisialisasi ViewBinding
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Padding atas dipasang di Toolbar supaya warna merahnya menutupi area status bar
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            binding.toolbar.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        // Ambil data dari Intent (nilai setelah ?: adalah cadangan jika data kosong)
        val judul = intent.getStringExtra("EXTRA_JUDUL") ?: "Resusitasi Jantung Paru (CPR)"
        val deskripsi = intent.getStringExtra("EXTRA_DESKRIPSI") ?: "Penanganan henti napas & henti jantung"
        val kategori = intent.getStringExtra("EXTRA_KATEGORI") ?: "Pertolongan Pertama (P3K)"
        val badge = intent.getStringExtra("EXTRA_BADGE") ?: "GOLDEN RATIO 30:2"
        val nomor = intent.getStringExtra("EXTRA_NOMOR") ?: "119"
        val langkahJudul = intent.getStringArrayExtra("EXTRA_LANGKAH_JUDUL") ?: arrayOf()
        val langkahIsi = intent.getStringArrayExtra("EXTRA_LANGKAH_ISI") ?: arrayOf()

        // Setup Toolbar sebagai ActionBar dengan tombol back
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Detail SOP: $kategori"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Isi kartu hero
        binding.tvJudul.text = judul
        binding.tvDeskripsi.text = deskripsi
        binding.tvBadge.text = badge

        // Isi 4 kartu langkah
        isiLangkah(binding.step1, 1, langkahJudul, langkahIsi)
        isiLangkah(binding.step2, 2, langkahJudul, langkahIsi)
        isiLangkah(binding.step3, 3, langkahJudul, langkahIsi)
        isiLangkah(binding.step4, 4, langkahJudul, langkahIsi)

        // Tombol panggil nomor darurat sesuai SOP
        binding.btnPanggil.text = "Panggil $nomor Sekarang"
        binding.btnPanggil.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$nomor")
            startActivity(intent)
        }
    }

    // Mengisi satu kartu langkah (layout item_langkah.xml yang di-include)
    private fun isiLangkah(step: ItemLangkahBinding, nomor: Int, judul: Array<String>, isi: Array<String>) {
        step.tvNomorLangkah.text = nomor.toString()
        step.tvLangkahJudul.text = judul.getOrNull(nomor - 1) ?: "-"
        step.tvLangkahIsi.text = isi.getOrNull(nomor - 1) ?: "-"
    }

    // Tombol back di Toolbar (cara Modul P6 langkah 4.5)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
