package nl.ecci.hamers.quotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.SendPostRequest;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class NewQuoteFragment extends DialogFragment {

    private final ArrayList<String> users = new ArrayList<>();
    private SharedPreferences prefs;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.new_quote_fragment, null);
        builder.setView(view)
                .setTitle(R.string.quote)
                .setPositiveButton(R.string.send_quote, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            // Get quote from editText
                            EditText edit = (EditText) view.findViewById(R.id.quote_input);
                            String quote = URLEncoder.encode(edit.getText().toString(), "UTF-8");

                            // Get userID from spinner
                            Spinner userSpinner = (Spinner) view.findViewById(R.id.user_spinner);
                            int userID = DataManager.usernameToID(prefs, userSpinner.getSelectedItem().toString());

                            // Post quote
                            NewQuoteFragment.this.postQuote(quote, userID);
                        } catch (UnsupportedEncodingException e) {
                        }
                    }
                });

        // Initialize spinner
        Spinner spinner = (Spinner) view.findViewById(R.id.user_spinner);
        createUserList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, users);

        spinner.setAdapter(adapter);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void createUserList() {
        JSONArray userJSON;
        try {
            if ((userJSON = DataManager.getJsonArray(prefs, DataManager.USERKEY)) != null) {
                for (int i = 0; i < userJSON.length(); i++) {
                    users.add(userJSON.getJSONObject(i).getString("name"));
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), getString(R.string.snackbar_userloaderror), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private void postQuote(String quote, int userid) {
        SendPostRequest req = new SendPostRequest(this.getActivity(), null, QuoteFragment.parentLayout, SendPostRequest.QUOTEURL, prefs, "quote[text]=" + quote + " &quote[user_id]=" + userid);
        req.execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }
}