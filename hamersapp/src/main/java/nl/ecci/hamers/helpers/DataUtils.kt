package nl.ecci.hamers.helpers

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.beers.Beer
import nl.ecci.hamers.events.Event
import nl.ecci.hamers.loader.Loader
import nl.ecci.hamers.meetings.Meeting
import nl.ecci.hamers.users.User
import java.util.*

object DataUtils {

    /**
     * Show the dialog for entering the apikey on startup
     */
    private fun showApiKeyDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.apikeydialogtitle))
        builder.setMessage(context.getString(R.string.apikeydialogmessage))
        val apiKey = EditText(context)
        apiKey.setSingleLine()
        apiKey.hint = context.getString(R.string.apikey_hint)

        val container = FrameLayout(context)
        container.addView(apiKey)

        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.marginStart = context.resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.marginEnd = context.resources.getDimensionPixelSize(R.dimen.dialog_margin)
        apiKey.layoutParams = params

        builder.setView(container)

        builder.setPositiveButton(context.getString(R.string.dialog_positive)) { _, _ ->
            val key = apiKey.text
            if (key.toString().isNotBlank()) {
                // Store in memory
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Loader.APIKEYKEY, key.toString()).apply()
                Utils.showToast(context, context.resources.getString(R.string.downloading), Toast.LENGTH_SHORT)
            } else {
                Utils.showToast(context, context.resources.getString(R.string.store_key_settings), Toast.LENGTH_SHORT)
            }
        }
        builder.show()
    }

    /**
     * Checks if the API key is present
     */
    fun hasApiKey(context: Context, prefs: SharedPreferences) {
        if (prefs.getString(Loader.APIKEYKEY, null) == null) {
            if (Utils.alertDialog == null) {
                showApiKeyDialog(context)
            } else if (!Utils.alertDialog!!.isShowing) {
                showApiKeyDialog(context)
            }
        }
    }

    fun getGravatarURL(email: String): String {
        return String.format("http://gravatar.com/avatar/%s/?s=1920", Utils.md5(email))
    }

    fun convertNicknames(nicknames: ArrayList<User.Nickname>): String {
        val sb = StringBuilder()
        for (nickname in nicknames) {
            sb.append(nickname.nickname).append(" ")
        }
        return sb.toString()
    }

    fun usernameToID(prefs: SharedPreferences, name: String): Int {
        var result = Utils.notFound
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val type = object : TypeToken<ArrayList<User>>() {

        }.type
        val userList = gson.fromJson<ArrayList<User>>(prefs.getString(Loader.USERURL, null), type)

        userList.filter { it.name == name }
                .forEach { result = it.id }
        return result
    }

    fun createActiveMemberList(): ArrayList<User> {
        val result = ArrayList<User>()
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val type = object : TypeToken<ArrayList<User>>() {
        }.type

        gson.fromJson<ArrayList<User>>(MainActivity.prefs.getString(Loader.USERURL, null), type)?.filterTo(result) { it.member === User.Member.LID }
        return result
    }

    fun getUser(context: Context, id: Int): User {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)


        val userList: ArrayList<User>?
        var result = User(Utils.notFound, Utils.unknown, "example@example.org", Utils.notFound, Utils.notFound, User.Member.LID, Utils.notFound, ArrayList<User.Nickname>(), Date())
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val type = object : TypeToken<ArrayList<User>>() {

        }.type

        userList = gson.fromJson<ArrayList<User>>(prefs?.getString(Loader.USERURL, null), type)

        if (userList != null) {
            userList.filter { it.id == id }
                    .forEach { result = it }
        }

        return result
    }

    fun getOwnUser(prefs: SharedPreferences): User {
        var user: User?
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        user = gson.fromJson(prefs.getString(Loader.WHOAMIURL, null), User::class.java)
        if (user == null) {
            user = User(Utils.notFound, Utils.unknown, "example@example.org", Utils.notFound, Utils.notFound, User.Member.LID, Utils.notFound, ArrayList<User.Nickname>(), Date())
        }
        return user
    }

    fun getEvent(prefs: SharedPreferences?, id: Int): Event {
        var result = Event(Utils.notFound, Utils.unknown, Utils.unknown, Utils.unknown, Date(), Date(), Date(), ArrayList<Event.SignUp>(), Date(), false)
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val type = object : TypeToken<ArrayList<Event>>() {
        }.type

        val eventList = gson.fromJson<ArrayList<Event>>(prefs?.getString(Loader.EVENTURL, null), type)

        if (eventList != null) {
            eventList.filter { it.id == id }
                    .forEach { result = it }
        }

        return result
    }

    fun getBeer(prefs: SharedPreferences?, id: Int): Beer {
        var result = Beer(Utils.notFound, Utils.unknown, Utils.unknown, Utils.unknown, Utils.unknown, Utils.unknown, Utils.unknown, Utils.unknown, Utils.unknown, Date())
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val type = object : TypeToken<ArrayList<Beer>>() {
        }.type

        val beerList = gson.fromJson<ArrayList<Beer>>(prefs?.getString(Loader.BEERURL, null), type)

        if (beerList != null) {
            beerList.filter { it.id == id }
                    .forEach { result = it }
        }

        return result
    }

    fun getMeeting(prefs: SharedPreferences, id: Int): Meeting {
        val date = Date()
        var result = Meeting(Utils.notFound, Utils.unknown, Utils.unknown, Utils.unknown, Utils.notFound, date, date, date)
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val type = object : TypeToken<ArrayList<Meeting>>() {

        }.type
        val meetingList = gson.fromJson<ArrayList<Meeting>>(prefs.getString(Loader.MEETINGURL, null), type)

        if (meetingList != null) {
            meetingList
                    .filter { it.id == id }
                    .forEach { result = it }
        }

        return result
    }
}