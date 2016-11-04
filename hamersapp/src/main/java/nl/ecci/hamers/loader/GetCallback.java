package nl.ecci.hamers.loader;

import com.android.volley.VolleyError;

import org.json.JSONArray;

public interface GetCallback {
    void onSuccess(String response);

    void onError(VolleyError error);
}
