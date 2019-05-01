package nl.ecci.hamers.ui.fragments

import android.content.Context
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceClickListener
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import nl.ecci.hamers.R
import nl.ecci.hamers.data.Loader
import nl.ecci.hamers.ui.activities.MainActivity

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.preferences)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val view = view

        val clearImageCache = findPreference(KEY_PREF_CLEAR_IMAGE_CACHE)
        clearImageCache.onPreferenceClickListener = OnPreferenceClickListener {
            if (view != null) {
                Snackbar.make(view, R.string.clear_image_cache_snackbar, Snackbar.LENGTH_SHORT).show()
            }
            true
        }

        val clearStorage = findPreference(KEY_PREF_CLEAR_STORAGE)
        clearStorage.onPreferenceClickListener = OnPreferenceClickListener {
            for (url in Loader.urls) {
                prefs.edit().remove(url).apply()
            }

            if (view != null) {
                Snackbar.make(view, R.string.clear_storage_snackbar, Snackbar.LENGTH_SHORT).show()
            }
            true
        }

        val refreshApp = findPreference(KEY_PREF_REFRESH_APP)
        refreshApp.onPreferenceClickListener = OnPreferenceClickListener {
            activity?.finish()
            startActivity(activity?.intent)
            true
        }

        val night_mode = findPreference(KEY_PREF_NIGHT_MODE)
        night_mode.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            AppCompatDelegate.setDefaultNightMode(MainActivity.getNightModeInt(newValue as String))
            prefs.edit().putString(KEY_PREF_NIGHT_MODE, newValue).apply()
            activity?.recreate()
            true
        }

        val getAllDataPreference = findPreference(KEY_PREF_GET_ALL_DATA)
        getAllDataPreference.onPreferenceClickListener = OnPreferenceClickListener {
            Loader.getAllData(context as Context)

            if (view != null) {
                Snackbar.make(view, R.string.get_all_data_snackbar, Snackbar.LENGTH_SHORT).show()
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.title = resources.getString(R.string.navigation_item_settings)
    }

    companion object {
        private const val KEY_PREF_CLEAR_IMAGE_CACHE = "clear_image_cache"
        private const val KEY_PREF_CLEAR_STORAGE = "clear_storage"
        private const val KEY_PREF_REFRESH_APP = "refresh_app"
        private const val KEY_PREF_NIGHT_MODE = "night_mode"
        private const val KEY_PREF_GET_ALL_DATA = "get_all_data"
    }
}
