package com.cbruinsm.tailwindtwo

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.cbruinsm.tailwindtwo.History.FlightHistory
import com.cbruinsm.tailwindtwo.TailWindApp.Companion.NIGHT_MODE
import com.cbruinsm.tailwindtwo.menu.Information
import com.cbruinsm.tailwindtwo.ui.main.plane.FlightSearch
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Main Fragment
 */
class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var FAB : FloatingActionButton
    private lateinit var HISTORY : FloatingActionButton


    private val prefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment

        supportActionBar?.show()
        val Navy = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, Navy)
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.let {
                it.title = when (destination.id) {
                    R.id.information2 ->getString(R.string.about)
                    R.id.settingsFragment -> getString(R.string.Settings)
                    else -> getString(R.string.app_name)
                }
            }
        }
        if (prefs.getBoolean(NIGHT_MODE, true)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }


        FAB  = findViewById(R.id.floatingActionButton)
        FAB.setOnClickListener {
            val dialogFragment = FlightSearch()
            dialogFragment.isCancelable = true
            dialogFragment.show(getSupportFragmentManager(),"My  Fragment")
        }
        HISTORY = findViewById(R.id.History)
        HISTORY.setOnClickListener {
            val history = FlightHistory()
            history.isCancelable = true
            history.show(getSupportFragmentManager(),"My  Fragment")
        }
    }

    override fun onResume() {
        super.onResume()
    }
    override fun onSupportNavigateUp(): Boolean {
        FAB.isVisible = true
        HISTORY.isVisible = true
        return Navigation.findNavController(this, R.id.fragmentContainerView2).navigateUp()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.in_flight_options, menu)
        return true
    }

    override fun onNavigateUp(): Boolean {
        FAB.isVisible = true
        return super.onNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Info-> {
                val dialogFragment = Information()
                dialogFragment.show(getSupportFragmentManager(),"My  Fragment")
                true
            }
            R.id.Settings_Nav-> {
                navHostFragment.navController.navigate(R.id.action_mapsFragment_to_settingsFragment)
                FAB.isVisible = false
                HISTORY.isVisible = false
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        if (prefs.getBoolean(NIGHT_MODE, true)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


}