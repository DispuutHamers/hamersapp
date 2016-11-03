package nl.ecci.hamers.helpers;

import com.android.volley.VolleyError;

import org.json.JSONArray;

public interface VolleyCallback {
    void onSuccess(JSONArray response);

    void onError(VolleyError error);
}
