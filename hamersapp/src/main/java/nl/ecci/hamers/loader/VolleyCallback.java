package nl.ecci.hamers.loader;

import com.android.volley.VolleyError;

import org.json.JSONArray;

public interface VolleyCallback {
    void onSuccess(JSONArray response);

    void onError(VolleyError error);
}
