package nl.ecci.hamers.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;

public class SendPostRequest extends AsyncTask<String, String, String> {
    private final SharedPreferences prefs;
    private final String dataURL;
    private final String dataKEY;
    private final String urlParams;
    private final View view;
    private final View parentView;
    private final Context context;

    public SendPostRequest(Context context, View view, View parentView, String dataURL, String dataKEY, SharedPreferences prefs, String urlParams) {
        this.context = context;
        this.view = view;
        this.parentView = parentView;
        this.dataURL = dataURL;
        this.dataKEY = dataKEY;
        this.prefs = prefs;
        this.urlParams = urlParams;
    }

    protected String doInBackground(String... params) {
        int response = -1;
        try {
            URL url = new URL(MainActivity.baseURL + prefs.getString("apikey", "a") + dataURL);
            System.out.println(url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("charset", "utf-8");
            con.setRequestProperty("Content-Length", "" + Integer.toString(urlParams.getBytes().length));
            con.setUseCaches(false);

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParams);
            wr.flush();
            wr.close();

            response = con.getResponseCode();
            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(response);
    }

    protected void onPostExecute(String result) {
        Activity activity = (Activity) context;

        if (result.equals("500")) {
            if (view != null && context != null) {
                Snackbar.make(view, context.getResources().getString(R.string.api_stuk), Snackbar.LENGTH_SHORT).show();
            } else if (parentView != null && context != null) {
                Snackbar.make(parentView, context.getResources().getString(R.string.api_stuk), Snackbar.LENGTH_SHORT).show();
            } else if (context != null) {
                Toast.makeText(context, context.getResources().getString(R.string.api_stuk), Toast.LENGTH_SHORT).show();
            }
        } else if (result.equals("201") || result.equals("200")) {
            if (!(activity instanceof MainActivity) && activity != null) {
                activity.finish();
            }

            DataManager.getData(context, prefs, dataURL, dataKEY);

            if (view != null && context != null) {
                Snackbar.make(view, context.getResources().getString(R.string.posted), Snackbar.LENGTH_SHORT).show();
            } else if (parentView != null && context != null) {
                Snackbar.make(parentView, context.getResources().getString(R.string.posted), Snackbar.LENGTH_SHORT).show();
            } else if (context != null) {
                Toast.makeText(context, context.getResources().getString(R.string.posted), Toast.LENGTH_SHORT).show();
            }
        } else if (context != null) {
            if (view != null) {
                Snackbar.make(view, context.getResources().getString(R.string.not_posted), Snackbar.LENGTH_SHORT).show();
            } else if (parentView != null) {
                Snackbar.make(parentView, context.getResources().getString(R.string.not_posted), Snackbar.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.not_posted), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
