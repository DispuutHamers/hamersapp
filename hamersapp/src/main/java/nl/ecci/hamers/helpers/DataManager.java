package nl.ecci.hamers.helpers;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DataManager {
    public static final String QUOTEKEY = "quoteData";
    public static final String USERKEY = "userData";
    public static final String EVENTKEY = "eventData";
    public static final String NEWSKEY = "newsData";
    public static final String BEERKEY = "beerData";
    public static final String REVIEWKEY = "reviewdata";
    public static final String APIKEYKEY = "apikey";
    public static final String WHOAMIKEY = "whoamikey";
    public static final String AUTHENTICATED = "authenticated";

    public static JSONObject getUser(SharedPreferences prefs, int id) {
        JSONArray users;
        try {
            if ((users = getJsonArray(prefs, USERKEY)) != null) {
                for (int i = 0; i < users.length(); i++) {
                    JSONObject user = users.getJSONObject(i);
                    if (user.getInt("id") == id) {
                        return user;
                    }
                }
            }
        } catch (JSONException e) {
            return null;
        }
        return null;
    }

    public static JSONObject getEvent(SharedPreferences prefs, String title, Date date) {
        JSONArray events;
        try {
            if ((events = getJsonArray(prefs, EVENTKEY)) != null) {
                for (int i = 0; i < events.length(); i++) {
                    JSONObject temp = events.getJSONObject(i);
                    DateFormat dbDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", new Locale("nl"));
                    Date dbDatum = dbDF.parse(temp.getString("date"));
                    if (dbDatum.equals(date)) {
                        if (temp.getString("title").equals(title)) {
                            return temp;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isAuthenticated(SharedPreferences prefs) {
        return prefs.getBoolean("Authenticated", false);
    }

    public static JSONObject getBeer(SharedPreferences prefs, String name) {
        JSONArray beers;
        try {
            if ((beers = getJsonArray(prefs, BEERKEY)) != null) {
                for (int i = 0; i < beers.length(); i++) {
                    JSONObject temp = beers.getJSONObject(i);
                    if (temp.getString("name").equals(name)) {
                        return temp;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static JSONObject getBeer(SharedPreferences prefs, int id) {
        JSONArray beers;
        try {
            if ((beers = getJsonArray(prefs, BEERKEY)) != null) {
                for (int i = 0; i < beers.length(); i++) {
                    JSONObject temp = beers.getJSONObject(i);
                    if (temp.getInt("id") == id) {
                        return temp;
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

    public static String IDToEmail(SharedPreferences prefs, int id) {
        String result = null;
        JSONArray users;
        try {
            if ((users = getJsonArray(prefs, USERKEY)) != null) {
                for (int i = 0; i < users.length(); i++) {
                    if (users.getJSONObject(i).getInt("id") == id) {
                        result = users.getJSONObject(i).getString("email");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String UserIDtoUserName(SharedPreferences prefs, int id) {
        String result = null;
        JSONArray users;
        try {
            if ((users = getJsonArray(prefs, USERKEY)) != null) {
                for (int i = 0; i < users.length(); i++) {
                    if (users.getJSONObject(i).getInt("id") == id) {
                        result = users.getJSONObject(i).getString("name");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String BeerIDtoBeerName(SharedPreferences prefs, int id) {
        String result = null;
        JSONArray users;
        try {
            if ((users = getJsonArray(prefs, BEERKEY)) != null) {
                for (int i = 0; i < users.length(); i++) {
                    if (users.getJSONObject(i).getInt("id") == id) {
                        result = users.getJSONObject(i).getString("name");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getUserID(SharedPreferences prefs) {
        int result = 0;
        JSONArray whoami = DataManager.getJsonArray(prefs, DataManager.WHOAMIKEY);
        try {
            if (whoami != null) {
                result = whoami.getJSONObject(0).getInt("id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getUserName(SharedPreferences prefs) {
        return UserIDtoUserName(prefs, getUserID(prefs));
    }
}
