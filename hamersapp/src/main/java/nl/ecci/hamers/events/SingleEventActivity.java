package nl.ecci.hamers.events;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.HamersActivity;
import nl.ecci.hamers.loader.Loader;
import nl.ecci.hamers.loader.VolleyCallback;
import nl.ecci.hamers.users.User;

import static nl.ecci.hamers.helpers.Utils.getEvent;
import static nl.ecci.hamers.helpers.Utils.getOwnUser;
import static nl.ecci.hamers.helpers.Utils.getUser;

public class SingleEventActivity extends HamersActivity {

    private Event event;
    private LayoutInflater inflater;
    private LinearLayout presentView;
    private LinearLayout absentView;
    private ViewGroup eventLayout;
    private ViewGroup presentLayout;
    private ViewGroup absentLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_event);

        inflater = getLayoutInflater();

        initToolbar();

        TextView titleTV = (TextView) findViewById(R.id.event_title);
        View dateRow = findViewById(R.id.date_row);
        View locationRow = findViewById(R.id.location_row);
        View descriptionRow = findViewById(R.id.description_row);
        LinearLayout button_layout = (LinearLayout) findViewById(R.id.button_layout);
        presentView = (LinearLayout) findViewById(R.id.present_insert_point);
        absentView = (LinearLayout) findViewById(R.id.absent_insert_point);
        LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);

        eventLayout = (ViewGroup) findViewById(R.id.single_event_layout);
        presentLayout = (ViewGroup) findViewById(R.id.present_layout);
        absentLayout = (ViewGroup) findViewById(R.id.absent_layout);

        event = getEvent(MainActivity.prefs, getIntent().getIntExtra(Event.EVENT, 1));

        initSignups();

        titleTV.setText(event.getTitle());
        fillDetailRow(descriptionRow, event.getDescription());

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
            if (event.getLocation() != null) {
                fillImageRow(locationRow, "Locatie", event.getLocation(), ContextCompat.getDrawable(this, R.drawable.location));

                locationRow.setClickable(true);
                locationRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Create a Uri from an intent string. Use the result to create an Intent.
                        Uri uri = Uri.parse("geo:0,0?q=" + event.getLocation());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_item:
                Intent intent = new Intent(this, NewEventActivity.class);
                if (event != null) {
                    intent.putExtra(Event.EVENT, event.getID());
                }
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setPresent(View view) {
        postSignup(event.getID(), "true");
    }

    public void setAbsent(View view) {
        postSignup(event.getID(), "false");
    }

    private void postSignup(int eventID, String status) {
        JSONObject body = new JSONObject();
        try {
            body.put("event_id", eventID);
            body.put("status", status);
        } catch (JSONException ignored) {
        }

        Loader.postOrPatchData(new VolleyCallback() {
            @Override
            public void onSuccess(JSONArray response) {

            }

            @Override
            public void onError(VolleyError error) {

            }
        }, this, MainActivity.prefs, Loader.SIGNUPURL, -1, body);
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
                present.add(getUser(MainActivity.prefs, signup.getUserID()).getName());
            } else {
                absent.add(getUser(MainActivity.prefs, signup.getUserID()).getName());
            }
        }

        if (present.size() != 0) {
            for (String name : present) {
                presentView.addView(newSingleRow(name, presentView), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } else {
            eventLayout.removeView(presentLayout);
        }

        if (absent.size() != 0) {
            for (String name : absent) {
                absentView.addView(newSingleRow(name, absentView), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        } else {
            eventLayout.removeView(absentLayout);
        }

        User ownUser = getOwnUser(MainActivity.prefs);
        if (present.contains(ownUser.getName()) && presentButton != null) {
            presentButton.setVisibility(View.GONE);
        } else if (absent.contains(ownUser.getName()) && absentButton != null) {
            absentButton.setVisibility(View.GONE);
        }
    }

    private View newSingleRow(final String title, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.row_singleview, viewGroup, false);

        TextView titleView = (TextView) view.findViewById(R.id.row_title);
        titleView.setText(title);

        return view;
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
