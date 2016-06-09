package nl.ecci.hamers.events;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.users.User;

public class SingleEventActivity extends AppCompatActivity {
    private Event event;
    private LayoutInflater inflater;
    private ViewGroup presentView;
    private ViewGroup absentView;
    private ViewGroup eventLayout;
    private ViewGroup presentLayout;
    private ViewGroup absentLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.single_event);

        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        event = DataManager.getEvent(MainActivity.prefs, getIntent().getExtras().getInt("id"));

        initToolbar();

        TextView titleTV = (TextView) findViewById(R.id.event_title);
        View dateRow = findViewById(R.id.date_row);
        View locationRow = findViewById(R.id.location_row);
        View descriptionRow = findViewById(R.id.description_row);
        LinearLayout button_layout = (LinearLayout) findViewById(R.id.button_layout);
        presentView = (ViewGroup) findViewById(R.id.present_insert_point);
        absentView = (ViewGroup) findViewById(R.id.absent_insert_point);
        LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);

        eventLayout = (ViewGroup) findViewById(R.id.single_event_layout);
        presentLayout = (ViewGroup) findViewById(R.id.present_layout);
        absentLayout = (ViewGroup) findViewById(R.id.absent_layout);

        final String title = event.getTitle();
        final String description = event.getDescription();
        final String location = event.getLocation();

        initSignups();

        titleTV.setText(title);
        fillDetailRow(descriptionRow, description);

        // Current date
        Date today = new Date();
        // Event deadline
        Date deadline = event.getDeadline();

        if (deadline != null) {
            if (today.after(deadline)) {
                button_layout.setVisibility(View.GONE);
                ScrollView scrollView = (ScrollView) findViewById(R.id.single_event_scrollview);
                scrollView.removeView(buttonLayout);
            }
        }

        if (dateRow != null) {
            fillImageRow(dateRow, "Datum", MainActivity.appDF.format(event.getDate()), ContextCompat.getDrawable(this, R.drawable.ic_event));
            dateRow.setClickable(true);
            dateRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long startMillis = event.getDate().getTime();
                    Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                    builder.appendPath("time");
                    ContentUris.appendId(builder, startMillis);
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
                    startActivity(intent);
                }
            });
        }

        if (locationRow != null) {
            if (location != null) {
                fillImageRow(locationRow, "Locatie", location, ContextCompat.getDrawable(this, R.drawable.location));

                locationRow.setClickable(true);
                locationRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Create a Uri from an intent string. Use the result to create an Intent.
                        Uri uri = Uri.parse("geo:0,0?q=" + location);
                        // Create an Intent from uri. Set the action to ACTION_VIEW
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setPackage("com.google.android.apps.maps");
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
            } else {
                locationRow.setVisibility(View.GONE);
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

    public void setPresent(View view) {
        postSignup(event.getId(), "true");
    }

    public void setAbsent(View view) {
        postSignup(event.getId(), "false");
    }

    private void postSignup(int eventid, String status) {
        JSONObject body = new JSONObject();
        try {
            body.put("event_id", eventid);
            body.put("status", status);
        } catch (JSONException ignored) {
        }

        DataManager.postData(this, MainActivity.prefs, DataManager.SIGNUPURL, null, body);
        this.finish();
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

    private void initSignups() {
        Button presentButton = (Button) findViewById(R.id.present_button);
        Button absentButton = (Button) findViewById(R.id.absent_button);
        ArrayList<Event.Signup> signups = event.getSignups();
        ArrayList<String> present = new ArrayList<>();
        ArrayList<String> absent = new ArrayList<>();
        for (int i = 0; i < signups.size(); i++) {
            Event.Signup signup = signups.get(i);
            if (signup.isAttending()) {
                present.add(DataManager.getUser(MainActivity.prefs, signup.getUserID()).getName());
            } else {
                absent.add(DataManager.getUser(MainActivity.prefs, signup.getUserID()).getName());
            }
        }

        if (present.size() != 0) {
            for (String name : present) {
                View view = inflater.inflate(R.layout.row_singleview, null);
                fillSingleRow(view, name);
                presentView.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } else {
            eventLayout.removeView(presentLayout);
        }

        if (absent.size() != 0) {
            for (String name : absent) {
                View view = inflater.inflate(R.layout.row_singleview, null);
                fillSingleRow(view, name);
                absentView.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } else {
            eventLayout.removeView(absentLayout);
        }

        User ownUser = DataManager.getOwnUser(MainActivity.prefs);
        if (present.contains(ownUser.getName()) && presentButton != null) {
            presentButton.setVisibility(View.GONE);
        } else if (absent.contains(ownUser.getName()) && absentButton != null) {
            absentButton.setVisibility(View.GONE);
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
