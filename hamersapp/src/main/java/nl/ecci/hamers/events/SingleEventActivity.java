package nl.ecci.hamers.events;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;

public class SingleEventActivity extends AppCompatActivity {

    private final DateFormat dbDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", MainActivity.locale);
    private final DateFormat appDF = new SimpleDateFormat("EEE dd MMM yyyy HH:mm", MainActivity.locale);
    private int id;
    private Date dbDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_event);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        initToolbar();

        TextView titleTV = (TextView) findViewById(R.id.event_title);
        View dateRow = findViewById(R.id.date_row);
        View locationRow = findViewById(R.id.location_row);
        View descriptionRow = findViewById(R.id.description_row);
        LinearLayout button_layout = (LinearLayout) findViewById(R.id.button_layout);
        ViewGroup aanwezigView = (ViewGroup) findViewById(R.id.aanwezig_insert_point);
        ViewGroup afwezigView = (ViewGroup) findViewById(R.id.afwezig_insert_point);
        LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        Button aanwezigButton = (Button) findViewById(R.id.aanwezig_button);
        Button afwezigButton = (Button) findViewById(R.id.afwezig_button);

        ViewGroup eventLayout = (ViewGroup) findViewById(R.id.single_event_layout);
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
            eventLayout.removeView(aanwezigLayout);
        }

        if (afwezigItems.size() != 0) {
            for (String u : afwezigItems) {
                View view = inflater.inflate(R.layout.row_singleview, null);

                fillSingleRow(view, u);

                afwezigView.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } else {
            eventLayout.removeView(afwezigLayout);
        }

        String title = extras.getString("title");
        String beschrijving = extras.getString("beschrijving");
        final String location = extras.getString("location");

        String deadLine;
        String date = null;
        try {
            // Current date
            Date today = new Date();
            // Event deadline
            deadLine = extras.getString("deadline");
            Date dbDeadlineDate = dbDF.parse(deadLine);

            // Event date
            date = extras.getString("date");
            dbDate = dbDF.parse(date);
            date = appDF.format(dbDate);

            if (today.after(dbDeadlineDate)) {
                button_layout.setVisibility(View.GONE);
                ScrollView scrollView = (ScrollView) findViewById(R.id.single_event_scrollview);
                scrollView.removeView(buttonLayout);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        titleTV.setText(title);
        fillImageRow(dateRow, "Datum", date, ContextCompat.getDrawable(this, R.drawable.ic_event));
        fillDetailRow(descriptionRow, beschrijving);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            dateRow.setClickable(true);
            dateRow.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(View v) {
                    long startMillis = dbDate.getTime();
                    Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                    builder.appendPath("time");
                    ContentUris.appendId(builder, startMillis);
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
                    startActivity(intent);
                }
            });
        }

        if (!location.equals("null") && !location.equals("")) {
            fillImageRow(locationRow, "Locatie", location, ContextCompat.getDrawable(this, R.drawable.location));
            locationRow.setClickable(true);

            locationRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create a Uri from an intent string. Use the result to create an Intent.
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + location);
                    // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    // Make the Intent explicit by setting the Google Maps package
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        // Attempt to start an activity that can handle the Intent
                        startActivity(mapIntent);
                    }
                }
            });
        } else {
            locationRow.setVisibility(View.GONE);
        }

        String userName = DataManager.getUserName(MainActivity.prefs);
        for (String aanwezigItem : aanwezigItems) {
            if (aanwezigItem.equals(userName)) {
                aanwezigButton.setVisibility(View.GONE);
            }
        }
        for (String afwezigItem : afwezigItems) {
            if (afwezigItem.equals(userName)) {
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
        Map<String, String> params = new HashMap<>();
        params.put("signup[event_id]", Integer.toString(eventid));
        params.put("signup[status]", status);

        DataManager.postData(this, MainActivity.prefs, DataManager.SIGNUPURL, null, params);
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

    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void fillSingleRow(View view, final String title) {
        TextView titleView = (TextView) view.findViewById(R.id.row_title);
        titleView.setText(title);
    }

    private void fillDetailRow(View view, final String description) {
        TextView titleView = (TextView) view.findViewById(R.id.row_title);
        titleView.setText(R.string.description);

        TextView descriptionView = (TextView) view.findViewById(R.id.row_description);
        descriptionView.setText(description);
    }

    private void fillImageRow(View view, final String title, final String description, final Drawable image) {
        TextView titleView = (TextView) view.findViewById(R.id.row_title);
        titleView.setText(title);

        TextView descriptionView = (TextView) view.findViewById(R.id.row_description);
        descriptionView.setText(description);

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        iconView.setImageDrawable(image);
    }
}
