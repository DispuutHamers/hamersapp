package nl.ecci.hamers.helpers

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import android.text.Editable
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils

import java.lang.reflect.Type
import java.util.ArrayList
import java.util.Date

import nl.ecci.hamers.R
import nl.ecci.hamers.beers.Beer
import nl.ecci.hamers.events.Event
import nl.ecci.hamers.loader.Loader
import nl.ecci.hamers.meetings.Meeting
import nl.ecci.hamers.users.User

import android.content.Context.INPUT_METHOD_SERVICE
import nl.ecci.hamers.MainActivity.prefs

object Utils {
    private var alertDialog: AlertDialog? = null
    val unknown = "Unknown"

    private fun md5(message: String): String {
        return String(Hex.encodeHex(DigestUtils.md5(message)))
    }

    /**
     * Show the dialog for entering the apikey on startup
     */
    private fun showApiKeyDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.apikeydialogtitle))
        builder.setMessage(context.getString(R.string.apikeydialogmessage))
        val apiKey = EditText(context)
        apiKey.hint = context.getString(R.string.apikey_hint)
        builder.setView(apiKey)
        builder.setPositiveButton(context.getString(R.string.dialog_positive)) { dialog, whichButton ->
            val key = apiKey.text
            if (key.toString() != "") {
                // Store in memory
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Loader.APIKEYKEY, key.toString()).apply()
                showToast(context, context.resources.getString(R.string.dowloading), Toast.LENGTH_SHORT)
            } else {
                showToast(context, context.resources.getString(R.string.store_key_settings), Toast.LENGTH_SHORT)
            }
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    /**
     * Checks if the API key is present
     */
    fun hasApiKey(context: Context, prefs: SharedPreferences) {
        if (prefs.getString("apikey", null) == null) {
            if (Utils.alertDialog == null) {
                Utils.showApiKeyDialog(context)
            } else if (!Utils.alertDialog!!.isShowing) {
                Utils.showApiKeyDialog(context)
            }
        }
    }

    fun stringArrayToCharSequenceArray(stringArray: Array<Any>): Array<CharSequence?> {
        val charSequenceArray = arrayOfNulls<CharSequence>(stringArray.size)

        for (i in stringArray.indices)
            charSequenceArray[i] = stringArray[i] as String

        return charSequenceArray
    }

    /**
     * Get app version
     */
    fun getAppVersion(context: Context): String {
        var versionName: String
        try {
            versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            versionName = ""
        }

        return versionName
    }

    fun getGravatarURL(email: String): String {
        return String.format("http://gravatar.com/avatar/%s/?s=1920", md5(email))
    }

    fun convertNicknames(nicknames: ArrayList<User.Nickname>): String {
        val sb = StringBuilder()
        for (nickname in nicknames) {
            sb.append(nickname.nickname).append(" ")
        }
        return sb.toString()
    }

    fun usernameToID(prefs: SharedPreferences, name: String): Int {
        var result = -1
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val type = object : TypeToken<ArrayList<User>>() {

        }.type
        val userList = gson.fromJson<ArrayList<User>>(prefs.getString(Loader.USERURL, null), type)

        for (user in userList) {
            if (user.name == name) {
                result = user.id
            }
        }
        return result
    }

    fun createActiveMemberList(): ArrayList<User> {
        val result = ArrayList<User>()
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val type = object : TypeToken<ArrayList<User>>() {

        }.type
        val userList = gson.fromJson<ArrayList<User>>(prefs.getString(Loader.USERURL, null), type)

        if (userList != null) {
            for (user in userList) {
                if (user.member === User.Member.LID) {
                    result.add(user)
                }
            }
        }
        return result
    }

    fun getUser(prefs: SharedPreferences, id: Int): User {
        val userList: ArrayList<User>?
        var result = User(-1, unknown, "example@example.org", 0, 0, User.Member.LID, -1, ArrayList<User.Nickname>(), Date())
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val type = object : TypeToken<ArrayList<User>>() {

        }.type

        userList = gson.fromJson<ArrayList<User>>(prefs.getString(Loader.USERURL, null), type)

        if (userList != null) {
            for (user in userList) {
                if (user.id == id) {
                    result = user
                }
            }
        }

        return result
    }

    fun getOwnUser(prefs: SharedPreferences): User {
        var user: User?
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        user = gson.fromJson(prefs.getString(Loader.WHOAMIURL, null), User::class.java)
        if (user == null) {
            user = User(-1, unknown, "example@example.org", 0, 0, User.Member.LID, -1, ArrayList<User.Nickname>(), Date())
        }
        return user
    }

    fun getEvent(prefs: SharedPreferences?, id: Int): Event {
        var result = Event(1, unknown, unknown, unknown, Date(), Date(), Date(), ArrayList<Event.SignUp>(), Date(), false)
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val type = object : TypeToken<ArrayList<Event>>() {

        }.type
        var eventList: ArrayList<Event>? = null
        if (prefs != null) {
            eventList = gson.fromJson<ArrayList<Event>>(prefs.getString(Loader.EVENTURL, null), type)
        }

        if (eventList != null) {
            for (event in eventList) {
                if (event.id == id) {
                    result = event
                }
            }
        }

        return result
    }

    fun getBeer(prefs: SharedPreferences, id: Int): Beer {
        var result = Beer(-1, unknown, unknown, unknown, unknown, unknown, unknown, unknown, unknown, Date())
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val type = object : TypeToken<ArrayList<Beer>>() {

        }.type
        val beerList = gson.fromJson<ArrayList<Beer>>(prefs.getString(Loader.BEERURL, null), type)

        if (beerList != null) {
            for (beer in beerList) {
                if (beer.id == id) {
                    result = beer
                }
            }
        }

        return result
    }

    fun getMeeting(prefs: SharedPreferences, id: Int): Meeting {
        val date = Date()
        var result = Meeting(-1, unknown, unknown, unknown, -1, date, date, date)
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val type = object : TypeToken<ArrayList<Meeting>>() {

        }.type
        val meetingList = gson.fromJson<ArrayList<Meeting>>(prefs.getString(Loader.MEETINGURL, null), type)

        if (meetingList != null) {
            for (meeting in meetingList) {
                if (meeting.id == id) {
                    result = meeting
                }
            }
        }

        return result
    }

    /**
     * Hides the soft keyboard
     */
    fun hideKeyboard(activity: Activity?) {
        if (activity != null) {
            if (activity.currentFocus != null) {
                val inputMethodManager = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
        }
    }

    private var mToast: Toast? = null

    fun showToast(context: Context, text: String, duration: Int) {
        if (mToast != null) mToast!!.cancel()
        mToast = Toast.makeText(context, text, duration)
        mToast!!.show()
    }
}
