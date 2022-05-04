package com.cbruinsm.tailwindtwo.menu
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.preference.DialogPreference
import androidx.preference.PreferenceFragmentCompat
import com.cbruinsm.tailwindtwo.R

/**
 * SettingsFragment
 * Allows shared preferences
 */
class SettingsFragment : PreferenceFragmentCompat(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_fragment, rootKey)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }
}