package nl.ecci.hamers.fcm

import android.util.Log

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import nl.ecci.hamers.helpers.DataUtils.sendRegistrationToServer
import nl.ecci.hamers.helpers.Utils

import org.json.JSONException
import org.json.JSONObject

import nl.ecci.hamers.loader.Loader

class InstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(MessagingService.TAG, "Refreshed token: " + refreshedToken!!)
        sendRegistrationToServer(this, refreshedToken)
    }

}