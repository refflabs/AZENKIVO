package com.example.azenkivo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.azenkivo.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi ViewBinding
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tahan splash 2 detik tanpa membuat layar macet
        lifecycleScope.launch {
            delay(2000) // simulasi pengambilan data selama 2 detik

            // Cek status login di SharedPreferences
            val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
            val isLogin = sharedPref.getBoolean("isLogin", false)

            // Sudah login -> MainActivity, belum login -> LoginActivity
            val target = if (isLogin) MainActivity::class.java else LoginActivity::class.java
            startActivity(Intent(this@SplashActivity, target))
            finish()
        }
    }
}
