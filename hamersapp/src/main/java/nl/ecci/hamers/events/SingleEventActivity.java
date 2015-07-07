package nl.ecci.hamers.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.helpers.SendPostRequest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SingleEventActivity extends AppCompatActivity {

    private int id;
    private LinearLayout parentLayout;
    private SharedPreferences prefs;
    private ViewGroup aanwezigLayout;
    private ViewGroup afwezigLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_event);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentLayout = (LinearLayout) findViewById(R.id.single_event_parent);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        initToolbar();

        TextView titleTV = (TextView) findViewById(R.id.event_title);
        View dateRow = findViewById(R.id.row_date);
        View locationRow = findViewById(R.id.row_location);
        View descriptionRow = findViewById(R.id.row_description);
        LinearLayout button_layout = (LinearLayout) findViewById(R.id.button_layout);
        ViewGroup aanwezigView = (ViewGroup) findViewById(R.id.aanwezig_insert_point);
        ViewGroup afwezigView = (ViewGroup) findViewById(R.id.afwezig_insert_point);
        LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        Button aanwezigButton = (Button) findViewById(R.id.aanwezig_button);
        Button afwezigButton = (Button) findViewById(R.id.afwezig_button);

        ViewGroup aanwezigLayout = (ViewGroup) findViewById(R.id.aanwezig_layout);
        ViewGroup afwezigLayout = (ViewGroup) findViewById(R.id.afwezig_layout);

        Bundle extras = getIntent().getExtras();
        id = extras.getInt("id");
        ArrayList<String> aanwezigItems = getIntent().getStringArrayListExtra("aanwezig");
        ArrayList<String> afwezigItems = getIntent().getStringArrayListExtra("afwezig");

        if (aanwezigItems.size() != 0) {
            for (String u : aanwezigItems) {
                View view = inflater.inflate(R.layout.row_singleview, null);

                fillSingleRow(view, u);

                aanwezigView.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } else {
            aanwezigLayout.removeAllViews();
        }

        if (afwezigItems.size() != 0) {
            for (String u : afwezigItems) {
                View view = inflater.inflate(R.layout.row_singleview, null);

                fillSingleRow(view, u);

                afwezigView.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } else {
            afwezigLayout.removeAllViews();
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
            DateFormat dbDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", new Locale("nl"));
            DateFormat appDF = new SimpleDateFormat("EEE dd MMM yyyy HH:mm", new Locale("nl"));
            Date dbDatum = dbDF.parse(date);
            appDatum = appDF.format(dbDatum);

            if (today.after(dbDatum)) {
                button_layout.setVisibility(View.GONE);
                ScrollView scrollView = (ScrollView) findViewById(R.id.single_event_scrollview);
                scrollView.removeView(buttonLayout);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Resources res = getResources();
        titleTV.setText(title);
        fillDetailRow(descriptionRow, "Beschrijving", beschrijving);
        fillImageRow(dateRow, "Datum", appDatum, res.getDrawable(R.drawable.ic_event));

        if (!location.equals("null") && !location.equals("")) {
            fillImageRow(locationRow, "Locatie", location, res.getDrawable(R.drawable.ic_location));
        } else {
            locationRow.setVisibility(View.GONE);
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

    public void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    public void fillSingleRow(View view, final String title) {
        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(title);
    }

    public void fillDetailRow(View view, final String title, final String description) {
        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(title);

        TextView descriptionView = (TextView) view.findViewById(R.id.description);
        descriptionView.setText(description);
    }

    public void fillImageRow(View view, final String title, final String description, final Drawable image) {
        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(title);

        TextView descriptionView = (TextView) view.findViewById(R.id.description);
        descriptionView.setText(description);

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        iconView.setImageDrawable(image);
    }
}
