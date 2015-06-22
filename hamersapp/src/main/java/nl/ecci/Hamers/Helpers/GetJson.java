package nl.ecci.Hamers.Helpers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import nl.ecci.Hamers.Beers.BeerFragment;
import nl.ecci.Hamers.Events.EventFragment;
import nl.ecci.Hamers.MainActivity;
import nl.ecci.Hamers.News.NewsFragment;
import nl.ecci.Hamers.Quotes.QuoteFragment;
import nl.ecci.Hamers.R;
import nl.ecci.Hamers.Users.UserFragment;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class GetJson extends AsyncTask<String, String, String> {
    public static final String QUOTEURL = "/quote.json";
    public static final String USERURL = "/user.json";
    public static final String EVENTURL = "/event.json";
    public static final String NEWSURL = "/news.json";
    public static final String BEERURL = "/beer.json";
    public static final String REVIEWURL = "/review.json";
    public static final String WHOAMIURL = "/whoami.json";
    private static final boolean DEBUG = false;
    private final Fragment f;
    private final String typeURL;
    private final SharedPreferences prefs;
    private final Activity a;

    public GetJson(Activity activity, Fragment fragment, String typeURL, SharedPreferences s) {
        this.f = fragment;
        this.typeURL = typeURL;
        this.prefs = s;
        this.a = activity;
    }

    protected String doInBackground(String... params) {
        BufferedReader reader;
        StringBuilder buffer = new StringBuilder();
        try {
            URL url = new URL(MainActivity.baseURL + prefs.getString(DataManager.APIKEYKEY, "a") + typeURL);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            reader.close();
        } catch (MalformedURLException e) {
            if (DEBUG) {
                System.out.println("--------------------------Malformed URL!: ");
                e.printStackTrace();
            }
            return null;
        } catch (IOException e) {
            if (DEBUG) {
                System.out.println("--------------------------IOException!: ");
                e.printStackTrace();
            }
            return null;
        }
        return buffer.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            try {
                JSONArray arr = new JSONArray(result);
                if (arr.getJSONObject(0).has("error")) {
                    ((MainActivity) a).loadData2(prefs, false);
                }
                if (a instanceof MainActivity) {
                    ((MainActivity) a).loadData2(prefs, true);
                }
                if (result.equals("{}")) {
                    Toast.makeText(a, a.getString(R.string.snackbar_downloaderror), Toast.LENGTH_SHORT).show();
                } else {
                    if (f instanceof QuoteFragment) {
                        prefs.edit().putString(DataManager.QUOTEKEY, result).apply();
                        MainActivity.QUOTE_FRAGMENT.populateList(prefs);
                    }
                    // User fragment
                    else if (f instanceof UserFragment) {
                        prefs.edit().putString(DataManager.USERKEY, result).apply();
                        MainActivity.USER_FRAGMENT.populateList(prefs);
                    }
                    // Event fragment
                    else if (f instanceof EventFragment) {
                        prefs.edit().putString(DataManager.EVENTKEY, result).apply();
                        MainActivity.EVENT_FRAGMENT.populateList(prefs);
                    } else if (f instanceof NewsFragment) {
                        prefs.edit().putString(DataManager.NEWSKEY, result).apply();
                        MainActivity.NEWS_FRAGMENT.populateList(prefs);
                    }
                    // Beer fragment
                    else if (f instanceof BeerFragment) {
                        prefs.edit().putString(DataManager.BEERKEY, result).apply();
                        GetJson g2 = new GetJson(a, null, REVIEWURL, prefs);
                        g2.execute();
                        MainActivity.BEER_FRAGMENT.populateList(prefs);
                    }
                    // Quote
                    else if (typeURL.equals(QUOTEURL)) {
                        prefs.edit().putString(DataManager.QUOTEKEY, result).apply();
                        MainActivity.QUOTE_FRAGMENT.populateList(prefs);
                    }
                    // Beer
                    else if (typeURL.equals(BEERURL)) {
                        prefs.edit().putString(DataManager.BEERKEY, result).apply();
                        MainActivity.BEER_FRAGMENT.populateList(prefs);
                    }
                    // Review
                    else if (typeURL.equals(REVIEWURL)) {
                        prefs.edit().putString(DataManager.REVIEWKEY, result).apply();
                    }
                    // WHOAMI
                    else if (typeURL.equals(WHOAMIURL)) {
                        prefs.edit().putString(DataManager.WHOAMIKEY, result).apply();
                    }
                }
            } catch (JSONException e) {
                ((MainActivity) a).loadData2(prefs, false);
            }
        } else {
            ((MainActivity) a).loadData2(prefs, false);
        }
    }
}
