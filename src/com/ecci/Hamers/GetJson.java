package com.ecci.Hamers;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class GetJson extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... params) {
        System.out.println("Getting JSON");
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(params[0]);
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
            System.out.println(json);
            for(int i = 0; i< json.length(); i++){
                JSONObject temp = json.getJSONObject(i);
                System.out.println(temp.getString("text"));
            }
        } catch (JSONException e) {
            System.out.println("error parsing json");
        }

    }
}
