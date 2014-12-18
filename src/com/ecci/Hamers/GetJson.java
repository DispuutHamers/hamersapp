package com.ecci.Hamers;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Base64;
import com.ecci.Hamers.Fragments.BeerFragment;
import com.ecci.Hamers.Fragments.EventFragment;
import com.ecci.Hamers.Fragments.QuoteListFragment;
import com.ecci.Hamers.Fragments.UserFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class GetJson extends AsyncTask<String, String, String> {
    public static final String baseURL = "http://zondersikkel.nl/api/v1/";
    public static final String QUOTE = "/quote.json";
    public static final String USER = "/user.json";
    public static final String EVENT = "/event.json";
    public static final String BEER = "/beer.json";

    private Fragment f;
    private String type;
    private SharedPreferences prefs;

    public GetJson(Fragment f, String type, SharedPreferences s){
        this.f = f;
        this.type = type;
        this.prefs = s;
    }

    protected String doInBackground(String... params) {
        BufferedReader reader;
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(baseURL + prefs.getString("apikey", "a") + type);
            System.out.println(url);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            reader.close();
        } catch(MalformedURLException e){
            System.out.println("Unable to retreive data - Url not correctly formulated");
        } catch(IOException e){
            System.out.println("Unable to retreive data - input/output error");
            e.printStackTrace();
        }
        if (type == USER) {
            try {
                downloadProfilepictures(new JSONArray(buffer.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            };
        }
        return buffer.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONArray json = new JSONArray(result);
            // Quotelist fragment
            if(f instanceof QuoteListFragment && type == USER) {
                prefs.edit().putString("userData", result).apply();
                ((QuoteListFragment) f).getQuotes();
            }else if (f instanceof QuoteListFragment){
                ((QuoteListFragment) f).populateList(json);
            }
            // User fragment
            else if (f instanceof UserFragment) {
                prefs.edit().putString("userData", result).apply();
                ((UserFragment) f).populateList(json);
            }
            // Event fragment
            else if (f instanceof EventFragment) {
                ((EventFragment) f).populateList(json);
            }
            // Beer fragment
            else if (f instanceof BeerFragment)
                ((BeerFragment) f).populateList(json);
        } catch (JSONException e) {
            System.out.println("error parsing json");
        }

    }

    private void downloadProfilepictures(JSONArray users){
        for (int i = 0; i < users.length(); i++) {
            try {
                URL url = new URL("http://gravatar.com/avatar/" + MD5Util.md5Hex( users.getJSONObject(i).getString("email")));
                InputStream in = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                while (-1!=(n=in.read(buf))){out.write(buf, 0, n);}
                out.close(); in.close();
                prefs.edit().putString("userpic-" + users.getJSONObject(i).getString("id"), Base64.encodeToString(out.toByteArray(), Base64.DEFAULT)).apply();
                System.out.println("STORED USERPICS");
                //For restoring byte[] array = Base64.decode(stringFromSharedPrefs, Base64.DEFAULT);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
