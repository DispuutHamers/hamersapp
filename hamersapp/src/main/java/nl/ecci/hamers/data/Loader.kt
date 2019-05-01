package nl.ecci.hamers.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import nl.ecci.hamers.R
import nl.ecci.hamers.utils.Utils
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

object Loader {
    private const val APIURL = "api/v2/"
    // URL Appendices
    const val QUOTEURL = "quotes"
    const val USERURL = "users"
    const val EVENTURL = "events"
    const val REMINDURL = "remind"
    const val NEWSURL = "news"
    const val BEERURL = "beers"
    const val REVIEWURL = "reviews"
    const val WHOAMIURL = "whoami"
    const val MEETINGURL = "meetings"
    const val SIGNUPURL = "signups"
    const val STICKERURL = "stickers"
    const val CHANGEURL = "changes?since=2017-03-16T17:00:00.000Z"
    // Data keys (excluded form urls below)
    const val APIKEYKEY = "apikey"
    const val FCMURL = "register"

    val urls = hashSetOf(QUOTEURL, USERURL, EVENTURL, SIGNUPURL, NEWSURL, BEERURL, REVIEWURL, WHOAMIURL, MEETINGURL, SIGNUPURL, STICKERURL, CHANGEURL)

    fun getData(context: Context, dataURL: String, urlAppendix: Int, callback: GetCallback?, params: Map<String, String>? = null) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val url = buildURL(context, dataURL, params, urlAppendix)

        val request = object : StringRequest(Method.GET, url,
                Response.Listener<String> { response ->
                    Log.d("GET-response", response)
                    // Do not save events from upcoming events fragment
                    if (!(dataURL == EVENTURL && params != null) && urlAppendix == Utils.notFound) {
                        prefs.edit().putString(dataURL, response).apply()
                    }
                    callback?.onSuccess(response)
                },
                Response.ErrorListener { error ->
                    Log.d("GET-error", error.toString())
                    handleErrorResponse(context, error)
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Token token=" + prefs.getString(APIKEYKEY, "")
                return headers
            }

            public override fun getParams(): Map<String, String>? {
                return params
            }
        }

        Log.d("Request", request.toString())
        Singleton.getInstance(context).addToRequestQueue<String>(request)
    }

    fun postOrPatchData(context: Context, dataURL: String, body: JSONObject, urlAppendix: Int, callback: PostCallback?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val url = buildURL(context, dataURL, null, urlAppendix)

        Log.d("PostRequest: ", body.toString())

        val request = object : JsonObjectRequest(url, body,
                Response.Listener<JSONObject> { response ->
                    Log.d("POST-response", response.toString())

                    if (dataURL != FCMURL)
                        Utils.showToast(context, context.getString(R.string.posted), Toast.LENGTH_SHORT)

                    callback?.onSuccess(response)
                },
                Response.ErrorListener { error ->
                    Log.d("POST-error", error.toString())
                    callback?.onError(error)
                    handleErrorResponse(context, error)
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = "Token token=" + prefs.getString(APIKEYKEY, "")!!
                params["Content-Type"] = "Application/json"
                if (urlAppendix != Utils.notFound) {
                    params["X-HTTP-Method-Override"] = "PATCH"
                }
                return params
            }
        }

        // Set (temporary) timeout value for reminders
        if (dataURL.contains(REMINDURL))
            request.retryPolicy = DefaultRetryPolicy(
                    45 * 1000, // 45 seconds
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

        Log.d("PostRequest: ", request.toString())
        Singleton.getInstance(context).addToRequestQueue<JSONObject>(request)
    }

    private fun buildURL(context: Context, URL: String, params: Map<String, String>?, appendix: Int): String {
        val builder = StringBuilder()
        builder.append(context.getString(R.string.host)).append(APIURL).append(URL)

        if (appendix != Utils.notFound) {
            builder.append("/").append(appendix)
        }

        if (params != null) {
            builder.append("?")
            for (key in params.keys) {
                var value: Any? = params[key]
                if (value != null) {
                    try {
                        value = URLEncoder.encode((value).toString(), "UTF-8")
                        if (builder.isNotBlank())
                            builder.append("&")
                        builder.append(key).append("=").append(value)
                    } catch (ignored: UnsupportedEncodingException) {
                    }

                }
            }
        }
        return builder.toString()
    }

    private fun handleErrorResponse(context: Context, error: VolleyError) {
        when (error) {
            is AuthFailureError -> // Wrong API key
                Utils.showToast(context, context.getString(R.string.auth_error), Toast.LENGTH_SHORT)
            is TimeoutError -> // Timeout
                Utils.showToast(context, context.getString(R.string.timeout_error), Toast.LENGTH_SHORT)
            is ServerError -> // Server error (500)
                Utils.showToast(context, context.getString(R.string.server_error), Toast.LENGTH_SHORT)
            is NoConnectionError -> // No network connection
                Utils.showToast(context, context.getString(R.string.connection_error), Toast.LENGTH_SHORT)
            is NetworkError -> // Network error
                Utils.showToast(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT)
            else -> // Other error
                Utils.showToast(context, context.getString(R.string.volley_error), Toast.LENGTH_SHORT)
        }
    }

    fun getAllData(context: Context) {
        for (url in urls) {
            getData(context, url, -1, null, null)
        }
    }
}
