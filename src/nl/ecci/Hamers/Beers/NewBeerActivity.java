package nl.ecci.Hamers.Beers;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import nl.ecci.Hamers.Helpers.SendPostRequest;
import nl.ecci.Hamers.R;

public class NewBeerActivity extends ActionBarActivity {
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

    private void postBeer(String name, String picture, String percentage, String country, String brewer, String soort) {
        SendPostRequest req = new SendPostRequest(this, SendPostRequest.BEERURL, PreferenceManager.getDefaultSharedPreferences(this), "beer[name]=" + name + "&beer[picture]=" + picture + "&beer[percentage]=" + percentage + "&beer[country]=" + country + "&beer[brewer]=" + brewer + "&beer[soort]=" + soort);
        req.execute();
    }
}