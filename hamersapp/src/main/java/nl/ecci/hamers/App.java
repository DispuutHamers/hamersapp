package nl.ecci.hamers;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String nightmode = prefs.getString("nightmode", "default");

        AppCompatDelegate.setDefaultNightMode(
                getNightModeInt(nightmode)
        );
    }

    @AppCompatDelegate.NightMode
    public static int getNightModeInt(String nightMode) {
        switch (nightMode) {
            case "auto":
                return AppCompatDelegate.MODE_NIGHT_AUTO;
            case "on":
                return AppCompatDelegate.MODE_NIGHT_YES;
            default:
                return AppCompatDelegate.MODE_NIGHT_NO;
        }
    }
}