package com.example.azenkivo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.viewpager2.widget.ViewPager2
import com.example.azenkivo.Tutorial.Tutorial1Fragment
import com.example.azenkivo.Tutorial.Tutorial2Fragment
import com.example.azenkivo.Tutorial.Tutorial3Fragment
import com.example.azenkivo.Tutorial.TutorialFragmentAdapter
import com.example.azenkivo.databinding.ActivityTutorialBinding

// Halaman Tutorial (Onboarding) yang tampil setelah SplashActivity dan berisi 3 Fragment
class TutorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTutorialBinding
    private lateinit var pagerAdapter: TutorialFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Perbarui tombol saat halaman berpindah (titik diatur DotsIndicator)
        binding.viewPagerTutorial.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                perbaruiTampilanNavigasi(position)
            }
        })

        // Tombol Lewati -> langsung ke halaman berikutnya (Login / Main)
        binding.btnSkip.setOnClickListener {
            lanjutKeHalamanBerikutnya()
        }

        // Tombol Kembali -> mundur 1 fragment
        binding.btnPrev.setOnClickListener {
            val current = binding.viewPagerTutorial.currentItem
            if (current > 0) {
                binding.viewPagerTutorial.currentItem = current - 1
            }
        }

        // Tombol Selanjutnya / Mulai Sekarang
        binding.btnNext.setOnClickListener {
            val current = binding.viewPagerTutorial.currentItem
            if (current < pagerAdapter.itemCount - 1) {
                binding.viewPagerTutorial.currentItem = current + 1
            } else {
                lanjutKeHalamanBerikutnya()
            }
        }
    }

    private fun perbaruiTampilanNavigasi(posisi: Int) {
        // Tampilkan tombol Kembali di halaman ke-2 dan ke-3
        binding.btnPrev.visibility = if (posisi == 0) View.GONE else View.VISIBLE

        // Di halaman terakhir (ke-3), ubah teks tombol menjadi "Mulai Sekarang"
        val halamanTerakhir = posisi == pagerAdapter.itemCount - 1
        binding.btnNext.setText(if (halamanTerakhir) R.string.tut_start else R.string.tut_next)
        binding.btnSkip.visibility = if (halamanTerakhir) View.INVISIBLE else View.VISIBLE
    }

    // Tutorial selesai: simpan tutorial_seen (Modul P9 7.5), lalu lanjut
    private fun lanjutKeHalamanBerikutnya() {
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        sharedPref.edit { putBoolean("tutorial_seen", true) }

        // Dibuka dari ikon Info di Toolbar Panduan -> cukup tutup
        if (intent.getBooleanExtra(EXTRA_DARI_MENU, false)) {
            finish()
            return
        }

        val isLogin = sharedPref.getBoolean("isLogin", false)
        val target = if (isLogin) MainActivity::class.java else LoginActivity::class.java
        startActivity(Intent(this, target))
        finish()
    }

    companion object {
        const val EXTRA_DARI_MENU = "EXTRA_DARI_MENU"
    }
}
