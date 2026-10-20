package com.example.azenkivo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.azenkivo.Home.HomeFragment
import com.example.azenkivo.Panduan.PanduanFragment
import com.example.azenkivo.Tracking.TrackingFragment
import com.example.azenkivo.databinding.ActivityBaseBinding

class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ikon status bar dibuat putih karena header aplikasi berwarna merah
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))

        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0) // bottom = 0
            insets
        }

        // HomeFragment sebagai fragment default
        replaceFragment(HomeFragment())

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.beranda -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.tracking -> {
                    replaceFragment(TrackingFragment())
                    true
                }
                R.id.panduan -> {
                    replaceFragment(PanduanFragment())
                    true
                }
                R.id.keluar -> {
                    // Keluar bukan Fragment, cukup tampilkan dialog logout
                    showLogoutDialog()
                    false // false: tab Keluar tidak ikut terpilih
                }
                else -> false // false jika item tidak dikenal
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            // .addToBackStack(null) sengaja dinonaktifkan: saat back langsung keluar aplikasi
            .commit()
    }

    // Dipanggil dari Fragment untuk pindah tab (mis. tombol "Buka SOP" atau SOS)
    fun bukaTab(menuId: Int) {
        binding.bottomNavView.selectedItemId = menuId
    }

    // Option Menu Toolbar (cara Modul P5). Toolbar-nya dipasang di tiap Fragment
    // dengan setSupportActionBar(), jadi menu ini ikut tampil di semua tab.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifikasi -> {
                showNotifikasiDialog()
                true
            }
            R.id.action_info_bmkg -> {
                startActivity(Intent(this, WebViewActivity::class.java))
                true
            }
            R.id.action_tentang -> {
                AlertDialog.Builder(this)
                    .setTitle("Tentang AZENKIVO")
                    .setMessage("AZENKIVO v1.0\nSistem Keselamatan & Tanggap Darurat\nEdisi Siaga Indonesia")
                    .setIcon(R.drawable.ic_azenkivo_logo)
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showNotifikasiDialog() {
        // Data notifikasi dummy (belum ada backend)
        val daftarNotifikasi = arrayOf(
            "Peringatan Gempa M 5.2 - 12 km barat laut",
            "Cuaca ekstrem: hujan lebat sore ini",
            "Latihan evakuasi gedung hari Jumat 09.00"
        )
        AlertDialog.Builder(this)
            .setTitle("Notifikasi")
            .setItems(daftarNotifikasi, null)
            .setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showLogoutDialog() {
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { dialog, _ ->
                sharedPref.edit().clear().apply()
                dialog.dismiss()

                // Kembali ke Login dan bersihkan seluruh back stack
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
