package nl.ecci.Hamers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SendPostRequest extends AsyncTask<String, String, String> {

    private static final String baseurl = "http://zondersikkel.nl/api/v1/";
    public static final String QUOTEURL = "/quote";
    public static final String EVENTUTL = "/event";
    public static final String BEERURL = "/beer";
    public static final String MOTIEURL = "/motion";
    public static final String REVIEWURL = "/review";
    private SharedPreferences prefs;
    private String type;
    private String urlParams;
    private Context mContext;

    public SendPostRequest(Context context, String type, SharedPreferences s, String urlParams) {
        prefs = s;
        this.type = type;
        this.urlParams = urlParams;
        mContext = context;
    }

    protected String doInBackground(String... params) {
        int response = -1;
        try {
            URL url = new URL(baseurl + prefs.getString("apikey", "a") + type);
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

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(response);
    }

    protected void onPostExecute(String result) {
        if (result.equals("201")) {
            Toast.makeText(mContext, "Item posted!", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println(result);
            Toast.makeText(mContext, "Item not posted, try again later...", Toast.LENGTH_SHORT).show();
        }

    }
}
