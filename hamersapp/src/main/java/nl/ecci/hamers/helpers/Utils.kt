package nl.ecci.hamers.helpers

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.preference.PreferenceManager
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import nl.ecci.hamers.MainActivity.prefs
import nl.ecci.hamers.R
import nl.ecci.hamers.loader.Loader
import nl.ecci.hamers.users.User
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.util.*

object Utils {
    var alertDialog: AlertDialog? = null
    val unknown = "Unknown"
    val notFound = -1

    fun md5(message: String): String {
        return String(Hex.encodeHex(DigestUtils.md5(message)))
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

    fun getIdFromUri(uri: Uri): Int {
        val path = uri.path
        return Integer.parseInt(path.substring(path.lastIndexOf('/') + 1))
    }
}
