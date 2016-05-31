package nl.ecci.hamers.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.beers.Beer;
import nl.ecci.hamers.events.Event;
import nl.ecci.hamers.users.User;

import static nl.ecci.hamers.MainActivity.parseDate;

public final class DataManager {
    public static final String QUOTEURL = "/quote.json";
    public static final String USERURL = "/user.json";
    public static final String EVENTURL = "/event.json";
    public static final String NEWSURL = "/news.json";
    public static final String BEERURL = "/beer.json";
    public static final String REVIEWURL = "/review.json";
    public static final String WHOAMIURL = "/whoami.json";
    public static final String MOTIEURL = "/motion";
    public static final String SIGNUPURL = "/signup";
    public static final String GCMURL = "/register";
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
        String url = MainActivity.baseURL + prefs.getString(DataManager.APIKEYKEY, "a") + dataURL;

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
                        System.out.println("--------------------\nError:\n" + error.toString());
                        if (error instanceof AuthFailureError) {
                            // Wrong API key
                            if (Utils.alertDialog == null) {
                                Utils.showApiKeyDialog(context);
                            } else if (!Utils.alertDialog.isShowing()) {
                                Utils.showApiKeyDialog(context);
                            }
                        } else {
                            // (Generic) Volley error
                            Toast.makeText(context, context.getString(R.string.snackbar_volley_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        Singleton.getInstance(context).addToRequestQueue(request);
    }

    public static void postData(final Context context, final SharedPreferences prefs, final String dataURL, final String dataKEY, final Map<String, String> urlParams) {
        String url = MainActivity.baseURL + prefs.getString(DataManager.APIKEYKEY, "a") + dataURL;

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
                        System.out.println("--------------------\nError:" + error.toString());
                        if (error instanceof AuthFailureError) {
                            // Wrong API key
                            if (Utils.alertDialog == null) {
                                Utils.showApiKeyDialog(context);
                            } else if (!Utils.alertDialog.isShowing()) {
                                Utils.showApiKeyDialog(context);
                            }
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return urlParams;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        Singleton.getInstance(context).addToRequestQueue(request);
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
//                MainActivity.EVENT_FRAGMENT_ALL.populateList();
//                MainActivity.EVENT_FRAGMENT_UPCOMING.populateList();
                break;
            case NEWSURL:
                MainActivity.NEWS_FRAGMENT.populateList();
                break;
            case USERURL:
                MainActivity.USER_FRAGMENT.populateList();
                break;
        }
    }

    public static User getUser(SharedPreferences prefs, int id) {
        User result = new User("Unknown", -1, "example@example.org", 0, 0);
        JSONArray users;
        try {
            if ((users = getJsonArray(prefs, USERKEY)) != null) {
                for (int i = 0; i < users.length(); i++) {
                    JSONObject user = users.getJSONObject(i);
                    if (user.getInt("id") == id) {
                        result = new User(user.getString("name"), user.getInt("id"), user.getString("email"), user.getInt("quotes"), user.getInt("reviews"));
                        return result;
                    }
                }
            }
        } catch (JSONException ignored) {
        }
        return result;
    }

    public static User getOwnUser(SharedPreferences prefs) {
        JSONArray whoami = DataManager.getJsonArray(prefs, DataManager.WHOAMIKEY);
        try {
            if (whoami != null) {
                JSONObject user = whoami.getJSONObject(0);
                return new User(user.getString("name"), user.getInt("id"), user.getString("email"), user.getInt("quotes"), user.getInt("reviews"));
            }
        } catch (JSONException ignored) {
        }
        return new User("Unknown", -1, "example@example.org", 0, 0);
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
        JSONArray beers;
        try {
            if ((beers = getJsonArray(prefs, BEERKEY)) != null) {
                for (int i = 0; i < beers.length(); i++) {
                    JSONObject temp = beers.getJSONObject(i);
                    if (temp.getInt("id") == id) {
                        Beer result;
                        String cijfer = temp.getString("cijfer");
                        if (cijfer.equals("null")) {
                            result = new Beer(temp.getInt("id"), temp.getString("name"), temp.getString("soort"),
                                    temp.getString("picture"), temp.getString("percentage"), temp.getString("brewer"), temp.getString("country"), "nog niet bekend", parseDate(temp.getString("created_at")));
                        } else {
                            result = new Beer(temp.getInt("id"), temp.getString("name"), temp.getString("soort"),
                                    temp.getString("picture"), temp.getString("percentage"), temp.getString("brewer"), temp.getString("country"), cijfer, parseDate(temp.getString("created_at")));
                        }
                        return result;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return null;
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
}
