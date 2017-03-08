package nl.ecci.hamers

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatDelegate
import android.support.v7.preference.Preference
import android.support.v7.preference.Preference.OnPreferenceClickListener
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import com.nostra13.universalimageloader.core.ImageLoader
import nl.ecci.hamers.loader.Loader

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.preferences)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val view = view

        val clearImageCache = findPreference(KEY_PREF_CLEAR_IMAGE_CACHE)
        clearImageCache.onPreferenceClickListener = OnPreferenceClickListener {
            ImageLoader.getInstance().clearMemoryCache()
            ImageLoader.getInstance().clearDiskCache()

            if (view != null) {
                Snackbar.make(view, R.string.clear_image_cache_snackbar, Snackbar.LENGTH_SHORT).show()
            }
            true
        }

        val clearStorage = findPreference(KEY_PREF_CLEAR_STORAGE)
        clearStorage.onPreferenceClickListener = OnPreferenceClickListener {
            prefs.edit().remove(Loader.QUOTEURL).apply()
            prefs.edit().remove(Loader.USERURL).apply()
            prefs.edit().remove(Loader.EVENTURL).apply()
            prefs.edit().remove(Loader.NEWSURL).apply()
            prefs.edit().remove(Loader.BEERURL).apply()
            prefs.edit().remove(Loader.REVIEWURL).apply()
            prefs.edit().remove(Loader.MEETINGURL).apply()
            prefs.edit().remove(Loader.WHOAMIURL).apply()

            if (view != null) {
                Snackbar.make(view, R.string.clear_storage_snackbar, Snackbar.LENGTH_SHORT).show()
            }
            true
        }

        val refreshApp = findPreference(KEY_PREF_REFRESH_APP)
        refreshApp.onPreferenceClickListener = OnPreferenceClickListener {
            activity.finish()
            startActivity(activity.intent)
            true
        }

        val night_mode = findPreference(KEY_PREF_NIGHT_MODE)
        night_mode.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            AppCompatDelegate.setDefaultNightMode(MainActivity.getNightModeInt(newValue as String))
            prefs.edit().putString(KEY_PREF_NIGHT_MODE, newValue).apply()
            activity.recreate()
            true
        }

        val getAllDataPreference = findPreference(KEY_PREF_GET_ALL_DATA)
        getAllDataPreference.onPreferenceClickListener = OnPreferenceClickListener {
            Loader.getAllData(context)

            if (view != null) {
                Snackbar.make(view, R.string.get_all_data_snackbar, Snackbar.LENGTH_SHORT).show()
            }
            true
        }
    }

    companion object {
        private val KEY_PREF_CLEAR_IMAGE_CACHE = "clear_image_cache"
        private val KEY_PREF_CLEAR_STORAGE = "clear_storage"
        private val KEY_PREF_REFRESH_APP = "refresh_app"
        private val KEY_PREF_NIGHT_MODE = "night_mode"
        private val KEY_PREF_GET_ALL_DATA = "get_all_data"
    }
}
