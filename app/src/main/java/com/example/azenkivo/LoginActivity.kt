package com.example.azenkivo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.azenkivo.databinding.ActivityLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Latar putih penuh
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        // Inisialisasi ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Padding bawah = tinggi keyboard bila muncul
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(bars.left, bars.top, bars.right, maxOf(bars.bottom, ime.bottom))
            insets
        }

        // Tombol Masuk Sistem
        binding.btnLogin.setOnClickListener { cobaLogin() }

        // Tombol "Selesai" di keyboard = tekan Masuk
        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                cobaLogin()
                true
            } else {
                false
            }
        }

        // Error di kolom hilang begitu pengguna mulai mengetik lagi
        binding.etUsername.doAfterTextChanged { binding.tilUsername.error = null }
        binding.etPassword.doAfterTextChanged { binding.tilPassword.error = null }

        // Tombol Tamu: tetap disimpan sebagai login dengan username "Tamu"
        binding.btnTamu.setOnClickListener {
            simpanSesiDanMasuk(getString(R.string.login_guest_name))
        }

        // Lupa Sandi: cukup info lewat dialog
        binding.tvLupaSandi.setOnClickListener {
            tampilkanInfo(R.string.login_forgot, R.string.login_forgot_message)
        }

        // Daftar Relawan: belum tersedia di versi ini
        binding.tvDaftarRelawan.setOnClickListener {
            tampilkanInfo(R.string.login_register, R.string.login_register_message)
        }
        // Lihat/sembunyikan sandi kini ditangani TextInputLayout
    }

    // Aturan Modul P6: login berhasil jika username == password
    private fun cobaLogin() {
        val username = binding.etUsername.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()

        // Validasi kolom kosong: error tampil di bawah kolomnya sendiri
        when {
            username.isEmpty() -> {
                tampilkanError(binding.tilUsername, R.string.login_err_username)
                return
            }
            password.isEmpty() -> {
                tampilkanError(binding.tilPassword, R.string.login_err_password)
                return
            }
        }

        if (username == password) {
            simpanSesiDanMasuk(username)
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.login_failed_title)
                .setMessage(R.string.login_failed_message)
                .setPositiveButton(R.string.dialog_ok) { dialog, _ ->
                    dialog.dismiss()
                    // Kosongkan sandi & fokus ke sana supaya bisa langsung ketik ulang
                    binding.etPassword.text?.clear()
                    binding.etPassword.requestFocus()
                }
                .show()
        }
    }

    private fun tampilkanError(kolom: TextInputLayout, @StringRes pesanRes: Int) {
        kolom.error = getString(pesanRes)
        kolom.editText?.requestFocus()
    }

    private fun tampilkanInfo(@StringRes judulRes: Int, @StringRes pesanRes: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle(judulRes)
            .setMessage(pesanRes)
            .setPositiveButton(R.string.dialog_ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Simpan isLogin dan username ke SharedPreferences
    private fun simpanSesiDanMasuk(username: String) {
        // Cegah tap ganda membuka MainActivity dua kali
        binding.btnLogin.isEnabled = false
        binding.btnTamu.isEnabled = false

        getSharedPreferences("user_pref", MODE_PRIVATE).edit {
            putBoolean("isLogin", true)
            putString("username", username)
        }

        // Syarat tugas: tampilkan hasil inputan login menggunakan Toast
        Toast.makeText(this, getString(R.string.login_toast_input, username), Toast.LENGTH_SHORT).show()
        // Syarat tugas: login berhasil -> MainActivity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
