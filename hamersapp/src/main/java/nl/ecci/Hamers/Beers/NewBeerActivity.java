package nl.ecci.Hamers.Beers;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import nl.ecci.Hamers.Helpers.SendPostRequest;
import nl.ecci.Hamers.R;

public class NewBeerActivity extends ActionBarActivity {
    private String title;
    private String picture;
    private String soort;
    private String percentage;
    private String brewer;
    private String country;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_beer_acitivity);
        getSupportActionBar().setHomeButtonEnabled(true);
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

        title = beer_title.getText().toString();
        picture = beer_picture.getText().toString();
        soort = beer_soort.getText().toString();
        percentage = beer_percentage.getText().toString();
        brewer = beer_brewer.getText().toString();
        country = beer_country.getText().toString();

        if (!percentage.contains("%")) {
            percentage = percentage + "%";
        }

        // Send request
        SendPostRequest req = new SendPostRequest(this, SendPostRequest.BEERURL, PreferenceManager.getDefaultSharedPreferences(this), "beer[name]=" + title + "&beer[picture]=" + picture + "&beer[percentage]=" + percentage + "25" + "&beer[country]=" + country + "&beer[brewer]=" + brewer + "&beer[soort]=" + soort);
        req.execute();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}