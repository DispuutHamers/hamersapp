package nl.ecci.hamers.loader;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface PostCallback {
    void onSuccess(JSONObject response);

    void onError(VolleyError error);
}
