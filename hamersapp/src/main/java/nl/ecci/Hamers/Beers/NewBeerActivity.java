package nl.ecci.Hamers.Beers;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import nl.ecci.Hamers.Helpers.SendPostRequest;
import nl.ecci.Hamers.R;

public class NewBeerActivity extends AppCompatActivity {
    private RelativeLayout parentLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_beer_activity);

        parentLayout = (RelativeLayout) findViewById(R.id.new_beer_activity_parent);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void postBeer(View view) {
        EditText beer_title = (EditText) findViewById(R.id.beer_title_et);
        EditText beer_picture = (EditText) findViewById(R.id.beer_picture_et);
        EditText beer_soort = (EditText) findViewById(R.id.beer_soort_et);
        EditText beer_percentage = (EditText) findViewById(R.id.beer_percentage_et);
        EditText beer_brewer = (EditText) findViewById(R.id.beer_brewer_et);
        EditText beer_country = (EditText) findViewById(R.id.beer_country_et);

        String title = beer_title.getText().toString();
        String picture = beer_picture.getText().toString();
        String soort = beer_soort.getText().toString();
        String percentage = beer_percentage.getText().toString();
        String brewer = beer_brewer.getText().toString();
        String country = beer_country.getText().toString();

        if (!percentage.contains("%")) {
            percentage = percentage + "%";
        }

        // Send request
        SendPostRequest req = new SendPostRequest(this, null, BeerFragment.parentLayout, SendPostRequest.BEERURL, PreferenceManager.getDefaultSharedPreferences(this), "beer[name]=" + title + "&beer[picture]=" + picture + "&beer[percentage]=" + percentage + "25" + "&beer[country]=" + country + "&beer[brewer]=" + brewer + "&beer[soort]=" + soort);
        req.execute();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}