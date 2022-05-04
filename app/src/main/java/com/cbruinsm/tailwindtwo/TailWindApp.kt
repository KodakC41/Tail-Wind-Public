package com.cbruinsm.tailwindtwo

import android.app.Application

class TailWindApp :  Application() {
    companion object {
        private lateinit var instance: TailWindApp
        const val YOUR_NAME = "your_name"
        const val NIGHT_MODE = "day_night"
        const val CURRENT_LOCATION = "third_pref"
        const val PATH_POINTS = "flight_path"
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
    }
}