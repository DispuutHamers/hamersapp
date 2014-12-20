package nl.ecci.Hamers.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import nl.ecci.Hamers.R;
import nl.ecci.Hamers.SendPostRequest;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class NewQuoteFragment extends DialogFragment {

    private SharedPreferences prefs;
    ArrayList<String> users = new ArrayList<String>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.new_quote_fragment, null);
        builder.setView(view)
                .setTitle(R.string.quote)
                .setPositiveButton(R.string.send_quote, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // Get quote from editText
                        EditText edit = (EditText) view.findViewById(R.id.quote_input);
                        String quote = edit.getText().toString();

                        // Get userID from spinner
                        Spinner userSpinner = (Spinner) view.findViewById(R.id.user_spinner);
                        String userID = nameToID(userSpinner.getSelectedItem().toString());

                        // Post quote
                        postQuote(quote, userID);

                    }
                })
                .setNegativeButton(R.string.cancel_quote, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        // Initialize spinner
        Spinner spinner = (Spinner) view.findViewById(R.id.user_spinner);
        createUserList();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, users);
        System.out.println(adapter.toString());
        System.out.println(spinner.toString());

        spinner.setAdapter(adapter);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void createUserList() {
        JSONArray userJSON;
        try {
            if ((userJSON = new JSONArray(prefs.getString("userData", null))) != null) {
                for (int i = 0; i < userJSON.length(); i++) {
                    users.add(userJSON.getJSONObject(i).getString("name"));
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), getString(R.string.toast_userloaderror), Toast.LENGTH_SHORT).show();e.printStackTrace();
        }

    }

    private String nameToID(String name){
        JSONArray userJSON;
        String returnv = "-1";
        try {
            if ((userJSON = new JSONArray(prefs.getString("userData", null))) != null) {
                for (int i = 0; i < userJSON.length(); i++) {
                    if(userJSON.getJSONObject(i).getString("name").equals(name)){
                        returnv = userJSON.getJSONObject(i).getString("id");
                    }
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), getString(R.string.toast_userloaderror), Toast.LENGTH_SHORT).show();e.printStackTrace();
        }
        return returnv;
    }

    private void postQuote(String quote, String userid) {
        System.out.println("posting quote");
        SendPostRequest req = new SendPostRequest(this.getActivity(), this, SendPostRequest.QUOTE, prefs, "quote[text]=" + quote + " &quote[user_id]=" + userid);
        req.execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }
}