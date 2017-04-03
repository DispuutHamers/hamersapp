package nl.ecci.hamers.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.PackageManager
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import nl.ecci.hamers.ui.activities.MainActivity
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    private var mToast: Toast? = null
    var alertDialog: AlertDialog? = null
    val unknown = "Unknown"
    val notFound = -1

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

    fun showToast(context: Context, text: String, duration: Int) {
        if (mToast != null) mToast!!.cancel()
        mToast = Toast.makeText(context, text, duration)
        mToast!!.show()
    }

    fun getIdFromUri(uri: Uri): Int {
        val path = uri.path
        return Integer.parseInt(path.substring(path.lastIndexOf('/') + 1))
    }

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
     * Parse date
     */
    fun parseDate(dateString: String): Date {
        var date: Date = Date()
        try {
            // Event date
            if (dateString.isNotBlank()) {
                val inputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", MainActivity.locale)
                date = inputFormat.parse(dateString)
            }
        } catch (ignored: ParseException) {
        }

        return date
    }
}
