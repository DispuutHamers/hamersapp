package nl.ecci.hamers.quotes

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.android.volley.VolleyError
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.Utils
import nl.ecci.hamers.helpers.Utils.usernameToID
import nl.ecci.hamers.loader.Loader
import nl.ecci.hamers.loader.PostCallback
import nl.ecci.hamers.users.User
import org.json.JSONException
import org.json.JSONObject

class NewQuoteFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.quote_new_fragment, null)
        builder.setView(view)
                .setTitle(R.string.quote)
                .setPositiveButton(R.string.send_quote
                ) { dialog, id ->
                    val edit = view.findViewById(R.id.quote_input) as EditText
                    val quote = edit.text.toString()

                    val userSpinner = view.findViewById(R.id.quote_user_spinner) as Spinner
                    val userID = usernameToID(MainActivity.prefs, userSpinner.selectedItem.toString())

                    postQuote(quote, userID)
                }
        val spinner = view.findViewById(R.id.quote_user_spinner) as Spinner
        val users = Utils.createActiveMemberList()
        val names = users.map(User::name)
        val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, names)
        spinner.adapter = adapter

        return builder.create()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        val parentFragment = parentFragment
        if (parentFragment is DialogInterface.OnDismissListener) {
            parentFragment.onDismiss(dialog)
        }
    }

    private fun postQuote(quote: String, userID: Int) {
        val body = JSONObject()
        try {
            body.put("text", quote)
            body.put("user_id", userID)
        } catch (ignored: JSONException) {
        }

        Loader.postOrPatchData(context, Loader.QUOTEURL, body, -1, object : PostCallback {
            override fun onSuccess(response: JSONObject) {

            }

            override fun onError(error: VolleyError) {

            }
        })
    }
}