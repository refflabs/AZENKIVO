package com.example.azenkivo

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.azenkivo.databinding.ActivityDetailBinding

// Halaman Detail Proyek AZENKIVO
class DetailActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Latar canvas terang: ikon status bar & navigasi dibuat gelap
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Padding untuk status bar & gesture bar
        val paddingBawahAsli = binding.scrollDetail.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            binding.scrollDetail.updatePadding(bottom = paddingBawahAsli + systemBars.bottom)
            insets
        }

        // Toolbar sebagai ActionBar dengan tombol back
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.detail_title)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Tombol "Kembali ke Beranda": tutup Detail
        binding.btnKembali.setOnClickListener { finish() }
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
