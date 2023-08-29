package com.thatwaz.weathercast.view.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.thatwaz.weathercast.view.MainActivity
import com.thatwaz.weathercast.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val splashDelayMs = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashView = findViewById<View>(R.id.splash)
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        splashView.startAnimation(animation)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, splashDelayMs)
    }
}



