package com.thatwaz.weathercast.view.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.thatwaz.weathercast.view.MainActivity
import com.thatwaz.weathercast.R

class SplashActivity : AppCompatActivity() {

    private val splashDelayMs = 2000L // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashView = findViewById<View>(R.id.splash) // Replace with the actual ID of your splash view
        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        splashView.startAnimation(animation)

        // Use a Handler to post a delayed runnable
        Handler(Looper.getMainLooper()).postDelayed({
            // Start the main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish the splash activity to prevent going back to it
        }, splashDelayMs)
    }
}



