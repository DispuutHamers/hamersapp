package nl.ecci.hamers.loader

import android.content.Context

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

internal class Singleton private constructor(private val mCtx: Context) {
    private var mRequestQueue: RequestQueue? = null

    init {
        mRequestQueue = requestQueue
    }

    private // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
    val requestQueue: RequestQueue?
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(mCtx.applicationContext)
            }
            return mRequestQueue
        }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue?.add(req)
    }

    companion object {
        private var mInstance: Singleton? = null

        fun getInstance(context: Context): Singleton {
            if (mInstance == null) {
                mInstance = Singleton(context)
            }
            return mInstance as Singleton
        }
    }
}