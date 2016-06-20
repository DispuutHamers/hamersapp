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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.beers.Beer;
import nl.ecci.hamers.events.Event;
import nl.ecci.hamers.meetings.Meeting;
import nl.ecci.hamers.users.User;
import nl.ecci.hamers.users.User.Nickname;

public final class DataManager {
    // URL
//    public static final String baseURL = "https://zondersikkel.nl/api/v1/";
    private static final String baseURL = "http://192.168.100.100:3000/api/v2/";
    // URL Appendices
    public static final String QUOTEURL = "quotes";
    public static final String USERURL = "users";
    public static final String EVENTURL = "events?sorted=date-asc";
    public static final String NEWSURL = "news";
    public static final String BEERURL = "beers";
    public static final String REVIEWURL = "reviews";
    public static final String WHOAMIURL = "whoami";
    public static final String MOTIEURL = "motions";
    public static final String MEETINGURL = "meetings";
    public static final String SIGNUPURL = "signups";
    public static final String GCMURL = "register";
    // Data keys
    public static final String QUOTEKEY = "quoteData";
    public static final String USERKEY = "userData";
    public static final String EVENTKEY = "eventData";
    public static final String NEWSKEY = "newsData";
    public static final String BEERKEY = "beerData";
    public static final String REVIEWKEY = "reviewdata";
    public static final String MEETINGKEY = "meetingdata";
    public static final String APIKEYKEY = "apikey";
    public static final String WHOAMIKEY = "whoamikey";

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

    public static void postOrPatchData(final Context context, final SharedPreferences prefs, final String dataURL, final int urlAppendix, final String dataKEY, JSONObject body) {
        String url = baseURL + dataURL;
        if (urlAppendix != -1) {
            url = baseURL + dataURL + "/" + urlAppendix;
        }

        JsonObjectRequest request = new JsonObjectRequest(url, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (context != null) {
                            if (!(context instanceof MainActivity)) {
                                ((Activity) context).finish();
                            }
                            Toast.makeText(context, context.getString(R.string.posted), Toast.LENGTH_SHORT).show();
                            if (dataURL.equals(SIGNUPURL)) {
                                getData(context, prefs, EVENTURL, EVENTURL);
                            } else if (urlAppendix != -1){
                                getData(context, prefs, dataURL, dataKEY);
                            }
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
        Singleton.getInstance(context).addToRequestQueue(request);
    }

    private static void handleErrorResponse(@NonNull Context context, @NonNull VolleyError error) {
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
            case NEWSURL:
                MainActivity.NEWS_FRAGMENT.populateList();
                break;
            case USERURL:
                MainActivity.USER_FRAGMENT_ALL.populateList();
                MainActivity.USER_FRAGMENT_EX.populateList();
                break;
            case MEETINGURL:
                MainActivity.MEETING_FRAGMENT.populateList();
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
        }
        return new User(-1, "Unknown", "example@example.org", 0, 0, User.Member.LID, -1, new ArrayList<Nickname>(), new Date());
    }

    public static User getOwnUser(SharedPreferences prefs) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        JSONObject whoami;
        if ((whoami = DataManager.getJsonObject(prefs, DataManager.WHOAMIKEY)) != null) {
            return gson.fromJson(whoami.toString(), User.class);
        }
        return new User(-1, "Unknown", "example@example.org", 0, 0, User.Member.LID, -1, new ArrayList<Nickname>(), new Date());
    }

    public static Event getEvent(SharedPreferences prefs, int id) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        JSONArray events;
        try {
            if ((events = getJsonArray(prefs, EVENTKEY)) != null) {
                for (int i = 0; i < events.length(); i++) {
                    JSONObject event = events.getJSONObject(i);
                    if (event.getInt("id") == id) {
                        return gson.fromJson(event.toString(), Event.class);
                    }
                }
            }
        } catch (JSONException ignored) {
        }
        return new Event(1, "Unknown", "Unknown", "Unknown", new Date(), new Date(), new Date(), new ArrayList<Event.Signup>(), new Date());
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
        return new Beer(-1, "Unknown", "Unknown", "Unknown", "Unknown", "Unknown", "Unknown", "Unknown", "Unknown", new Date());
    }

    public static Meeting getMeeting(SharedPreferences prefs, int id) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        JSONArray meetings;
        try {
            if ((meetings = getJsonArray(prefs, MEETINGKEY)) != null) {
                for (int i = 0; i < meetings.length(); i++) {
                    JSONObject meeting = meetings.getJSONObject(i);
                    if (meeting.getInt("id") == id) {
                        return gson.fromJson(meeting.toString(), Meeting.class);
                    }
                }
            }
        } catch (JSONException ignored) {
        }
        Date date = new Date();
        return new Meeting(-1, "Unknown", "Unknown", "Unknown", -1, date, date, date);
    }

    private static JSONObject getJsonObject(SharedPreferences prefs, String key) {
        try {
            return new JSONObject(prefs.getString(key, null));
        } catch (JSONException | NullPointerException e) {
            return null;
        }
    }

    public static JSONArray getJsonArray(SharedPreferences prefs, String key) {
        try {
            return new JSONArray(prefs.getString(key, null));
        } catch (JSONException | NullPointerException e) {
            return null;
        }
    }


}
