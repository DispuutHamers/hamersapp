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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.users.User;

import static nl.ecci.hamers.helpers.DataManager.getJsonArray;

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
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(DataManager.APIKEYKEY, key.toString()).apply();
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
            if ((userJSON = getJsonArray(prefs, DataManager.USERURL)) != null) {
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
            if ((userJSON = DataManager.getJsonArray(MainActivity.prefs, DataManager.USERURL)) != null) {
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
}
