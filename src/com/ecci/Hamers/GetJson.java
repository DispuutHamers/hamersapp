package com.ecci.Hamers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import com.ecci.Hamers.Fragments.QuoteListFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class GetJson extends AsyncTask<String, String, String> {
    public static final String baseURL = "http://zondersikkel.nl/api/v1/";
    public static final String QUOTE = "/quote.json";

    private Fragment f;
    private String type;
    private SharedPreferences s;

    public GetJson(Fragment f, String type, SharedPreferences s){
        this.f = f;
        this.type = type;
        this.s = s;
    }

    protected String doInBackground(String... params) {
        BufferedReader reader;
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(baseURL + s.getString("apikey", "a") + type);
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
        }
        return buffer.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONArray json = new JSONArray(result);
            if(f instanceof QuoteListFragment) {
                ((QuoteListFragment) f).populateList(json);
            }
        } catch (JSONException e) {
            System.out.println("error parsing json");
        }

    }
}
