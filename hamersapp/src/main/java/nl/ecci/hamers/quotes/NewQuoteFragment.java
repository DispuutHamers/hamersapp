package nl.ecci.hamers.quotes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.Utils;
import nl.ecci.hamers.loader.Loader;
import nl.ecci.hamers.loader.PostCallback;
import nl.ecci.hamers.users.User;

import static nl.ecci.hamers.helpers.Utils.usernameToID;

public class NewQuoteFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.quote_new_fragment, null);
        builder.setView(view)
                .setTitle(R.string.quote)
                .setPositiveButton(R.string.send_quote, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText edit = (EditText) view.findViewById(R.id.quote_input);
                                String quote = edit.getText().toString();

                                Spinner userSpinner = (Spinner) view.findViewById(R.id.quote_user_spinner);
                                int userID = usernameToID(MainActivity.prefs, userSpinner.getSelectedItem().toString());

                                postQuote(quote, userID);
                            }
                        }
                );
        Spinner spinner = (Spinner) view.findViewById(R.id.quote_user_spinner);
        ArrayList<User> users = Utils.createActiveMemberList();
        ArrayList<String> names = new ArrayList<>();
        for (User user : users) {
            names.add(user.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, names);
        spinner.setAdapter(adapter);

        return builder.create();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) parentFragment).onDismiss(dialog);
        }
    }

    private void postQuote(String quote, int userID) {
        JSONObject body = new JSONObject();
        try {
            body.put("text", quote);
            body.put("user_id", userID);
        } catch (JSONException ignored) {
        }

        Loader.postOrPatchData(new PostCallback() {
            @Override
            public void onSuccess(JSONObject response) {

            }

            @Override
            public void onError(VolleyError error) {

            }
        }, getContext(), MainActivity.prefs, Loader.QUOTEURL, -1, body);
    }
}