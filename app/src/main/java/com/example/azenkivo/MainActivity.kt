package com.example.azenkivo

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.azenkivo.Home.HomeFragment
import com.example.azenkivo.Panduan.PanduanFragment
import com.example.azenkivo.Profile.ProfileFragment
import com.example.azenkivo.Tracking.TrackingFragment
import com.example.azenkivo.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// Halaman utama setelah login
class MainActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: ActivityMainBinding

    // Senter (lampu kilat kamera belakang)
    private val cameraManager by lazy { getSystemService(Context.CAMERA_SERVICE) as CameraManager }
    private var kameraSenterId: String? = null
    private var senterNyala = false
    private val torchCallback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            if (cameraId == kameraSenterId) {
                senterNyala = enabled
                invalidateOptionsMenu()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Status bar transparan
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0) // bottom = 0
            insets
        }

        // HomeFragment sebagai fragment default
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }
        terapkanWarnaHeader(binding.bottomNavView.selectedItemId)

        kameraSenterId = cariKameraSenter()
        kameraSenterId?.let { cameraManager.registerTorchCallback(torchCallback, null) }

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.beranda -> {
                    replaceFragment(HomeFragment())
                    terapkanWarnaHeader(R.id.beranda)
                    true
                }
                R.id.tracking -> {
                    replaceFragment(TrackingFragment())
                    terapkanWarnaHeader(R.id.tracking)
                    true
                }
                R.id.panduan -> {
                    replaceFragment(PanduanFragment())
                    terapkanWarnaHeader(R.id.panduan)
                    true
                }
                R.id.profil -> {
                    replaceFragment(ProfileFragment())
                    terapkanWarnaHeader(R.id.profil)
                    true
                }
                else -> false // false jika item tidak dikenal
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            // .addToBackStack sengaja dinonaktifkan
            .commit()
    }

    // Area status bar mengikuti bagian atas tab aktif
    private fun terapkanWarnaHeader(menuId: Int) {
        val warna = when (menuId) {
            R.id.beranda -> R.color.brand
            R.id.tracking -> R.color.map_bg
            else -> R.color.canvas
        }
        binding.main.setBackgroundColor(ContextCompat.getColor(this, warna))
        WindowCompat.getInsetsController(window, binding.main).isAppearanceLightStatusBars =
            (menuId != R.id.beranda)
    }

    // Dipanggil dari Fragment untuk pindah tab
    fun bukaTab(menuId: Int) {
        binding.bottomNavView.selectedItemId = menuId
    }

    // Option Menu Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    // Judul menu senter mengikuti status nyala/mati
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_senter)?.setTitle(
            if (senterNyala) R.string.menu_senter_off else R.string.menu_senter_on
        )
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifikasi -> {
                showNotifikasiDialog()
                true
            }
            R.id.action_senter -> {
                ubahSenter()
                true
            }
            R.id.action_pesan_darurat -> {
                kirimPesanDarurat()
                true
            }
            R.id.action_kontak_keluarga -> {
                teleponKontakKeluarga()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun cariKameraSenter(): String? = try {
        cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    } catch (e: Exception) {
        null
    }

    private fun ubahSenter() {
        val id = kameraSenterId
        if (id == null) {
            Toast.makeText(this, R.string.senter_tidak_ada, Toast.LENGTH_SHORT).show()
            return
        }
        try {
            cameraManager.setTorchMode(id, !senterNyala)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.senter_tidak_ada, Toast.LENGTH_SHORT).show()
        }
    }

    // Bagikan pesan minta tolong lewat WhatsApp, SMS, dll.
    private fun kirimPesanDarurat() {
        val nama = getSharedPreferences("user_pref", MODE_PRIVATE)
            .getString("username", null)?.takeIf { it.isNotBlank() } ?: ""
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.pesan_darurat_isi, nama).replace("  ", " "))
        }
        startActivity(Intent.createChooser(intent, getString(R.string.pesan_darurat_pilih)))
    }

    // Telepon nomor kontak keluarga yang diisi di Profil
    private fun teleponKontakKeluarga() {
        val kontak = getSharedPreferences("user_pref", MODE_PRIVATE).getString("kontak_darurat", null)
        val nomor = kontak?.let { Regex("""\+?\d[\d\s-]{5,}\d""").find(it)?.value }
            ?.replace(Regex("""[\s-]"""), "")
        if (nomor != null) {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$nomor")))
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.kontak_kosong_judul)
                .setMessage(R.string.kontak_kosong_pesan)
                .setPositiveButton(R.string.kontak_kosong_isi) { dialog, _ ->
                    dialog.dismiss()
                    bukaTab(R.id.profil)
                }
                .setNegativeButton(R.string.kontak_kosong_batal) { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    // Matikan senter saat aplikasi ditutup
    override fun onDestroy() {
        kameraSenterId?.let {
            cameraManager.unregisterTorchCallback(torchCallback)
            if (senterNyala) try { cameraManager.setTorchMode(it, false) } catch (e: Exception) { }
        }
        super.onDestroy()
    }

    private fun showNotifikasiDialog() {
        // Data notifikasi dummy
        val daftarNotifikasi = arrayOf(
            "Peringatan Gempa M 5.2 - 12 km barat laut",
            "Cuaca ekstrem: hujan lebat sore ini",
            "Latihan evakuasi gedung hari Jumat 09.00"
        )
        MaterialAlertDialogBuilder(this)
            .setTitle("Notifikasi")
            .setItems(daftarNotifikasi, null)
            .setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Syarat tugas: AlertDialog Logout di MainActivity
    fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_logout_title))
            .setMessage(getString(R.string.dialog_logout_message))
            .setIcon(R.drawable.ic_app_mark)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.dialog_logout_yes)) { dialog, _ ->
                val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
                val tutorialSeen = sharedPref.getBoolean("tutorial_seen", false)
                val editor = sharedPref.edit()
                editor.clear()
                editor.putBoolean("tutorial_seen", tutorialSeen) // tutorial tidak muncul lagi
                editor.apply()
                dialog.dismiss()

                Toast.makeText(this, R.string.main_logout_toast, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(R.string.dialog_logout_no)) { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, R.string.main_stay_toast, Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}
