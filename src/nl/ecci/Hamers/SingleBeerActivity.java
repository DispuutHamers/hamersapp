package nl.ecci.Hamers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

public class SingleBeerActivity extends ActionBarActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_beer_item);

        TextView soort = (TextView) findViewById(R.id.beer_soort);
        TextView alc = (TextView) findViewById(R.id.beer_alc);
        TextView brewer = (TextView) findViewById(R.id.beer_brewer);
        TextView country = (TextView) findViewById(R.id.beer_country);

        Bundle extras = getIntent().getExtras();
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
