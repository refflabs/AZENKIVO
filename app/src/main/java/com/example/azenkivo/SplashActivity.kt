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

            val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
            val tutorialSeen = sharedPref.getBoolean("tutorial_seen", false)
            val isLogin = sharedPref.getBoolean("isLogin", false)

            // Pertama kali -> Tutorial (3 Fragment). Berikutnya -> Main / Login
            val target = when {
                !tutorialSeen -> TutorialActivity::class.java
                isLogin -> MainActivity::class.java
                else -> LoginActivity::class.java
            }
            startActivity(Intent(this@SplashActivity, target))
            finish()
        }
    }
}
