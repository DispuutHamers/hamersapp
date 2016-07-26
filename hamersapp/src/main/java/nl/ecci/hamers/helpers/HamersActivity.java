package nl.ecci.hamers.helpers;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class HamersActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }
}
