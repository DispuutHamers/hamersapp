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

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.HamersActivity;

public class NewBeerActivity extends HamersActivity {

    private Beer beer;
    private SharedPreferences prefs;
    private EditText beer_name;
    private EditText beer_picture;
    private EditText beer_soort;
    private EditText beer_percentage;
    private EditText beer_brewer;
    private EditText beer_country;

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

        beer_name = (EditText) findViewById(R.id.beer_name);
        beer_picture = (EditText) findViewById(R.id.beer_picture);
        beer_soort = (EditText) findViewById(R.id.beer_soort);
        beer_percentage = (EditText) findViewById(R.id.beer_percentage);
        beer_brewer = (EditText) findViewById(R.id.beer_brewer);
        beer_country = (EditText) findViewById(R.id.beer_country);

        int beerID = getIntent().getIntExtra(Beer.BEER, -1);
        if (beerID != -1) {
            beer = DataManager.getBeer(MainActivity.prefs, beerID);
            beer_name.setText(beer.getName());
            beer_picture.setText(beer.getImageURL());
            beer_soort.setText(beer.getKind());
            beer_percentage.setText(beer.getPercentage());
            beer_brewer.setText(beer.getBrewer());
            beer_country.setText(beer.getCountry());
        }
    }

    public void postBeer(View view) {
        String percentage = beer_percentage.getText().toString();

        if (!percentage.contains("%")) {
            percentage = percentage + "%";
        }

        JSONObject body = new JSONObject();
        try {
            body.put("name", beer_name.getText().toString());
            body.put("picture", beer_picture.getText().toString());
            body.put("percentage", percentage);
            body.put("country", beer_country.getText().toString());
            body.put("brewer", beer_brewer.getText().toString());
            body.put("soort", beer_soort.getText().toString());
        } catch (JSONException ignored) {
        }

        DataManager.postOrPatchData(this, prefs, DataManager.BEERURL, -1, DataManager.BEERKEY, body);
    }
}