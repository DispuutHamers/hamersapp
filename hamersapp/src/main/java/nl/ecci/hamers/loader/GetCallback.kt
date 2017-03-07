package nl.ecci.hamers.loader

import com.android.volley.VolleyError

interface GetCallback {
    fun onSuccess(response: String)

    fun onError(error: VolleyError)
}
