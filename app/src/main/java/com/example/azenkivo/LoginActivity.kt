package com.example.azenkivo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.azenkivo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // Menyimpan status: kata sandi sedang terlihat atau tidak
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ikon status bar putih karena latar halaman login gelap (navy)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))

        // Inisialisasi ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tombol MASUK SISTEM (aturan Modul P6: login berhasil jika username == password)
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (username.isNotEmpty() && username == password) {
                simpanSesiDanMasuk(username)
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Login Gagal")
                    .setMessage("Silahkan coba lagi")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }

        // Tombol Akses Cepat Tamu: tetap disimpan sebagai login dengan username "Tamu"
        binding.btnTamu.setOnClickListener {
            simpanSesiDanMasuk("Tamu")
        }

        // Ikon mata: tampilkan / sembunyikan kata sandi
        binding.ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.ivTogglePassword.setImageResource(R.drawable.ic_eye_off)
            } else {
                binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.ivTogglePassword.setImageResource(R.drawable.ic_eye)
            }
            // Kursor dipindah ke akhir teks supaya tidak loncat ke depan
            binding.etPassword.setSelection(binding.etPassword.text.length)
        }

        // Lupa Sandi: cukup info lewat dialog (tidak ada backend)
        binding.tvLupaSandi.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Lupa Sandi")
                .setMessage("Hubungi koordinator tim siaga untuk mengatur ulang kata sandi.\n\nMode demo: kata sandi sama dengan ID Tim.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // Daftar Relawan: belum tersedia di versi ini
        binding.tvDaftarRelawan.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Daftar Relawan")
                .setMessage("Pendaftaran relawan belum tersedia. Sementara ini gunakan Akses Cepat Tamu.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    // Simpan isLogin dan username ke SharedPreferences, lalu pindah ke halaman utama
    private fun simpanSesiDanMasuk(username: String) {
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("isLogin", true)
        editor.putString("username", username)
        editor.apply()

        Toast.makeText(this, "Selamat datang, $username!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, BaseActivity::class.java))
        finish() // ke BaseActivity (Modul P7 2.4), tutup Login supaya tombol back tidak kembali ke sini
    }
}
