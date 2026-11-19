package com.oudombun.chelfy.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oudombun.chelfy.R
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_sreen)
        lifecycleScope.launch {
            delay(1500)
            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            finish()
        }
    }
}