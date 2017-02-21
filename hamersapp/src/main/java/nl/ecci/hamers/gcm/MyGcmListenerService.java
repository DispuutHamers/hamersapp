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
import nl.ecci.hamers.beers.SingleBeerActivity;
import nl.ecci.hamers.events.Event;
import nl.ecci.hamers.events.SingleEventActivity;
import nl.ecci.hamers.helpers.Utils;
import nl.ecci.hamers.loader.Loader;
import nl.ecci.hamers.users.User;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    private final String QUOTEDATE = "created_at";
    private JSONObject event;
    private JSONObject beer;
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

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
        JSONArray json;
        String USERID = "user_id";
        try {
            String QUOTETYPE = "quote";
            JSONObject quote = new JSONObject(object.getString(QUOTETYPE));
            if (quote.length() != 0) {
                type = Type.QUOTE;
                String QUOTEBODY = "text";
                title = quote.getString(QUOTEBODY);

                User user = Utils.INSTANCE.getUser(prefs, Integer.valueOf(quote.getString(USERID)));
                if (user != null) {
                    message = user.getName();
                } else {
                    message = "- user";
                }

                // Add quote to quote list
                json = new JSONArray(prefs.getString(Loader.QUOTEURL, null));
                json.put(quote);
                prefs.edit().putString(Loader.QUOTEURL, json.toString()).apply();
            }
        } catch (JSONException | NullPointerException ignored) {
        }

        // EVENT
        try {
            String EVENTTYPE = "event";
            event = new JSONObject(object.getString(EVENTTYPE));
            if (event.length() != 0) {
                type = Type.EVENT;
                String EVENTTITLE = "title";
                title = event.getString(EVENTTITLE);
                String EVENTDESCRIPTION = "beschrijving";
                message = event.getString(EVENTDESCRIPTION);

                // Add event to event list
                json = new JSONArray(prefs.getString(Loader.EVENTURL, null));
                json.put(event);
                prefs.edit().putString(Loader.EVENTURL, json.toString()).apply();

                intent = new Intent(this, SingleEventActivity.class);
                intent.putExtra(Event.EVENT, event.getInt("id"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            }
        } catch (JSONException | NullPointerException ignored) {
        }

        // BEER
        try {
            String BEERTYPE = "beer";
            beer = new JSONObject(object.getString(BEERTYPE));
            if (beer.length() != 0) {
                type = Type.BEER;
                String BEERNAME = "name";
                title = "Biertje: " + beer.getString(BEERNAME);
                message = "Is net toegevoegd aan de database!";

                // Add beer to beer list
                json = new JSONArray(prefs.getString(Loader.BEERURL, null));
                json.put(beer);
                prefs.edit().putString(Loader.BEERURL, json.toString()).apply();

                intent = new Intent(this, SingleBeerActivity.class);
                intent.putExtra(Beer.BEER, beer.getInt("id"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            }
        } catch (JSONException | NullPointerException ignored) {
        }

        // REVIEW
        try {
            String REVIEWTYPE = "review";
            JSONObject review = new JSONObject(object.getString(REVIEWTYPE));
            if (review.length() != 0) {
                type = Type.REVIEW;
                User user = Utils.INSTANCE.getUser(prefs, review.getInt(USERID));
                String REVIEWBEER = "beer_id";
                Beer beer = Utils.INSTANCE.getBeer(prefs, review.getInt(REVIEWBEER));
                String REVIEWRATING = "rating";
                if (user != null && beer != null) {
                    title = user.getName() + " / " + beer.getName() + " / " + review.getString(REVIEWRATING);
                } else if (user != null) {
                    title = user.getName() + " / onbekend / " + review.getString(REVIEWRATING);
                } else {
                    title = "Hamers";
                }
                String REVIEWDESCRIPTION = "description";
                message = review.getString(REVIEWDESCRIPTION);

                // Add review to review list
                json = new JSONArray(prefs.getString(Loader.REVIEWURL, null));
                json.put(review);
                prefs.edit().putString(Loader.REVIEWURL, json.toString()).apply();

                intent = new Intent(this, SingleBeerActivity.class);
                intent.putExtra(Beer.BEER, review.getInt("beer_id"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            }
        } catch (JSONException | NullPointerException ignored) {
        }

        if (title != null && message != null) {
            sendNotification(title, message, type, pendingIntent);
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String title, String message, Type type, PendingIntent pendingIntent) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.launcher_icon);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.launcher_icon)
                .setLargeIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        notificationBuilder.setStyle(bigTextStyle);
        if (pendingIntent != null) {
            notificationBuilder.setContentIntent(pendingIntent);
        }

        switch (type) {
            case QUOTE:
                bigTextStyle.setBigContentTitle(message);
                bigTextStyle.bigText(title);
                break;

            case EVENT:
                bigTextStyle.setBigContentTitle(title);
                try {
                    String EVENTLOCATION = "location";
                    bigTextStyle.bigText("Locatie: " + event.getString(EVENTLOCATION) + "\n" + message);
                } catch (JSONException ignored) {
                }
                break;

            case BEER:
                bigTextStyle.setBigContentTitle(title);

                try {
                    String BEERCOUNTRY = "country";
                    String BEERBREWER = "brewer";
                    String BEERPERCENTAGE = "percentage";
                    String BEERKIND = "soort";
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
