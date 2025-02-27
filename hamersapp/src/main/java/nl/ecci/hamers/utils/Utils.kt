package nl.ecci.hamers.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import nl.ecci.hamers.ui.activities.MainActivity
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    var alertDialog: AlertDialog? = null
    const val unknown = "Unknown"
    const val notFound = -1

    /**
     * Get app version
     */
    fun getAppVersion(context: Context?): String? {
        return try {
            context?.packageManager?.getPackageInfo(context.packageName, 0)?.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
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

    fun showToast(context: Context?, text: String, duration: Int) {
        if (context != null)
        Toast.makeText(context, text, duration).show()
    }

    fun getIdFromUri(uri: Uri): Int {
        val path = uri.path
        return Integer.parseInt(path!!.substring(path.lastIndexOf('/') + 1))
    }

    fun md5(message: String): String = String(Hex.encodeHex(DigestUtils.md5(message)))

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
        var date = Date()
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

    /**
     * To HTML
     */
    fun toHtml(s: String?) : Spanned {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(s, Html.FROM_HTML_MODE_COMPACT)
        return Html.fromHtml(s)
    }
}
