package nl.ecci.hamers.utils

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import nl.ecci.hamers.R
import nl.ecci.hamers.data.Loader
import nl.ecci.hamers.models.*
import org.json.JSONException
import org.json.JSONObject
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
                // Download the rest
                val token = FirebaseInstanceId.getInstance().token.toString()
                sendRegistrationToServer(context, token)
                Loader.getAllData(context)
                // Notify the user
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
    fun hasApiKey(context: Context, prefs: SharedPreferences?) {
        if (prefs?.getString(Loader.APIKEYKEY, null) == null) {
            if (Utils.alertDialog == null) {
                showApiKeyDialog(context)
            } else if (!Utils.alertDialog!!.isShowing) {
                showApiKeyDialog(context)
            }
        } else {
            val token = FirebaseInstanceId.getInstance().token.toString()
            sendRegistrationToServer(context, token)
            Loader.getAllData(context)
        }
    }

    fun sendRegistrationToServer(context: Context, token: String) {
        val body = JSONObject()
        try {
            body.put("device", token)
        } catch (ignored: JSONException) {
        }

        Loader.postOrPatchData(context, Loader.FCMURL, body, Utils.notFound, null)
    }

    fun getGravatarURL(email: String): String {
        return String.format("https://gravatar.com/avatar/%s/?s=1920", Utils.md5(email))
    }

    fun convertNicknames(nicknames: ArrayList<Nickname>): String {
        val sb = StringBuilder()
        for (nickname in nicknames) {
            sb.append(nickname.nickname).append(" ")
        }
        return sb.toString()
    }

    fun usernameToID(prefs: SharedPreferences, name: String): Int {
        var result = Utils.notFound
        val gson = GsonBuilder().create()
        val type = object : TypeToken<ArrayList<User>>() {

        }.type
        val userList = gson.fromJson<ArrayList<User>>(prefs.getString(Loader.USERURL, null), type)

        userList.filter { it.name == name }
                .forEach { result = it.id }
        return result
    }

    fun createActiveMemberList(prefs: SharedPreferences): ArrayList<User> {
        val result = ArrayList<User>()
        val gson = GsonBuilder().create()
        val type = object : TypeToken<ArrayList<User>>() {
        }.type

        gson.fromJson<ArrayList<User>>(prefs.getString(Loader.USERURL, null), type)?.filterTo(result) {
            it.member === User.Member.LID || it.id == 11 // Extern account
        }
        return result
    }

    /**
     * Get user by id
     */
    fun getUser(context: Context, id: Int): User {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val userList: ArrayList<User>?
        var result = User()
        val gson = GsonBuilder().create()
        val type = object : TypeToken<ArrayList<User>>() {

        }.type

        userList = gson.fromJson<ArrayList<User>>(prefs?.getString(Loader.USERURL, null), type)

        userList?.filter { it.id == id }?.forEach { result = it }

        return result
    }

    /**
     * Get user by nickname
     */
    fun getUserByNick(context: Context, id: Int): User {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        var result = User()

        val gson = GsonBuilder().create()
        val type = object : TypeToken<ArrayList<User>>() {
        }.type

        val userList = gson.fromJson<ArrayList<User>>(prefs?.getString(Loader.USERURL, null), type)

        if (userList != null) {
            for (user in userList) {
                repeat(user.nicknames.filter { it.id == id }.size) { result = user }
            }
        }

        return result
    }

    fun getOwnUser(context: Context): User {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val user: User?
        val gson = GsonBuilder().create()
        user = gson.fromJson(prefs.getString(Loader.WHOAMIURL, null), User::class.java)
        if (user == null) {
            return User()
        }
        return user
    }

    fun getEvent(context: Context, id: Int): Event {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        var result = Event()
        val gson = GsonBuilder().create()
        val type = object : TypeToken<ArrayList<Event>>() {
        }.type

        val eventList = gson.fromJson<ArrayList<Event>>(prefs?.getString(Loader.EVENTURL, null), type)

        eventList?.filter { it.id == id }
                ?.forEach { result = it }

        return result
    }

    fun getSignUp(context: Context, id: Int): SignUp {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        var result = SignUp()
        val gson = GsonBuilder().create()
        val type = object : TypeToken<ArrayList<SignUp>>() {
        }.type

        val signUpList: ArrayList<SignUp>?
        if (prefs != null) {
            signUpList = gson.fromJson<ArrayList<SignUp>>(prefs.getString(Loader.SIGNUPURL, null), type)
            signUpList?.filter { it.id == id }
                    ?.forEach { result = it }
        }

        return result
    }

    fun getBeer(context: Context, id: Int): Beer {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        var result = Beer()
        val gson = GsonBuilder().create()
        val type = object : TypeToken<ArrayList<Beer>>() {
        }.type

        val beerList = gson.fromJson<ArrayList<Beer>>(prefs?.getString(Loader.BEERURL, null), type)

        beerList?.filter { it.id == id }?.forEach { result = it }

        return result
    }

    fun getReview(context: Context, id: Int): Review {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        var result = Review()
        val gson = GsonBuilder().create()
        val type = object : TypeToken<ArrayList<Review>>() {
        }.type

        val reviewList = gson.fromJson<ArrayList<Review>>(prefs?.getString(Loader.REVIEWURL, null), type)

        reviewList?.filter { it.id == id }?.forEach { result = it }

        return result
    }

    fun getMeeting(context: Context, id: Int): Meeting {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        var result = Meeting()
        val gson = GsonBuilder().create()
        val type = object : TypeToken<ArrayList<Meeting>>() {

        }.type
        val meetingList = gson.fromJson<ArrayList<Meeting>>(prefs?.getString(Loader.MEETINGURL, null), type)

        meetingList?.filter { it.id == id }?.forEach { result = it }

        return result
    }
}