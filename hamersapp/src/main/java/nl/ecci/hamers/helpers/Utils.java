package nl.ecci.hamers.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.beers.Beer;
import nl.ecci.hamers.events.Event;
import nl.ecci.hamers.loader.Loader;
import nl.ecci.hamers.meetings.Meeting;
import nl.ecci.hamers.users.User;

public class Utils {
    private static AlertDialog alertDialog;

    private static String hex(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (byte anArray : array) {
            sb.append(Integer.toHexString((anArray
                    & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    private static String md5Hex(String message) {
        try {
            MessageDigest md =
                    MessageDigest.getInstance("MD5");
            return hex(md.digest(message.getBytes("CP1252")));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ignored) {
        }
        return null;
    }

    /**
     * Show the dialog for entering the apikey on startup
     */
    private static void showApiKeyDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.apikeydialogtitle));
        builder.setMessage(context.getString(R.string.apikeydialogmessage));
        final EditText apiKey = new EditText(context);
        apiKey.setHint(context.getString(R.string.apikey_hint));
        builder.setView(apiKey);
        builder.setPositiveButton(context.getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable key = apiKey.getText();
                if (!key.toString().equals("")) {
                    // Store in memory
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Loader.APIKEYKEY, key.toString()).apply();
                    Toast.makeText(context, context.getResources().getString(R.string.dowloading), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.store_key_settings), Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Checks if the API key is present
     */
    public static void hasApiKey(Context context, SharedPreferences prefs) {
        if (prefs.getString("apikey", null) == null) {
            if (Utils.alertDialog == null) {
                Utils.showApiKeyDialog(context);
            } else if (!Utils.alertDialog.isShowing()) {
                Utils.showApiKeyDialog(context);
            }
        }
    }

    public static CharSequence[] stringArrayToCharSequenceArray(Object[] stringArray) {
        CharSequence[] charSequenceArray = new CharSequence[stringArray.length];

        for (int i = 0; i < stringArray.length; i++)
            charSequenceArray[i] = (String) stringArray[i];

        return charSequenceArray;
    }

    /**
     * Get app version
     */
    public static String getAppVersion(Context context) {
        String versionName;
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "";
        }
        return versionName;
    }

    public static String getGravatarURL(String email) {
        return String.format("http://gravatar.com/avatar/%s/?s=1920", Utils.md5Hex(email));
    }

    @NonNull
    public static String convertNicknames(ArrayList<User.Nickname> nicknames) {
        StringBuilder sb = new StringBuilder();
        for (User.Nickname nickname : nicknames) {
            sb.append(nickname.getNickname()).append(" ");
        }
        return sb.toString();
    }

    public static int usernameToID(SharedPreferences prefs, String name) {
        JSONArray userJSON;
        int result = -1;
        try {
            if ((userJSON = getJsonArray(prefs, Loader.USERURL)) != null) {
                for (int i = 0; i < userJSON.length(); i++) {
                    if (userJSON.getJSONObject(i).getString("name").equals(name)) {
                        result = userJSON.getJSONObject(i).getInt("id");
                    }
                }
            }
        } catch (JSONException e) {
            return result;
        }
        return result;
    }

    public static ArrayList<String> createActiveMemberList(Context context) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        ArrayList<String> users = new ArrayList<>();
        JSONArray userJSON;
        try {
            if ((userJSON = getJsonArray(MainActivity.prefs, Loader.USERURL)) != null) {
                for (int i = 0; i < userJSON.length(); i++) {
                    User user = gson.fromJson(userJSON.getJSONObject(i).toString(), User.class);
                    if (user.getMember() == User.Member.LID) {
                        users.add(user.getName());
                    }
                }
            }
        } catch (JSONException e) {
            Toast.makeText(context, context.getString(R.string.user_load_error), Toast.LENGTH_SHORT).show();
        }
        return users;
    }

    public static User getUser(SharedPreferences prefs, int id) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        JSONArray users;
        try {
            if ((users = getJsonArray(prefs, Loader.USERURL)) != null) {
                for (int i = 0; i < users.length(); i++) {
                    JSONObject user = users.getJSONObject(i);
                    if (user.getInt("id") == id) {
                        return gson.fromJson(user.toString(), User.class);
                    }
                }
            }
        } catch (JSONException ignored) {
        }
        return new User(-1, "Unknown", "example@example.org", 0, 0, User.Member.LID, -1, new ArrayList<User.Nickname>(), new Date());
    }

    public static User getOwnUser(SharedPreferences prefs) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        JSONObject whoami;
        try {
            if ((whoami = new JSONObject(prefs.getString(Loader.WHOAMIURL, null))) != null) {
                return gson.fromJson(whoami.toString(), User.class);
            }
        } catch (JSONException | NullPointerException ignored) {
        }
        return new User(-1, "Unknown", "example@example.org", 0, 0, User.Member.LID, -1, new ArrayList<User.Nickname>(), new Date());
    }

    public static Event getEvent(SharedPreferences prefs, int id) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        JSONArray events;
        try {
            if ((events = getJsonArray(prefs, Loader.EVENTURL)) != null) {
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
            if ((beers = getJsonArray(prefs, Loader.BEERURL)) != null) {
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
            if ((meetings = getJsonArray(prefs, Loader.MEETINGURL)) != null) {
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
