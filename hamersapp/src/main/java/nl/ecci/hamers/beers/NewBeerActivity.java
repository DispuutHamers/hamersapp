package nl.ecci.hamers.beers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.HamersActivity;

public class NewBeerActivity extends HamersActivity {

    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beer_new_activity);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
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

        JSONObject body = new JSONObject();
        try {
            body.put("name", title);
            body.put("picture", picture);
            body.put("percentage", percentage);
            body.put("country", country);
            body.put("brewer", brewer);
            body.put("soort", soort);
        } catch (JSONException ignored) {
        }

        DataManager.postOrPatchData(this, prefs, DataManager.BEERURL, -1, DataManager.BEERKEY, body);
    }
}