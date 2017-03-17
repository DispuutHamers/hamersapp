package nl.ecci.hamers.fcm

import android.util.Log

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

import org.json.JSONException
import org.json.JSONObject

import nl.ecci.hamers.loader.Loader

class InstanceIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(MessagingService.TAG, "Refreshed token: " + refreshedToken!!)
        sendRegistrationToServer(refreshedToken)
    }

    private fun sendRegistrationToServer(token: String) {
        val body = JSONObject()
        try {
            body.put("device", token)
        } catch (ignored: JSONException) {
        }

        Loader.postOrPatchData(applicationContext, Loader.GCMURL, body, -1, null)
    }
}