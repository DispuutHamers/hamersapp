package nl.ecci.hamers.fcm

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import nl.ecci.hamers.utils.DataUtils.sendRegistrationToServer

class InstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(MessagingService.TAG, "Refreshed token: " + refreshedToken!!)
        sendRegistrationToServer(this, refreshedToken)
    }

}