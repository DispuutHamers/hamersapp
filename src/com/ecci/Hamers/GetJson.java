package com.ecci.Hamers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.widget.Toast;
import com.ecci.Hamers.Fragments.BeerFragment;
import com.ecci.Hamers.Fragments.EventFragment;
import com.ecci.Hamers.Fragments.QuoteListFragment;
import com.ecci.Hamers.Fragments.UserFragment;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class GetJson extends AsyncTask<String, String, String> {
    private static final boolean DEBUG = false;
    public static final String baseURL = "http://zondersikkel.nl/api/v1/";
    public static final String QUOTE = "/quote.json";
    public static final String USER = "/user.json";
    public static final String EVENT = "/event.json";
    public static final String BEER = "/beer.json";

    private Fragment f;
    private String type;
    private SharedPreferences prefs;
    private Context c;

    public GetJson(Context c, Fragment f, String type, SharedPreferences s) {
        this.f = f;
        this.type = type;
        this.prefs = s;
        this.c = c;
    }

    protected String doInBackground(String... params) {
        BufferedReader reader;
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(baseURL + prefs.getString("apikey", "a") + type);
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
        if (type == USER) {
            try {
                downloadProfilepictures(new JSONArray(buffer.toString()));
            } catch (JSONException e) {
                if (DEBUG) {
                    System.out.println("--------------------------JSONException!: ");
                    e.printStackTrace();
                }
                //todo mogelijk deze exceptie handlen
            }
            ;
        }
        if (type == BEER) {
            try {
                downloadBeerpictures(new JSONArray(buffer.toString()));
            } catch (JSONException e) {
                if (DEBUG) {
                    System.out.println("--------------------------JSONException!: ");
                    e.printStackTrace();
                }
                //todo mogelijk deze exceptie handlen
            }
        }
        return buffer.toString();
    }

    @Override
    protected void onPostExecute(String result) {

        if (result == null || result.equals("{}")) {
            Toast.makeText(c, c.getString(R.string.toast_downloaderror), Toast.LENGTH_SHORT).show();
        } else {
            if (type == USER) {
                prefs.edit().putString("userData", result).apply();
            }
            // Quotelist fragment
            if (f instanceof QuoteListFragment) {
                prefs.edit().putString("quoteData", result).apply();
                ((QuoteListFragment) f).populateList(prefs);
            }
            // User fragment
            else if (f instanceof UserFragment) {
                ((UserFragment) f).populateList(prefs);
            }
            // Event fragment
            else if (f instanceof EventFragment) {
                prefs.edit().putString("eventData", result).apply();
                ((EventFragment) f).populateList(prefs);
            }
            // Beer fragment
            else if (f instanceof BeerFragment) {
                prefs.edit().putString("beerData", result).apply();
                ((BeerFragment) f).populateList(prefs);
            }
        }

    }

    private void downloadProfilepictures(JSONArray users) {
        for (int i = 0; i < users.length(); i++) {
            try {
                if (DEBUG) {
                    System.out.println("downloading user picture " + i);
                }
                URL url = new URL("http://gravatar.com/avatar/" + MD5Util.md5Hex(users.getJSONObject(i).getString("email")));
                InputStream in = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                while (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                }
                out.close();
                in.close();
                prefs.edit().putString("userpic-" + users.getJSONObject(i).getString("id"), Base64.encodeToString(out.toByteArray(), Base64.DEFAULT)).apply();
                //For restoring byte[] array = Base64.decode(stringFromSharedPrefs, Base64.DEFAULT);
            }catch (IOException e) {
                if(DEBUG){System.out.println("--------------------------IOException!: "); e.printStackTrace();}
            } catch (JSONException e) {
                if(DEBUG){System.out.println("--------------------------JSONException!: "); e.printStackTrace();}
            }
        }
    }

    private void downloadBeerpictures(JSONArray beers) {
        for (int i = 0; i < beers.length(); i++) {
            try {
                if (DEBUG) {
                    System.out.println("downloading beer picture " + i);
                }
                URL url = new URL(beers.getJSONObject(i).getString("picture"));
                InputStream in = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                while (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                }
                out.close();
                in.close();
                prefs.edit().putString("beerpic-" + beers.getJSONObject(i).getString("name"), Base64.encodeToString(out.toByteArray(), Base64.DEFAULT)).apply();
            } catch (MalformedURLException e) {
                if (DEBUG) {
                    System.out.println("--------------------------Malformed URL!: ");
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                if (DEBUG) {
                    System.out.println("--------------------------JSONException!: ");
                    e.printStackTrace();
                }
            } catch (IOException e) {
                if (DEBUG) {
                    System.out.println("--------------------------IOException!: ");
                    e.printStackTrace();
                }
            }
            //For restoring byte[] array = Base64.decode(stringFromSharedPrefs, Base64.DEFAULT);

        }
    }

}
