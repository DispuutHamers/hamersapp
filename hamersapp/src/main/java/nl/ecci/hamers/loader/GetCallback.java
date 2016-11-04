package nl.ecci.hamers.loader;

import com.android.volley.VolleyError;

public interface GetCallback {
    void onSuccess(String response);

    void onError(VolleyError error);
}
