package nl.ecci.hamers.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import nl.ecci.hamers.R;
import nl.ecci.hamers.beers.Beer;
import nl.ecci.hamers.events.Event;
import nl.ecci.hamers.loader.Loader;
import nl.ecci.hamers.meetings.Meeting;
import nl.ecci.hamers.users.User;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static nl.ecci.hamers.MainActivity.prefs;

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
            MessageDigest md = MessageDigest.getInstance("MD5");
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
        int result = -1;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Type type = new TypeToken<ArrayList<User>>() {
        }.getType();
        ArrayList<User> userList = gson.fromJson(prefs.getString(Loader.USERURL, null), type);

        for (User user : userList) {
            if (user.getName().equals(name)) {
                result = user.getID();
            }
        }
        return result;
    }

    public static ArrayList<User> createActiveMemberList() {
        ArrayList<User> result = new ArrayList<>();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Type type = new TypeToken<ArrayList<User>>() {
        }.getType();
        ArrayList<User> userList = gson.fromJson(prefs.getString(Loader.USERURL, null), type);

        if (userList != null) {
            for (User user : userList) {
                if (user.getMember() == User.Member.LID) {
                    result.add(user);
                }
            }
        }
        return result;
    }

    public static User getUser(SharedPreferences prefs, int id) {
        ArrayList<User> userList;
        User result = new User(-1, "Unknown", "example@example.org", 0, 0, User.Member.LID, -1, new ArrayList<User.Nickname>(), new Date());
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Type type = new TypeToken<ArrayList<User>>() {
        }.getType();

        userList = gson.fromJson(prefs.getString(Loader.USERURL, null), type);

        if (userList != null) {
            for (User user : userList) {
                if (user.getID() == id) {
                    result = user;
                }
            }
        }

        return result;
    }

    public static User getOwnUser(SharedPreferences prefs) {
        User user;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        user = gson.fromJson(prefs.getString(Loader.WHOAMIURL, null), User.class);
        if (user == null) {
            user = new User(-1, "Unknown", "example@example.org", 0, 0, User.Member.LID, -1, new ArrayList<User.Nickname>(), new Date());
        }
        return user;
    }

    public static Event getEvent(SharedPreferences prefs, int id) {
        Event result = new Event(1, "Unknown", "Unknown", "Unknown", new Date(), new Date(), new Date(), new ArrayList<Event.Signup>(), new Date());
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Type type = new TypeToken<ArrayList<Event>>() {
        }.getType();
        ArrayList<Event> eventList = gson.fromJson(prefs.getString(Loader.EVENTURL, null), type);

        if (eventList != null) {
            for (Event event : eventList) {
                if (event.getID() == id) {
                    result = event;
                }
            }
        }

        return result;
    }

    public static Beer getBeer(SharedPreferences prefs, int id) {
        Beer result = new Beer(-1, "Unknown", "Unknown", "Unknown", "Unknown", "Unknown", "Unknown", "Unknown", "Unknown", new Date());
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Type type = new TypeToken<ArrayList<Beer>>() {
        }.getType();
        ArrayList<Beer> beerList = gson.fromJson(prefs.getString(Loader.BEERURL, null), type);

        if (beerList != null) {
            for (Beer beer : beerList) {
                if (beer.getID() == id) {
                    result = beer;
                }
            }
        }

        return result;
    }

    public static Meeting getMeeting(SharedPreferences prefs, int id) {
        Date date = new Date();
        Meeting result = new Meeting(-1, "Unknown", "Unknown", "Unknown", -1, date, date, date);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Type type = new TypeToken<ArrayList<Meeting>>() {
        }.getType();
        ArrayList<Meeting> meetingList = gson.fromJson(prefs.getString(Loader.MEETINGURL, null), type);

        if (meetingList != null) {
            for (Meeting meeting : meetingList) {
                if (meeting.getID() == id) {
                    result = meeting;
                }
            }
        }

        return result;
    }

    /**
     * Hides the soft keyboard
     */
    public static void hideKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
