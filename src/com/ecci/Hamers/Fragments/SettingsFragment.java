package com.ecci.Hamers.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;
import com.ecci.Hamers.MainActivity;
import com.ecci.Hamers.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("apikey")){
            if(this.getActivity() instanceof MainActivity){
                ((MainActivity) this.getActivity()).loadData();
            }
        }

    }
}
