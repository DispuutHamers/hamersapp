package nl.ecci.hamers.loader;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.Utils;

public final class Loader {
    // URL Appendices
    public static final String QUOTEURL = "quotes";
    public static final String USERURL = "users";
    public static final String EVENTURL = "events";
    public static final String NEWSURL = "news";
    public static final String BEERURL = "beers";
    public static final String REVIEWURL = "reviews";
    public static final String WHOAMIURL = "whoami";
    public static final String MEETINGURL = "meetings";
    public static final String SIGNUPURL = "signups";
    public static final String GCMURL = "register";
    public static final String STICKERURL = "stickers";
    // Data keys
    public static final String APIKEYKEY = "apikey";

    // URL
    private static final String baseURL = "https://zondersikkel.nl/api/v2/";
//    private static final String baseURL = "http://192.168.100.100:3000/api/v2/";

    public static void getData(@NonNull final Context context, final String dataURL, final GetCallback callback, final Map<String, String> params) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String url = buildURL(dataURL, params, -1);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("GET-response", response);
                        if (!dataURL.equals(SIGNUPURL) && !dataURL.equals(EVENTURL)) {
                            prefs.edit().putString(dataURL, response).apply();
                        }
                        if (callback != null) {
                            callback.onSuccess(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("GET-error", error.toString());
                        if (callback != null) {
                            callback.onError(error);
                        }
                        handleErrorResponse(context, error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token token=" + prefs.getString(APIKEYKEY, ""));
                return headers;
            }

            @Override
            public Map<String, String> getParams() {
                return params;
            }
        };

        Log.d("Request", request.toString());
        Singleton.getInstance(context).addToRequestQueue(request);
    }

    public static void postOrPatchData(@NonNull final Context context, final String dataURL, JSONObject body, final int urlAppendix, final PostCallback callback) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String url = buildURL(dataURL, null, urlAppendix);

        Log.d("PostRequest: ", body.toString());

        JsonObjectRequest request = new JsonObjectRequest(url, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("POST-response", response.toString());

                        if (!dataURL.equals(GCMURL))
                            Utils.showToast(context, context.getString(R.string.posted), Toast.LENGTH_SHORT);

                        if (callback != null) {
                            callback.onSuccess(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("POST-error", error.toString());
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

    private static String buildURL(String URL, Map<String, String> params, int appendix) {
        StringBuilder builder = new StringBuilder();
        builder.append(baseURL).append(URL);

        if (appendix != -1) {
            builder.append("/").append(appendix);
        }

        if (params != null) {
            builder.append("?");
            for (String key : params.keySet()) {
                Object value = params.get(key);
                if (value != null) {
                    try {
                        value = URLEncoder.encode(String.valueOf(value), "UTF-8");
                        if (builder.length() > 0)
                            builder.append("&");
                        builder.append(key).append("=").append(value);
                    } catch (UnsupportedEncodingException ignored) {
                    }
                }
            }
        }
        return builder.toString();
    }

    private static void handleErrorResponse(@NonNull Context context, @NonNull VolleyError error) {
        if (error instanceof AuthFailureError) {
            // Wrong API key
            Utils.showToast(context, context.getString(R.string.auth_error), Toast.LENGTH_SHORT);
        } else if (error instanceof TimeoutError) {
            // Timeout
            Utils.showToast(context, context.getString(R.string.timeout_error), Toast.LENGTH_SHORT);
        } else if (error instanceof ServerError) {
            // Server error (500)
            Utils.showToast(context, context.getString(R.string.server_error), Toast.LENGTH_SHORT);
        } else if (error instanceof NoConnectionError) {
            // No network connection
            Utils.showToast(context, context.getString(R.string.connection_error), Toast.LENGTH_SHORT);
        } else if (error instanceof NetworkError) {
            // Network error
            Utils.showToast(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT);
        } else {
            // Other error
            Utils.showToast(context, context.getString(R.string.volley_error), Toast.LENGTH_SHORT);
        }
    }

    public static void getAllData(@NonNull Context context) {
        Loader.getData(context, Loader.QUOTEURL, null, null);
        Loader.getData(context, Loader.EVENTURL, null, null);
        Loader.getData(context, Loader.NEWSURL, null, null);
        Loader.getData(context, Loader.BEERURL, null, null);
        Loader.getData(context, Loader.REVIEWURL, null, null);
        Loader.getData(context, Loader.WHOAMIURL, null, null);
        Loader.getData(context, Loader.MEETINGURL, null, null);
    }
}
