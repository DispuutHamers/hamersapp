package nl.ecci.Hamers.Beers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.R;

public class SingleBeerActivity extends ActionBarActivity {
    String name;
    String soort;
    String percentage;
    String brewer;
    String country;
    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_beer_item);

        getSupportActionBar().setHomeButtonEnabled(true);

        TextView nameTV = (TextView) findViewById(R.id.beer_name);
        TextView soortTV = (TextView) findViewById(R.id.beer_soort);
        TextView percentageTV = (TextView) findViewById(R.id.beer_alc);
        TextView brewerTV = (TextView) findViewById(R.id.beer_brewer);
        TextView countryTV = (TextView) findViewById(R.id.beer_country);
        ImageView beerImage = (ImageView) findViewById(R.id.beer_image);

        Bundle extras = getIntent().getExtras();

        name = extras.getString("name");
        soort = extras.getString("soort");
        percentage = extras.getString("percentage");
        brewer = extras.getString("brewer");
        country = extras.getString("country");

        prefs =  PreferenceManager.getDefaultSharedPreferences(this);
        beerImage.setImageBitmap(DataManager.getBeerImage(prefs, name));

        nameTV.setText(name);
        soortTV.setText(soort);
        percentageTV.setText(percentage);
        brewerTV.setText(brewer);
        countryTV.setText(country);
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

    /**
     * Called when the user clicks the button to create a new beerreview,
     * starts NewBeerActivity.
     * @param view
     */
    public void createReview(View view) {
        Intent intent = new Intent(this, NewBeerReviewActivity.class);
        startActivity(intent);
    }
}
