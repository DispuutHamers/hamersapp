package nl.ecci.hamers.events;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DatePickerFragment;
import nl.ecci.hamers.helpers.HamersActivity;
import nl.ecci.hamers.helpers.TimePickerFragment;
import nl.ecci.hamers.loader.Loader;
import nl.ecci.hamers.loader.PostCallback;

import static nl.ecci.hamers.helpers.Utils.getEvent;

public class NewEventActivity extends HamersActivity {

    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private EditText event_title;
    private EditText event_location;
    private EditText event_description;
    private Button eventTimeButton;
    private Button eventEndTimeButton;
    private Button eventDateButton;
    private Button eventEndDateButton;
    private Button deadlineTimeButton;
    private Button deadlineDateButton;
    private int eventID;
    private MenuItem refreshItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_new_activity);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        event_title = (EditText) findViewById(R.id.new_event_title);
        event_location = (EditText) findViewById(R.id.event_location);
        event_description = (EditText) findViewById(R.id.event_beschrijving);
        eventTimeButton = (Button) findViewById(R.id.event_time_button);
        eventEndTimeButton = (Button) findViewById(R.id.end_time_button);
        eventDateButton = (Button) findViewById(R.id.event_date_button);
        eventEndDateButton = (Button) findViewById(R.id.end_date_button);
        deadlineTimeButton = (Button) findViewById(R.id.deadline_time_button);
        deadlineDateButton = (Button) findViewById(R.id.deadline_date_button);

        DateFormat timeFormat = new SimpleDateFormat("HH:mm", MainActivity.locale);
        DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy", MainActivity.locale);

        eventID = getIntent().getIntExtra(Event.EVENT, -1);
        if (eventID != -1) {
            Event event = getEvent(MainActivity.prefs, eventID);
            event_title.setText(event.getTitle());
            event_location.setText(event.getLocation());
            event_description.setText(event.getDescription());
            eventTimeButton.setText(timeFormat.format(event.getDate()));
            eventEndTimeButton.setText(timeFormat.format(event.getEndDate()));
            eventDateButton.setText(dateFormat.format(event.getDate()));
            eventEndDateButton.setText(dateFormat.format(event.getEndDate()));
            deadlineTimeButton.setText(timeFormat.format(event.getDeadline()));
            deadlineDateButton.setText(dateFormat.format(event.getDeadline()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_button:
                refreshItem = item;

                /* Attach a rotating ImageView to the refresh item as an ActionView */
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);

                Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_refresh);
                rotation.setRepeatCount(Animation.INFINITE);
                iv.startAnimation(rotation);

                refreshItem.setActionView(iv);

                postEvent();
                return true;
            default:
                return false;
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment picker = new DatePickerFragment();
        picker.show(fragmentManager, "date");
    }

    public void showEndDatePickerDialog(View v) {
        DialogFragment picker = new DatePickerFragment();
        picker.show(fragmentManager, "end_date");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment picker = new TimePickerFragment();
        picker.show(fragmentManager, "time");
    }

    public void showEndTimePickerDialog(View v) {
        DialogFragment picker = new TimePickerFragment();
        picker.show(fragmentManager, "end_time");
    }

    public void showDeadlineTimePickerDialog(View v) {
        DialogFragment picker = new TimePickerFragment();
        picker.show(fragmentManager, "deadline_time");
    }

    public void showDeadlineDatePickerDialog(View v) {
        DialogFragment picker = new DatePickerFragment();
        picker.show(fragmentManager, "deadline_date");
    }

    /**
     * Posts event
     */
    public void postEvent() {
        String title = event_title.getText().toString();
        String location = event_location.getText().toString();
        String description = event_description.getText().toString();
        String eventTime = eventTimeButton.getText().toString();
        String eventEndTime = eventEndTimeButton.getText().toString();
        String eventDate = eventDateButton.getText().toString();
        String eventEndDate = eventEndDateButton.getText().toString();
        String deadlineTime = deadlineTimeButton.getText().toString();
        String deadlineDate = deadlineDateButton.getText().toString();

        if (!eventDate.contains("Datum") &&
                !title.equals("") &&
                !description.equals("") &&
                !eventTime.contains("Tijd") &&
                !eventEndDate.contains("Datum") &&
                !eventEndTime.contains("Tijd") &&
                !deadlineDate.contains("Datum") &&
                !deadlineTime.contains("Tijd")) {

            JSONObject body = new JSONObject();
            try {
                body.put("title", title);
                body.put("beschrijving", description);
                body.put("location", location);
                body.put("end_time", MainActivity.parseDate(eventEndDate + " " + eventEndTime));
                body.put("deadline", MainActivity.parseDate(deadlineDate + " " + deadlineTime));
                body.put("date", MainActivity.parseDate(eventDate + " " + eventTime));
            } catch (JSONException ignored) {
            }

            Loader.postOrPatchData(Loader.EVENTURL, body, eventID, this, MainActivity.prefs, new PostCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    finish();
                }

                @Override
                public void onError(VolleyError error) {

                }
            });
        } else {
            if (refreshItem != null && refreshItem.getActionView() != null) {
                refreshItem.getActionView().clearAnimation();
                refreshItem.setActionView(null);
            }
            Toast.makeText(this, R.string.missing_fields, Toast.LENGTH_SHORT).show();
        }
    }
}
