package nl.ecci.hamers.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import nl.ecci.hamers.loader.Loader;

public class InstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(MessagingService.TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        JSONObject body = new JSONObject();
        try {
            body.put("device", token);
        } catch (JSONException ignored) {
        }

        Loader.INSTANCE.postOrPatchData(getApplicationContext(), Loader.INSTANCE.getGCMURL(), body, -1, null);
    }
}