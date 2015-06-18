package nl.ecci.Hamers.Events;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.Helpers.SendPostRequest;
import nl.ecci.Hamers.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SingleEventActivity extends AppCompatActivity {

    private int id;
    private LinearLayout parentLayout;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_event);

        parentLayout = (LinearLayout) findViewById(R.id.single_event_parent);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        ScrollView scrollView = (ScrollView) findViewById(R.id.single_event_scrollview);
        LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        Button aanwezigButton = (Button) findViewById(R.id.aanwezig_button);
        Button afwezigButton = (Button) findViewById(R.id.afwezig_button);

        TextView titleTV = (TextView) findViewById(R.id.event_title);
        TextView beschrijvingTV = (TextView) findViewById(R.id.event_beschrijving);
        TextView dateTV = (TextView) findViewById(R.id.event_date);
        TextView locationTV = (TextView) findViewById(R.id.event_location);
        ListView aanwezig_list = (ListView) findViewById(R.id.event_aanwezig);
        ListView afwezig_list = (ListView) findViewById(R.id.event_afwezig);
        LinearLayout aanwezig_layout = (LinearLayout) findViewById(R.id.aanwezig_layout);

        Bundle extras = getIntent().getExtras();
        id = extras.getInt("id");
        ArrayList<String> aanwezigItems = getIntent().getStringArrayListExtra("aanwezig");
        ArrayList<String> afwezigItems = getIntent().getStringArrayListExtra("afwezig");

        ArrayAdapter<String> aanwezigAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, aanwezigItems);
        ArrayAdapter<String> afwezigAadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, afwezigItems);
        aanwezig_list.setAdapter(aanwezigAdapter);
        afwezig_list.setAdapter(afwezigAadapter);

        // Remove headers if no user is enrolled for that category
        if (aanwezigItems.size() == 0) {
            TextView event_aanwezig_tv = (TextView) findViewById(R.id.event_aanwezig_tv);
            event_aanwezig_tv.setVisibility(View.INVISIBLE);
        }
        if (afwezigItems.size() == 0) {
            TextView event_afwezig_tv = (TextView) findViewById(R.id.event_afwezig_tv);
            event_afwezig_tv.setVisibility(View.INVISIBLE);
        }

        String title = extras.getString("title");
        String beschrijving = extras.getString("beschrijving");
        String location = extras.getString("location");

        String appDatum = null;
        try {
            // Current date
            Date today = new Date();

            // Event date
            String date = extras.getString("date");
            DateFormat dbDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            DateFormat appDF = new SimpleDateFormat("EEE dd MMM yyyy HH:mm");
            Date dbDatum = dbDF.parse(date);
            appDatum = appDF.format(dbDatum);

            if (today.after(dbDatum)) {
                aanwezig_layout.setVisibility(View.GONE);
                scrollView.removeView(buttonLayout);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        titleTV.setText(title);
        beschrijvingTV.setText(beschrijving);
        dateTV.setText(appDatum);

        if (!location.equals("null") && !location.equals("")) {
            locationTV.setText(location);
        } else {
            locationTV.setVisibility(View.GONE);
        }

        String userName = DataManager.getUserName(prefs);
        for (int i = 0; i < aanwezigItems.size(); i++) {
            if (aanwezigItems.get(i).equals(userName)) {
                aanwezigButton.setVisibility(View.GONE);
            }
        }
        for (int i = 0; i < afwezigItems.size(); i++) {
            if (afwezigItems.get(i).equals(userName)) {
                afwezigButton.setVisibility(View.GONE);
            }
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

    public void setAanwezig(View view) {
        postSignup(id, "true");
    }

    public void setAfwezig(View view) {
        postSignup(id, "false");
    }

    private void postSignup(int eventid, String status) {
        SendPostRequest req = new SendPostRequest(this, null, EventFragment.parentLayout, SendPostRequest.SIGNUPURL, prefs, "signup[event_id]=" + eventid + "&signup[status]=" + status);
        req.execute();

        this.finish();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
