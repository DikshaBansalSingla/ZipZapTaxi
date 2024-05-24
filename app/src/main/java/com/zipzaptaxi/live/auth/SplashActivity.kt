package com.zipzaptaxi.live.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.zipzaptaxi.live.cache.getToken
import com.zipzaptaxi.live.databinding.ActivitySplashBinding
import com.zipzaptaxi.live.home.MainActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var splashBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)


        Handler(Looper.getMainLooper()).postDelayed({

            if (getToken(this).isNullOrEmpty()) {
                val intent = Intent(this, OnBoardActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 2000)

    }
}