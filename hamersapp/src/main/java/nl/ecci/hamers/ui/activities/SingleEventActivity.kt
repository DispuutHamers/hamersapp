package nl.ecci.hamers.ui.activities

import android.content.*
import android.net.Uri
import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_general.*
import kotlinx.android.synthetic.main.stub_detail_event.*
import nl.ecci.hamers.BuildConfig
import nl.ecci.hamers.R
import nl.ecci.hamers.data.Loader
import nl.ecci.hamers.data.PostCallback
import nl.ecci.hamers.models.Event
import nl.ecci.hamers.models.User
import nl.ecci.hamers.utils.DataUtils
import nl.ecci.hamers.utils.Utils
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SingleEventActivity : HamersActivity() {

    private var event: Event? = null
    private var ownUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_general)

        initToolbar()

        stub.layoutResource = R.layout.stub_detail_event
        stub.inflate()

        ownUser = DataUtils.getOwnUser(this)

        // Intent by clicking an event in EventFragment or by clicking a link elsewhere
        val appLinkData = intent.data
        val eventID = intent.getIntExtra(Event.EVENT, Utils.notFound)
        if (appLinkData != null) {
            event = DataUtils.getEvent(this, Utils.getIdFromUri(appLinkData))
        } else {
            event = DataUtils.getEvent(this, eventID)
        }

        var eventTitle = event?.title
        if (BuildConfig.DEBUG) {
            eventTitle += " (" + event?.id + ")"
        }

        event_title.text = eventTitle
        fillDetailRow(description_row, getString(R.string.description), event?.description)

        if (Date().after(event?.deadline)) {
            button_layout.visibility = View.GONE
            scrollview.removeView(button_layout)
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

        if (event!!.location.isNotBlank()) {
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

        present_button.setOnClickListener { postSignUp(true, null) }
        if (event!!.attendance) {
            absent_button.setOnClickListener { askForReason() }
        } else {
            absent_button.setOnClickListener { postSignUp(false, null) }
        }

        initSignUps()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_menu, menu)
        if (ownUser?.id == event?.userID || ownUser?.admin as Boolean) {
            // Event belongs to user or user is admin
            menu.findItem(R.id.send_reminder).isVisible = true
            menu.findItem(R.id.edit_item).isVisible = true
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
                startActivity(Intent(this, NewEventActivity::class.java).putExtra(Event.EVENT, event?.id))
                return true
            }
            R.id.share_item -> {
                // Copy link to clipboard
                val clipboard : ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(Event.EVENT, getString(R.string.host) + Loader.EVENTURL + "/" + event?.id)
                clipboard.primaryClip = clip
                // Notify user
                Toast.makeText(this, R.string.url_copied, Toast.LENGTH_SHORT).show()
            }
            R.id.send_reminder -> {
                AlertDialog.Builder(this)
                        .setTitle(R.string.reminder_title)
                        .setMessage(R.string.reminder_message)
                        .setPositiveButton(android.R.string.yes) { _, _ ->
                            postReminder()
                        }
                        .setNegativeButton(android.R.string.no) { _, _ ->
                            // Do nothing.
                        }
                        .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun postSignUp(status: Boolean?, reason: String?) {
        val body = JSONObject()
        try {
            body.put("event_id", event!!.id)
            body.put("status", status!!.toString())

            // Attendance is mandatory, so ask for the reason for absence!
            if (!status && event!!.attendance && reason != null) {
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

    private fun postReminder() {
        val url = Loader.EVENTURL + "/" + event?.id + "/" + Loader.REMINDURL
        Loader.postOrPatchData(this, url, JSONObject(), -1, null)
    }

    private fun initSignUps() {
        val signUps = event?.signUps
        val present = ArrayList<String>()
        val absent = ArrayList<String>()
        signUps?.indices
                ?.map { signUps[it] }
                ?.forEach {
                    if (it.isAttending) {
                        present.add(DataUtils.getUser(this, it.userID).name)
                    } else {
                        absent.add(DataUtils.getUser(this, it.userID).name)
                    }
                }

        if (present.size > 0) {
            for (name in present) {
                present_insert_point.addView(newSingleRow(name, present_insert_point), ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            }
            event_present_count.text = present.size.toString()
        } else {
            single_event_layout.removeView(present_layout)
        }

        if (absent.size > 0) {
            for (name in absent) {
                absent_insert_point.addView(newSingleRow(name, absent_insert_point), ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            }
            event_absent_count.text = absent.size.toString()
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
                .setTitle(R.string.attendance_reason_title)

        val input = EditText(this)
        input.setSingleLine()
        input.setHint(R.string.attendance_reason_message)

        val container = FrameLayout(this)
        container.addView(input)

        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.marginStart = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.marginEnd = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        input.layoutParams = params

        alert.setView(container)

        alert.setPositiveButton(android.R.string.yes) { _, _ ->
            val reason = input.text.toString()
            if (reason.length > 5) {
                postSignUp(false, reason)
            } else {
                Toast.makeText(this@SingleEventActivity, R.string.attendance_reason_size, Toast.LENGTH_SHORT).show()
            }
        }
        alert.setNegativeButton(android.R.string.no) { _, _ ->
            // Do nothing.
        }
        alert.show()
    }

}
