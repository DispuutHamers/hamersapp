package nl.ecci.Hamers.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;
import nl.ecci.Hamers.MainActivity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendPostRequest extends AsyncTask<String, String, String> {

    public static final String QUOTEURL = "/quote";
    public static final String EVENTURL = "/event";
    public static final String BEERURL = "/beer";
    public static final String NEWSURL = "/news";
    public static final String MOTIEURL = "/motions";
    public static final String REVIEWURL = "/review";
    public static final String SIGNUPURL = "/signup";
    private final SharedPreferences prefs;
    private final String type;
    private final String urlParams;
    private final Context mContext;

    public SendPostRequest(Context context, String type, SharedPreferences s, String urlParams) {
        mContext = context;
        this.type = type;
        prefs = s;
        this.urlParams = urlParams;
    }

    protected String doInBackground(String... params) {
        int response = -1;
        try {
            URL url = new URL(MainActivity.baseURL + prefs.getString("apikey", "a") + type);
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
        if (result.equals("201")) {
            Activity activity = (Activity) mContext;

            if (!(activity instanceof MainActivity)) {
                activity.finish();
            }

            switch (type) {
                case QUOTEURL: {
                    GetJson g = new GetJson((Activity) mContext, MainActivity.quoteListFragment, GetJson.QUOTEURL, prefs);
                    g.execute();
                    break;
                }
                case EVENTURL:
                case SIGNUPURL: {
                    GetJson g = new GetJson((Activity) mContext, MainActivity.eventFragment, GetJson.EVENTURL, prefs);
                    g.execute();
                    break;
                }
                case BEERURL: {
                    GetJson g = new GetJson((Activity) mContext, MainActivity.beerFragment, GetJson.BEERURL, prefs);
                    g.execute();
                    break;
                }
                case REVIEWURL: {
                    GetJson g = new GetJson((Activity) mContext, null, GetJson.REVIEWURL, prefs);
                    g.execute();
                    break;
                }
            }

            Toast.makeText(mContext, "Item posted!", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("----------" + result);
            Toast.makeText(mContext, "Item not posted, try again later...", Toast.LENGTH_SHORT).show();
        }

    }
}
