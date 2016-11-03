package nl.ecci.hamers.helpers;

import com.android.volley.VolleyError;

public interface VolleyCallback {
    void onSuccess();

    void onError(VolleyError error);
}
