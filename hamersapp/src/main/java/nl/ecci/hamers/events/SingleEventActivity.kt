package nl.ecci.hamers.events

import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.CalendarContract
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import com.android.volley.VolleyError
import kotlinx.android.synthetic.main.detail_event.*
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.helpers.HamersActivity
import nl.ecci.hamers.helpers.Utils
import nl.ecci.hamers.loader.Loader
import nl.ecci.hamers.loader.PostCallback
import nl.ecci.hamers.users.User
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SingleEventActivity : HamersActivity() {

    private var event: Event? = null
    private var ownUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_event)

        initToolbar()

        event = Utils.getEvent(prefs, intent.getIntExtra(Event.EVENT, 1))
        ownUser = Utils.getOwnUser(PreferenceManager.getDefaultSharedPreferences(this))

        initSignups()

        event_title.text = event?.title
        fillDetailRow(description_row, getString(R.string.description), event!!.description)

        if (Date().after(event?.deadline)) {
            button_layout.visibility = View.GONE
            single_event_scrollview.removeView(button_layout)
        }

        fillImageRow(date_row, "Datum", MainActivity.appDF.format(event!!.date), R.drawable.ic_event)
        date_row.isClickable = true
        date_row.setOnClickListener {
            val startMillis = event!!.date.time
            val builder = CalendarContract.CONTENT_URI.buildUpon()
            builder.appendPath("time")
            ContentUris.appendId(builder, startMillis)
            val intent = Intent(Intent.ACTION_VIEW).setData(builder.build())
            startActivity(intent)
        }

        if (event!!.location.isNotEmpty()) {
            fillImageRow(location_row, "Locatie", event!!.location, R.drawable.location)

            location_row.isClickable = true
            location_row.setOnClickListener {
                // Create a Uri from an intent string. Use the result to create an Intent.
                val uri = Uri.parse("geo:0,0?q=" + event!!.location)
                // Create an Intent from uri. Set the action to ACTION_VIEW
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.`package` = "com.google.android.apps.maps"
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        } else {
            location_row.visibility = View.GONE
        }

        present_button.setOnClickListener { postSignup(true, null) }
        absent_button.setOnClickListener { askForReason() }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        if (ownUser?.id != event!!.userID) {
            menu.removeItem(R.id.edit_item)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.edit_item -> {
                Intent(this, NewEventActivity::class.java).putExtra(Event.EVENT, event?.id)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun postSignup(status: Boolean?, reason: String?) {
        val body = JSONObject()
        try {
            body.put("event_id", event!!.id)
            body.put("status", status!!.toString())

            // Attendance is mandatory, so ask for the reason for absence!
            if (!status && event!!.signupMandatory && reason != null) {
                body.put("reason", reason)
            }
        } catch (ignored: JSONException) {
        }

        Loader.postOrPatchData(this, Loader.SIGNUPURL, body, -1, object : PostCallback {
            override fun onSuccess(response: JSONObject) {
                finish()
            }

            override fun onError(error: VolleyError) {

            }
        })
    }

    private fun initSignups() {
        val signUps = event!!.signUps
        val present = ArrayList<String>()
        val absent = ArrayList<String>()
        signUps.indices
                .map { signUps[it] }
                .forEach {
                    if (it.isAttending) {
                        present.add(Utils.getUser(prefs, it.userID).name)
                    } else {
                        absent.add(Utils.getUser(prefs, it.userID).name)
                    }
                }

        if (present.size > 0) {
            for (name in present) {
                present_insert_point.addView(newSingleRow(name, present_insert_point), ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            }
        } else {
            single_event_layout.removeView(present_layout)
        }

        if (absent.size > 0) {
            for (name in absent) {
                absent_insert_point.addView(newSingleRow(name, absent_insert_point), ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            }
        } else {
            single_event_layout.removeView(absent_layout)
        }

        if (present.contains(ownUser?.name)) {
            present_button.visibility = View.GONE
        } else if (absent.contains(ownUser?.name)) {
            absent_button.visibility = View.GONE
        }
    }

    private fun askForReason() {
        val alert = AlertDialog.Builder(this)

        val reasonField = EditText(this)
        alert.setTitle(R.string.attendance_reason_title)
        reasonField.setHint(R.string.attendance_reason_message)

        alert.setView(reasonField)

        alert.setPositiveButton(android.R.string.yes) { _, _ ->
            val reason = reasonField.text.toString()
            if (reason.length > 5) {
                postSignup(false, reason)
            } else {
                Toast.makeText(this@SingleEventActivity, R.string.attendance_reason_size, Toast.LENGTH_SHORT).show()
            }
        }

        alert.setNegativeButton(android.R.string.no) { _, _ ->
            // Do nothing.
        }

        alert.show()
        val layoutParams = FrameLayout.LayoutParams(16, FrameLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(16, 16, 16, 16)
        reasonField.layoutParams = layoutParams
    }

}
