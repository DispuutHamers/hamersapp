package nl.ecci.hamers.quotes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;

public class NewQuoteFragment extends DialogFragment {

    private final ArrayList<String> users = new ArrayList<>();

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
                                // Get quote from editText
                                EditText edit = (EditText) view.findViewById(R.id.quote_input);
                                String quote = edit.getText().toString();

                                // Get userID from spinner
                                Spinner userSpinner = (Spinner) view.findViewById(R.id.user_spinner);
                                int userID = DataManager.usernameToID(MainActivity.prefs, userSpinner.getSelectedItem().toString());

                                // Post quote
                                NewQuoteFragment.this.postQuote(quote, userID);
                            }
                        }
                );
        Spinner spinner = (Spinner) view.findViewById(R.id.user_spinner);
        createUserList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, users);
        spinner.setAdapter(adapter);

        return builder.create();
    }

    private void createUserList() {
        JSONArray userJSON;
        try {
            if ((userJSON = DataManager.getJsonArray(MainActivity.prefs, DataManager.USERKEY)) != null) {
                for (int i = 0; i < userJSON.length(); i++) {
                    users.add(userJSON.getJSONObject(i).getString("name"));
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), getString(R.string.snackbar_userloaderror), Toast.LENGTH_SHORT).show();
        }

    }

    private void postQuote(String quote, int userID) {
        JSONObject body = new JSONObject();
        try {
            body.put("text", quote);
            body.put("user_id", userID);
        } catch (JSONException ignored) {
        }

        DataManager.postData(this.getContext(), MainActivity.prefs, DataManager.QUOTEURL, DataManager.QUOTEKEY, body);
    }
}