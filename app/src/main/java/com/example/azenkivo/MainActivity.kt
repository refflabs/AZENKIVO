package com.example.azenkivo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.azenkivo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // 1. Terapkan ViewBinding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inisialisasi ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup Toolbar sebagai ActionBar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.app_name)
            setDisplayShowTitleEnabled(true)
        }

        // Ambil username dari SharedPreferences "user_pref" (Latihan Modul P6 5.1)
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        val username = sharedPref.getString("username", "Tim Siaga")
        binding.tvGreeting.text = "Halo, $username!"

        // 3. Terapkan Intent: Dari MainActivity ke DetailActivity
        binding.btnToDetail.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
        }

        // 3. Terapkan AlertDialog pada halaman MainActivity dengan tombol LogOut
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    // Inflate menu toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Handle item klik di toolbar menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            R.id.action_detail -> {
                startActivity(Intent(this, DetailActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_logout_title))
            .setMessage(getString(R.string.dialog_logout_message))
            .setIcon(R.drawable.ic_azenkivo_logo)
            .setCancelable(false)
            // Ketika "Ya" maka akan pindah ke Login dan hapus status login
            .setPositiveButton(getString(R.string.dialog_logout_yes)) { _, _ ->
                // Hapus status login dari SharedPreferences
                val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.clear()
                editor.apply()

                Toast.makeText(this, "Berhasil Keluar", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
            // Jika "Tidak" maka akan tetap berada di halaman MainActivity
            .setNegativeButton(getString(R.string.dialog_logout_no)) { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Tetap berada di Beranda", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }
}
