package nl.ecci.hamers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;

import nl.ecci.hamers.helpers.DataManager;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        Preference clear_image_cache = findPreference("clear_image_cache");
        clear_image_cache.setOnPreferenceClickListener(
                new OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference preference) {
                        ImageLoader.getInstance().clearMemoryCache();
                        ImageLoader.getInstance().clearDiskCache();

                        View view = getView();
                        if (view != null) {
                            Snackbar.make(view, R.string.clear_image_cache_snackbar, Snackbar.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                }
        );

        Preference clear_storage = findPreference("clear_storage");
        clear_storage.setOnPreferenceClickListener(
                new OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference preference) {
                        prefs.edit().remove(DataManager.QUOTEKEY).apply();
                        prefs.edit().remove(DataManager.USERKEY).apply();
                        prefs.edit().remove(DataManager.EVENTKEY).apply();
                        prefs.edit().remove(DataManager.NEWSKEY).apply();
                        prefs.edit().remove(DataManager.BEERKEY).apply();
                        prefs.edit().remove(DataManager.REVIEWKEY).apply();
                        prefs.edit().remove(DataManager.WHOAMIKEY).apply();
                        prefs.edit().remove(DataManager.SIGNUPKEY).apply();

                        View view = getView();
                        if (view != null) {
                            Snackbar.make(view, R.string.clear_storage_snackbar, Snackbar.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                }
        );

        Preference refresh_app = findPreference("refresh_app");
        refresh_app.setOnPreferenceClickListener(
                new OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference preference) {
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                        return true;
                    }
                }
        );
    }
}
