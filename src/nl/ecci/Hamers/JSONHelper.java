package nl.ecci.Hamers;

import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rob on 26-12-14.
 */
public final class JSONHelper {
    public static final String QUOTEKEY = "quoteData";
    public static final String USERKEY = "userData";
    public static final String EVENTKEY = "eventData";
    public static final String BEERKEY = "beerData";

    public static JSONObject getUser(SharedPreferences prefs, int id) {
        JSONArray users;
        try {
            if ((users = new JSONArray(prefs.getString("userData", null))) != null) {
                for (int i = 0; i < users.length(); i++) {
                    try {
                        JSONObject temp = users.getJSONObject(i);
                        if (temp.getInt("id") == id) {
                            return temp;
                        }
                    } catch (JSONException e) {
                        return null;
                    }
                }
            }
        } catch (JSONException e) {
            return null;
        }
        return null;
    }

    public static JSONArray getJsonArray(SharedPreferences prefs, String key){
        try {
            return new JSONArray(prefs.getString(key, null));
        } catch (JSONException e) {
            return null;
        }
    }

    public static int usernameToID(SharedPreferences prefs, String name){
        JSONArray userJSON;
        int returnv = -1;
        try {
            if ((userJSON = getJsonArray(prefs, USERKEY)) != null) {
                for (int i = 0; i < userJSON.length(); i++) {
                    if(userJSON.getJSONObject(i).getString("name").equals(name)){
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
