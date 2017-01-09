package nl.ecci.hamers.beers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.NewItemActivity;
import nl.ecci.hamers.loader.Loader;
import nl.ecci.hamers.loader.PostCallback;

import static nl.ecci.hamers.helpers.Utils.getBeer;

public class NewBeerActivity extends NewItemActivity {

    private int beerID;
    private SharedPreferences prefs;
    private EditText beerName;
    private EditText beerPicture;
    private EditText beerKind;
    private EditText beerPercentage;
    private EditText beerBrewer;
    private EditText beerCountry;


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

        beerName = (EditText) findViewById(R.id.beer_name);
        beerPicture = (EditText) findViewById(R.id.beer_picture);
        beerKind = (EditText) findViewById(R.id.beer_soort);
        beerPercentage = (EditText) findViewById(R.id.beer_percentage);
        beerBrewer = (EditText) findViewById(R.id.beer_brewer);
        beerCountry = (EditText) findViewById(R.id.beer_country);

        beerID = getIntent().getIntExtra(Beer.BEER, -1);
        if (beerID != -1) {
            Beer beer = getBeer(MainActivity.prefs, beerID);
            beerName.setText(beer.getName());
            beerPicture.setText(beer.getImageURL());
            beerKind.setText(beer.getKind());
            beerPercentage.setText(beer.getPercentage());
            beerBrewer.setText(beer.getBrewer());
            beerCountry.setText(beer.getCountry());

            setTitle("Wijzig " + beer.getName());
        }
    }

    public void postItem() {
        String percentage = beerPercentage.getText().toString();

        if (!percentage.contains("%")) {
            percentage = percentage + "%";
        }

        JSONObject body = new JSONObject();
        try {
            body.put("name", beerName.getText().toString());
            body.put("picture", beerPicture.getText().toString());
            body.put("percentage", percentage);
            body.put("country", beerCountry.getText().toString());
            body.put("brewer", beerBrewer.getText().toString());
            body.put("soort", beerKind.getText().toString());
        } catch (JSONException ignored) {
        }

        Loader.postOrPatchData(this, Loader.BEERURL, body, beerID, new PostCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(SingleBeerActivity.beerName, beerName.getText().toString());
                returnIntent.putExtra(SingleBeerActivity.beerKind, beerKind.getText().toString());
                returnIntent.putExtra(SingleBeerActivity.beerPercentage, beerPercentage.getText().toString());
                returnIntent.putExtra(SingleBeerActivity.beerBrewer, beerBrewer.getText().toString());
                returnIntent.putExtra(SingleBeerActivity.beerCountry, beerCountry.getText().toString());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }

            @Override
            public void onError(VolleyError error) {
                disableLoadingAnimation();
            }
        });
    }
}