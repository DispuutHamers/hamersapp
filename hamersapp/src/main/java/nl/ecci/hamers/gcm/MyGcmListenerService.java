package nl.ecci.hamers.gcm;

/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;
import nl.ecci.hamers.MainActivity;
import nl.ecci.hamers.R;
import nl.ecci.hamers.helpers.DataManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

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
    private final String USER = "user_id";
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
    // [START receive_message]
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

                String userName = null;
                userName = DataManager.UserIDtoUserName(prefs, Integer.valueOf(quote.getString(USER)));
                if (userName != null) {
                    message = userName;
                } else {
                    message = "- user";
                }

                // Add quote to quotelist
                if ((json = DataManager.getJsonArray(prefs, DataManager.QUOTEKEY)) != null) {
                    JSONArray quotes = new JSONArray();
                    quotes.put(quote);
                    for (int i = 0; i < json.length(); i++) {
                        quotes.put(json.getJSONObject(i));
                    }
                    prefs.edit().putString(DataManager.QUOTEKEY, quotes.toString()).apply();
                }
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }

        // EVENT
        try {
            event = new JSONObject(object.getString(EVENTTYPE));
            if (event.length() != 0) {
                type = Type.EVENT;
                title = event.getString(EVENTTITLE);
                message = event.getString(EVENTDESCRIPTION);

                // Add event to eventlist
                if ((json = DataManager.getJsonArray(prefs, DataManager.EVENTKEY)) != null) {
                    JSONArray events = new JSONArray();
                    for (int i = 0; i < json.length(); i++) {
                        events.put(json.getJSONObject(i));
                    }
                    events.put(event);
                    prefs.edit().putString(DataManager.EVENTKEY, events.toString()).apply();
                }
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }

        // BEER
        try {
            beer = new JSONObject(object.getString(BEERTYPE));
            if (beer.length() != 0) {
                type = Type.BEER;
                title = "Biertje: " + beer.getString(BEERNAME);
                message = "Is net toegevoegd aan de database!";

                // Add beer to beerlist
                if ((json = DataManager.getJsonArray(prefs, DataManager.BEERKEY)) != null) {
                    JSONArray beers = new JSONArray();
                    for (int i = 0; i < json.length(); i++) {
                        beers.put(json.getJSONObject(i));
                    }
                    beers.put(beer);
                    prefs.edit().putString(DataManager.BEERKEY, beers.toString()).apply();
                }
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }

        // REVIEW
        try {
            review = new JSONObject(object.getString(REVIEWTYPE));
            if (review.length() != 0) {
                type = Type.REVIEW;
                String userName;
                String beerName;
                userName = DataManager.UserIDtoUserName(prefs, review.getInt(USER));
                beerName = DataManager.BeerIDtoBeerName(prefs, review.getInt(REVIEWBEER));
                if (userName != null && beerName != null) {
                    title = userName + " / " + beerName + " / " + review.getString(REVIEWRATING);
                } else if (userName != null) {
                    title = userName + " / onbekend / " + review.getString(REVIEWRATING);
                } else {
                    title = "Hamers";
                }
                message = review.getString(REVIEWDESCRIPTION);

                // Add review to reviewlist
                if ((json = DataManager.getJsonArray(prefs, DataManager.REVIEWKEY)) != null) {
                    JSONArray reviews = new JSONArray();
                    for (int i = 0; i < json.length(); i++) {
                        reviews.put(json.getJSONObject(i));
                    }
                    reviews.put(review);
                    prefs.edit().putString(DataManager.REVIEWKEY, reviews.toString()).apply();
                }
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
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
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();

        int index = 0;
        switch (type) {
            case QUOTE:
                bigTextStyle.setBigContentTitle(message);
                bigTextStyle.bigText(title);
                break;

            case EVENT:
                bigTextStyle.setBigContentTitle(title);
                try {
                    bigTextStyle.bigText("Locatie: " + event.getString(EVENTLOCATION) + "\n" +
                            message);
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


        notificationBuilder.setStyle(bigTextStyle);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public enum Type {
        QUOTE, EVENT, BEER, REVIEW
    }
}
