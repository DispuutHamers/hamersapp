package nl.ecci.hamers.loader;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;

public final class Loader {
    // URL Appendices
    public static final String QUOTEURL = "quotes.json";
    public static final String USERURL = "users.json";
    public static final String EVENTURL = "events.json";
    public static final String UPCOMINGEVENTURL = "events?sorted=date-desc";
    public static final String NEWSURL = "news.json";
    public static final String BEERURL = "beers.json";
    public static final String REVIEWURL = "reviews.json";
    public static final String WHOAMIURL = "whoami";
    public static final String MEETINGURL = "meetings.json";
    public static final String SIGNUPURL = "signups.json";
    public static final String GCMURL = "register";
    public static final String STICKERURL = "stickers";
    // Data keys
    public static final String APIKEYKEY = "apikey";

    // URL
    private static final String baseURL = "https://zondersikkel.nl/api/v2/";
//    private static final String baseURL = "http://192.168.100.100:3000/api/v2/";

    public static void getData(final String dataURL, @NonNull final Context context, final SharedPreferences prefs, final GetCallback callback) {
        String url = baseURL + dataURL;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Loader-response", response);
                        prefs.edit().putString(dataURL, response).apply();
                        if (callback != null) {
                            callback.onSuccess(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Loader-error", error.toString());
                        if (callback != null) {
                            callback.onError(error);
                        }
                        handleErrorResponse(context, error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Token token=" + prefs.getString(APIKEYKEY, ""));
                return params;
            }
        };

        Log.d("Request", request.toString());
        Singleton.getInstance(context).addToRequestQueue(request);
    }

    public static void postOrPatchData(final String dataURL, JSONObject body, final int urlAppendix, @NonNull final Context context, final SharedPreferences prefs, final PostCallback callback) {
        String url = baseURL + dataURL;
        if (urlAppendix != -1) {
            url = baseURL + dataURL + "/" + urlAppendix;
        }

        Log.d("PostRequest: ", body.toString());

        JsonObjectRequest request = new JsonObjectRequest(url, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, context.getString(R.string.posted), Toast.LENGTH_SHORT).show();
                        if (callback != null) {
                            callback.onSuccess(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Loader-error", error.toString());
                        if (callback != null) {
                            callback.onError(error);
                        }
                        handleErrorResponse(context, error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Token token=" + prefs.getString(APIKEYKEY, ""));
                params.put("Content-Type", "Application/json");
                if (urlAppendix != -1) {
                    params.put("X-HTTP-Method-Override", "PATCH");
                }
                return params;
            }
        };
        Log.d("PostRequest: ", request.toString());
        Singleton.getInstance(context).addToRequestQueue(request);
    }

    private static void handleErrorResponse(@NonNull Context context, @NonNull VolleyError error) {
        if (error instanceof AuthFailureError) {
            // Wrong API key
            Toast.makeText(context, context.getString(R.string.auth_error), Toast.LENGTH_SHORT).show();
        } else if (error instanceof TimeoutError) {
            // Timeout
            Toast.makeText(context, context.getString(R.string.timeout_error), Toast.LENGTH_SHORT).show();
        } else if (error instanceof ServerError) {
            // Server error (500)
            Toast.makeText(context, context.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        } else if (error instanceof NoConnectionError) {
            // No network connection
            Toast.makeText(context, context.getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        } else if (error instanceof NetworkError) {
            // Network error
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        } else {
            // Other error
            Toast.makeText(context, context.getString(R.string.volley_error), Toast.LENGTH_SHORT).show();
        }
    }

    public static void getAllData(@NonNull Context context) {
        Loader.getData(Loader.QUOTEURL, context, MainActivity.prefs, null);
        Loader.getData(Loader.EVENTURL, context, MainActivity.prefs, null);
        Loader.getData(Loader.UPCOMINGEVENTURL, context, MainActivity.prefs, null);
        Loader.getData(Loader.NEWSURL, context, MainActivity.prefs, null);
        Loader.getData(Loader.BEERURL, context, MainActivity.prefs, null);
        Loader.getData(Loader.REVIEWURL, context, MainActivity.prefs, null);
        Loader.getData(Loader.WHOAMIURL, context, MainActivity.prefs, null);
        Loader.getData(Loader.MEETINGURL, context, MainActivity.prefs, null);
    }
}
