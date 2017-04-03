package nl.ecci.hamers.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment

open class HamersFragment : Fragment() {
    var prefs : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        prefs = PreferenceManager.getDefaultSharedPreferences(activity)
    }

    override fun onPause() {
        super.onPause()
        ActivityCompat.invalidateOptionsMenu(activity)
    }
}