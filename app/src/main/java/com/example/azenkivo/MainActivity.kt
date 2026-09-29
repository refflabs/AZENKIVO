package com.example.azenkivo

import android.content.Intent
import android.os.Bundle
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

        // Tangkap data username dari LoginActivity
        val username = intent.getStringExtra("EXTRA_USERNAME") ?: "Tim Siaga"
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

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_logout_title))
            .setMessage(getString(R.string.dialog_logout_message))
            .setIcon(R.drawable.ic_azenkivo_logo)
            .setCancelable(false)
            // Ketika "Ya" maka akan pindah ke Login
            .setPositiveButton(getString(R.string.dialog_logout_yes)) { _, _ ->
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
