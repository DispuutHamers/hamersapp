package nl.ecci.hamers.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.beers.Beer;
import nl.ecci.hamers.helpers.DataManager;
import nl.ecci.hamers.users.User;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    // Quote
    private final String QUOTETYPE = "quote";
    private final String QUOTEBODY = "text";
    private final String QUOTEDATE = "created_at";
    // Event
    private final String EVENTTYPE = "event";
    private final String EVENTTITLE = "title";
    private final String EVENTLOCATION = "location";
    private final String EVENTDESCRIPTION = "beschrijving";
    // Beer
    private final String BEERTYPE = "beer";
    private final String BEERNAME = "name";
    private final String BEERKIND = "soort";
    private final String BEERPERCENTAGE = "percentage";
    private final String BEERBREWER = "brewer";
    private final String BEERCOUNTRY = "country";
    // Review
    private final String REVIEWTYPE = "review";
    private final String REVIEWBEER = "beer_id";
    private final String REVIEWDESCRIPTION = "description";
    private final String REVIEWRATING = "rating";
    // Common
    private final String USERID = "user_id";
    private JSONArray json;
    private SharedPreferences prefs;

    private JSONObject quote;
    private JSONObject event;
    private JSONObject beer;
    private JSONObject review;

    private Type type;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String title = null;
        String message = null;

        JSONObject object = new JSONObject();
        Set<String> keys = data.keySet();
        for (String key : keys) {
            try {
                object.put(key, data.get(key));
            } catch (JSONException ignored) {
            }
        }

        // QUOTE
        try {
            quote = new JSONObject(object.getString(QUOTETYPE));
            if (quote.length() != 0) {
                type = Type.QUOTE;
                title = quote.getString(QUOTEBODY);

                User user = DataManager.getUser(prefs, Integer.valueOf(quote.getString(USERID)));
                if (user != null) {
                    message = user.getName();
                } else {
                    message = "- user";
                }

                // Add quote to quote list
                if ((json = DataManager.getJsonArray(prefs, DataManager.QUOTEKEY)) != null) {
                    JSONArray quotes = new JSONArray();
                    quotes.put(quote);
                    for (int i = 0; i < json.length(); i++) {
                        quotes.put(json.getJSONObject(i));
                    }
                    prefs.edit().putString(DataManager.QUOTEKEY, quotes.toString()).apply();
                }
            }
        } catch (JSONException | NullPointerException ignored) {
        }

        // EVENT
        try {
            event = new JSONObject(object.getString(EVENTTYPE));
            if (event.length() != 0) {
                type = Type.EVENT;
                title = event.getString(EVENTTITLE);
                message = event.getString(EVENTDESCRIPTION);

                // Add event to event list
                if ((json = DataManager.getJsonArray(prefs, DataManager.EVENTKEY)) != null) {
                    JSONArray events = new JSONArray();
                    for (int i = 0; i < json.length(); i++) {
                        events.put(json.getJSONObject(i));
                    }
                    events.put(event);
                    prefs.edit().putString(DataManager.EVENTKEY, events.toString()).apply();
                }
            }
        } catch (JSONException | NullPointerException ignored) {
        }

        // BEER
        try {
            beer = new JSONObject(object.getString(BEERTYPE));
            if (beer.length() != 0) {
                type = Type.BEER;
                title = "Biertje: " + beer.getString(BEERNAME);
                message = "Is net toegevoegd aan de database!";

                // Add beer to beer list
                if ((json = DataManager.getJsonArray(prefs, DataManager.BEERKEY)) != null) {
                    json.put(beer);
                    prefs.edit().putString(DataManager.BEERKEY, json.toString()).apply();
                }
            }
        } catch (JSONException | NullPointerException ignored) {
        }

        // REVIEW
        try {
            review = new JSONObject(object.getString(REVIEWTYPE));
            if (review.length() != 0) {
                type = Type.REVIEW;
                User user = DataManager.getUser(prefs, review.getInt(USERID));
                Beer beer = DataManager.getBeer(prefs, review.getInt(REVIEWBEER));
                if (user != null && beer != null) {
                    title = user.getName() + " / " + beer.getName() + " / " + review.getString(REVIEWRATING);
                } else if (user != null) {
                    title = user.getName() + " / onbekend / " + review.getString(REVIEWRATING);
                } else {
                    title = "Hamers";
                }
                message = review.getString(REVIEWDESCRIPTION);

                // Add review to reviewlist
                if ((json = DataManager.getJsonArray(prefs, DataManager.REVIEWKEY)) != null) {
                    json.put(review);
                    prefs.edit().putString(DataManager.REVIEWKEY, json.toString()).apply();
                }
            }
        } catch (JSONException | NullPointerException ignored) {
        }

        // Show notification
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean push = settings.getBoolean("pushPref", true);

        if (push && title != null && message != null) {
            sendNotification(title, message, type);
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String title, String message, Type type) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.launcher_icon);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.launcher_icon)
                .setLargeIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        notificationBuilder.setStyle(bigTextStyle);

        switch (type) {
            case QUOTE:
                bigTextStyle.setBigContentTitle(message);
                bigTextStyle.bigText(title);
                break;

            case EVENT:
                bigTextStyle.setBigContentTitle(title);
                try {
                    bigTextStyle.bigText("Locatie: " + event.getString(EVENTLOCATION) + "\n" + message);
                } catch (JSONException ignored) {
                }
                break;

            case BEER:
                bigTextStyle.setBigContentTitle(title);

                try {
                    bigTextStyle.bigText("Soort: " + beer.getString(BEERKIND) + "\n" +
                            "ALC: " + beer.getString(BEERPERCENTAGE) + "\n" +
                            "Brouwer: " + beer.getString(BEERBREWER) + "\n" +
                            "Land: " + beer.getString(BEERCOUNTRY));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case REVIEW:
                String[] parts = title.split(" / ");
                String name = parts[0];
                String beer = parts[1];
                String rating = parts[2];

                bigTextStyle.setBigContentTitle(beer);
                bigTextStyle.bigText(name + ", Cijfer: " + rating + "\n" + message);
                break;
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    public enum Type {
        QUOTE, EVENT, BEER, REVIEW
    }
}
