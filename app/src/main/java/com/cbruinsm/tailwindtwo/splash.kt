package com.cbruinsm.tailwindtwo

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

/**
 * Splash Screen
 */
class splash : AppCompatActivity() {
    private val prefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (prefs.getBoolean(TailWindApp.NIGHT_MODE, true)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed( {
            val startApp = Intent(this,MainActivity::class.java)
            startActivity(startApp)
        },1000)
    }
}