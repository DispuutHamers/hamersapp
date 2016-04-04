package nl.ecci.hamers.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;

public class Utils {
    private static String hex(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (byte anArray : array) {
            sb.append(Integer.toHexString((anArray
                    & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    public static String md5Hex(String message) {
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
    public static void showApiKeyDialog(final Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(context.getString(R.string.apikeydialogtitle));
        alert.setMessage(context.getString(R.string.apikeydialogmessage));
        final EditText apiKey = new EditText(context);
        apiKey.setHint(context.getString(R.string.apikey_hint));
        alert.setView(apiKey);
        alert.setPositiveButton(context.getString(R.string.dialog_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable key = apiKey.getText();
                if (!key.toString().equals("")) {
                    // Store in memory
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(DataManager.APIKEYKEY, key.toString()).apply();
                    Toast.makeText(context, context.getResources().getString(R.string.snackbar_downloading), Toast.LENGTH_SHORT).show();
                    hasApiKey(context);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.snackbar_storekeymemory), Toast.LENGTH_SHORT).show();
                }
            }
        });
        alert.show();
    }

    /**
     * Checks if the API key is present
     */
    public static void hasApiKey(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getString("apikey", null) == null) {
            Utils.showApiKeyDialog(context);
        }
    }
}
