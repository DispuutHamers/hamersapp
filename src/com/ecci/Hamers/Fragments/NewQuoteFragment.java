package com.ecci.Hamers.Fragments;

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
import com.ecci.Hamers.R;
import com.ecci.Hamers.SendPostRequest;
import org.json.JSONArray;

public class NewQuoteFragment extends DialogFragment {

    private SharedPreferences prefs;
    JSONArray users;

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
                        String userID = userSpinner.getSelectedItem().toString();
                        System.out.println("USERID: " + userID);

                        // Post quote
                        postQuote(quote, "2");

                    }
                })
                .setNegativeButton(R.string.cancel_quote, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        // Initialize spinner
        Spinner spinner = (Spinner) view.findViewById(R.id.user_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.users_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void postQuote(String quote, String userid){
        System.out.println("posting quote");
        SendPostRequest req = new SendPostRequest(this, SendPostRequest.QUOTE, prefs, "quote[text]="+ quote + " &quote[user_id]=" + userid);
        req.execute();

    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }
}