package nl.ecci.Hamers.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;
import nl.ecci.Hamers.MainActivity;
import nl.ecci.Hamers.R;

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
    public static final String GCMURL = "/register";
    private final SharedPreferences prefs;
    private final String type;
    private final String urlParams;
    private final View view;
    private final View parentView;
    private final Context context;

    public SendPostRequest(Context context, View view, View parentView, String type, SharedPreferences prefs, String urlParams) {
        this.context = context;
        this.view = view;
        this.parentView = parentView;
        this.type = type;
        this.prefs = prefs;
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
        if (result.equals("201") || result.equals("200")) {
            Activity activity = (Activity) context;

            if (!(activity instanceof MainActivity) && activity != null) {
                activity.finish();
            }

            switch (type) {
                case QUOTEURL: {
                    GetJson g = new GetJson((Activity) context, MainActivity.QUOTE_FRAGMENT, GetJson.QUOTEURL, prefs);
                    g.execute();
                    break;
                }
                case EVENTURL:
                case SIGNUPURL: {
                    GetJson g = new GetJson((Activity) context, MainActivity.EVENT_FRAGMENT, GetJson.EVENTURL, prefs);
                    g.execute();
                    break;
                }
                case BEERURL: {
                    GetJson g = new GetJson((Activity) context, MainActivity.BEER_FRAGMENT, GetJson.BEERURL, prefs);
                    g.execute();
                    break;
                }
                case REVIEWURL: {
                    GetJson g = new GetJson((Activity) context, null, GetJson.REVIEWURL, prefs);
                    g.execute();
                    break;
                }
            }

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
