package com.example.azenkivo

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.azenkivo.databinding.ActivitySopDetailBinding
import com.example.azenkivo.databinding.ItemLangkahBinding

// Halaman Detail SOP
class SopDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySopDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Latar canvas terang: ikon status bar & navigasi dibuat gelap
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        // Inisialisasi ViewBinding
        binding = ActivitySopDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Padding bawah bar aksi ikut tinggi gesture bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            binding.bottomBar.updatePadding(bottom = systemBars.bottom)
            insets
        }

        // Ambil data dari Intent
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
            title = getString(R.string.det_toolbar)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Header: foto P3K untuk kategori P3K
        val headerRes = if (kategori.contains("P3K", ignoreCase = true)) {
            R.drawable.img_first_aid
        } else {
            R.drawable.ill_mitigasi
        }
        binding.imgHeader.setImageResource(headerRes)
        binding.imgHeader.contentDescription = getString(R.string.det_header_desc, judul)

        // Isi bagian judul
        binding.tvKategori.text = kategori
        binding.tvJudul.text = judul
        binding.tvDeskripsi.text = deskripsi
        binding.tvBadge.text = badge

        // Isi 4 langkah timeline
        val total = 4
        isiLangkah(binding.step1, 1, total, langkahJudul, langkahIsi)
        isiLangkah(binding.step2, 2, total, langkahJudul, langkahIsi)
        isiLangkah(binding.step3, 3, total, langkahJudul, langkahIsi)
        isiLangkah(binding.step4, 4, total, langkahJudul, langkahIsi)

        // Tombol panggil nomor darurat sesuai SOP
        binding.btnPanggil.text = getString(R.string.det_call, nomor)
        binding.btnPanggil.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$nomor")
            startActivity(intent)
        }
    }

    // Mengisi satu langkah timeline
    private fun isiLangkah(
        step: ItemLangkahBinding,
        nomor: Int,
        total: Int,
        judul: Array<String>,
        isi: Array<String>
    ) {
        val teksJudul = judul.getOrNull(nomor - 1) ?: "-"
        val teksIsi = isi.getOrNull(nomor - 1) ?: "-"
        step.tvNomorLangkah.text = nomor.toString()
        step.tvLangkahJudul.text = teksJudul
        step.tvLangkahIsi.text = teksIsi
        // Langkah terakhir tidak butuh garis penghubung
        step.viewGaris.visibility = if (nomor == total) View.INVISIBLE else View.VISIBLE
        // TalkBack membaca satu langkah sebagai satu kalimat utuh
        step.root.contentDescription = getString(R.string.det_step_desc, nomor, total, teksJudul, teksIsi)
    }

    // Tombol back di Toolbar
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
