package nl.ecci.hamers.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.beers.Beer;
import nl.ecci.hamers.events.Event;
import nl.ecci.hamers.users.User;
import nl.ecci.hamers.users.User.Nickname;

import static nl.ecci.hamers.MainActivity.parseDate;

public final class DataManager {
    // URL
//    public static final String baseURL = "https://zondersikkel.nl/api/v1/";
    public static final String baseURL = "http://192.168.100.100:3000/api/v2/";
    // URL Appendices
    public static final String QUOTEURL = "quotes";
    public static final String USERURL = "users";
    public static final String EVENTURL = "events";
    public static final String NEWSURL = "news";
    public static final String BEERURL = "beers";
    public static final String REVIEWURL = "reviews";
    public static final String WHOAMIURL = "whoami";
    public static final String MOTIEURL = "motions";
    public static final String SIGNUPURL = "signups";
    public static final String GCMURL = "register";
    // Data keys
    public static final String QUOTEKEY = "quoteData";
    public static final String USERKEY = "userData";
    public static final String EVENTKEY = "eventData";
    public static final String NEWSKEY = "newsData";
    public static final String BEERKEY = "beerData";
    public static final String REVIEWKEY = "reviewdata";
    public static final String APIKEYKEY = "apikey";
    public static final String WHOAMIKEY = "whoamikey";
    public static final String SIGNUPKEY = "signupkey";

    public static void getData(final Context context, final SharedPreferences prefs, final String dataURL, final String dataKEY) {
        String url = baseURL + dataURL;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        prefs.edit().putString(dataKEY, response).apply();
                        populateList(dataURL);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
        Singleton.getInstance(context).addToRequestQueue(request);
    }

    public static void postData(final Context context, final SharedPreferences prefs, final String dataURL, final String dataKEY, final Map<String, String> urlParams) {
        String url = baseURL + dataURL;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (context != null) {
                            if (!(context instanceof MainActivity)) {
                                ((Activity) context).finish();
                            }
                            Toast.makeText(context, context.getString(R.string.posted), Toast.LENGTH_SHORT).show();
                            getData(context, prefs, dataURL, dataKEY);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handleErrorResponse(context, error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return urlParams;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Token token=" + prefs.getString(APIKEYKEY, ""));
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        Singleton.getInstance(context).addToRequestQueue(request);
    }

    private static void handleErrorResponse(@NonNull Context context, @NonNull VolleyError error) {
        System.out.println("--------------------\nError:\n" + error.toString());
        if (context != null) {
            if (error instanceof AuthFailureError) {
                // Wrong API key
                if (Utils.alertDialog == null) {
                    Utils.showApiKeyDialog(context);
                } else if (!Utils.alertDialog.isShowing()) {
                    Utils.showApiKeyDialog(context);
                }
            } else if (error instanceof TimeoutError) {
                // Timeout
                Toast.makeText(context, context.getString(R.string.snackbar_timeout_error), Toast.LENGTH_SHORT).show();
            } else if (error instanceof ServerError) {
                // Server error (500)
                Toast.makeText(context, context.getString(R.string.snackbar_server_error), Toast.LENGTH_SHORT).show();
            } else if (error instanceof NoConnectionError) {
                // No network connection
                Toast.makeText(context, context.getString(R.string.snackbar_connection_error), Toast.LENGTH_SHORT).show();
            } else if (error instanceof NetworkError) {
                // No network connection
                Toast.makeText(context, context.getString(R.string.snackbar_network_error), Toast.LENGTH_SHORT).show();
            } else {
                // Other error
                Toast.makeText(context, context.getString(R.string.snackbar_volley_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void populateList(String data) {
        switch (data) {
            case QUOTEURL:
                MainActivity.QUOTE_FRAGMENT.populateList();
                break;
            case BEERURL:
                MainActivity.BEER_FRAGMENT.populateList();
                break;
            case REVIEWURL:
                MainActivity.BEER_FRAGMENT.populateList();
                break;
            case EVENTURL:
                MainActivity.EVENT_FRAGMENT_ALL.populateList();
                MainActivity.EVENT_FRAGMENT_UPCOMING.populateList();
                break;
            case SIGNUPURL:
                MainActivity.EVENT_FRAGMENT_ALL.populateList();
                MainActivity.EVENT_FRAGMENT_UPCOMING.populateList();
                break;
            case NEWSURL:
                MainActivity.NEWS_FRAGMENT.populateList();
                break;
            case USERURL:
                MainActivity.USER_FRAGMENT_ALL.populateList();
                MainActivity.USER_FRAGMENT_EX.populateList();
                break;
        }
    }

    public static User getUser(SharedPreferences prefs, int id) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        JSONArray users;
        try {
            if ((users = getJsonArray(prefs, USERKEY)) != null) {
                for (int i = 0; i < users.length(); i++) {
                    JSONObject user = users.getJSONObject(i);
                    if (user.getInt("id") == id) {
                        return gson.fromJson(user.toString(), User.class);
                    }
                }
            }
        } catch (JSONException ignored) {
            ignored.printStackTrace();
        }
        return new User("Unknown", -1, "example@example.org", 0, 0, true, -1, null, new Date());
    }

    public static User getOwnUser(SharedPreferences prefs) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        JSONArray whoami;
        try {
            if ((whoami = DataManager.getJsonArray(prefs, DataManager.WHOAMIKEY)) != null) {
                JSONObject user = whoami.getJSONObject(0);
                return gson.fromJson(user.toString(), User.class);
            }
        } catch (JSONException ignored) {
        }
        return new User("Unknown", -1, "example@example.org", 0, 0, true, -1, null, new Date());
    }

    public static Event getEvent(SharedPreferences prefs, int id) {
        JSONArray events;
        try {
            if ((events = getJsonArray(prefs, EVENTKEY)) != null) {
                for (int i = 0; i < events.length(); i++) {
                    JSONObject event = events.getJSONObject(i);
                    if (event.getInt("id") == id) {
                        Date date = parseDate(event.getString("date"));
                        Date end_time = parseDate(event.getString("end_time"));
                        Date deadline = parseDate(event.getString("deadline"));

                        return new Event(event.getInt("id"), event.getString("title"), event.getString("beschrijving"), event.getString("location"), date, end_time, deadline, event.getJSONArray("signups"));
                    }
                }
            }
        } catch (JSONException ignored) {
            ignored.printStackTrace();
        }
        return new Event(1, "Unknown", "Unknown", "Unknown", new Date(), new Date(), new Date(), null);
    }

    public static Event getEvent(SharedPreferences prefs, String title, Date date) {
        JSONArray events;
        try {
            if ((events = getJsonArray(prefs, EVENTKEY)) != null) {
                for (int i = 0; i < events.length(); i++) {
                    JSONObject event = events.getJSONObject(i);
                    Date dbDatum = MainActivity.dbDF.parse(event.getString("date"));
                    if (dbDatum.equals(date) && event.getString("title").equals(title)) {
                        Date end_time = parseDate(event.getString("end_time"));
                        Date deadline = parseDate(event.getString("deadline"));
                        return new Event(event.getInt("id"), event.getString("title"), event.getString("beschrijving"), event.getString("location"), date, end_time, deadline, event.getJSONArray("signups"));
                    }
                }
            }
        } catch (JSONException | ParseException ignored) {
        }
        return new Event(1, "Unknown", "Unknown", "Unknown", new Date(), new Date(), new Date(), null);
    }

    public static Beer getBeer(SharedPreferences prefs, int id) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        JSONArray beers;
        try {
            if ((beers = getJsonArray(prefs, BEERKEY)) != null) {
                for (int i = 0; i < beers.length(); i++) {
                    JSONObject temp = beers.getJSONObject(i);
                    if (temp.getInt("id") == id) {
                        return gson.fromJson(temp.toString(), Beer.class);
                    }
                }
            }
        } catch (JSONException ignored) {
        }
        return new Beer(-1, "Unknown", "Unknown", null, "Unknown", "Unknown", "Unknown", "Unknown", null, new Date());
    }

    public static JSONArray getJsonArray(SharedPreferences prefs, String key) {
        try {
            return new JSONArray(prefs.getString(key, null));
        } catch (JSONException | NullPointerException e) {
            return null;
        }
    }

    public static int usernameToID(SharedPreferences prefs, String name) {
        JSONArray userJSON;
        int returnv = -1;
        try {
            if ((userJSON = getJsonArray(prefs, USERKEY)) != null) {
                for (int i = 0; i < userJSON.length(); i++) {
                    if (userJSON.getJSONObject(i).getString("name").equals(name)) {
                        returnv = userJSON.getJSONObject(i).getInt("id");
                    }
                }
            }
        } catch (JSONException e) {
            return returnv;
        }
        return returnv;
    }

    public static String getGravatarURL(String email) {
        return String.format("http://gravatar.com/avatar/%s/?s=1920", Utils.md5Hex(email));
    }

    @NonNull
    public static String convertNicknames(ArrayList<Nickname> nicknames) {
        StringBuilder sb = new StringBuilder();
        for (Nickname nickname : nicknames) {
            sb.append(nickname.getNickname());
        }
        return sb.toString();
    }
}
