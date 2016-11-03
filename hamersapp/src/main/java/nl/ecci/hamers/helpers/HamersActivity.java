package nl.ecci.hamers.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import nl.ecci.hamers.R;

@SuppressLint("Registered")
public class HamersActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return (super.onOptionsItemSelected(item));
    }

    private static void handleErrorResponse(@NonNull Context context, @NonNull VolleyError error) {
        if (error instanceof AuthFailureError) {
            // Wrong API key
            Toast.makeText(context, context.getString(R.string.auth_error), Toast.LENGTH_SHORT).show();
        } else if (error instanceof TimeoutError) {
            // Timeout
            Toast.makeText(context, context.getString(R.string.timeout_error), Toast.LENGTH_SHORT).show();
        } else if (error instanceof ServerError) {
            // Server error (500)
            Toast.makeText(context, context.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
        } else if (error instanceof NoConnectionError) {
            // No network connection
            Toast.makeText(context, context.getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        } else if (error instanceof NetworkError) {
            // Network error
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        } else {
            // Other error
            Toast.makeText(context, context.getString(R.string.volley_error), Toast.LENGTH_SHORT).show();
        }
    }
}
