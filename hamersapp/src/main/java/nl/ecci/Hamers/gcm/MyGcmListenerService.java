package nl.ecci.Hamers.gcm;

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
import com.google.android.gms.gcm.GcmListenerService;
import nl.ecci.Hamers.Helpers.DataManager;
import nl.ecci.Hamers.MainActivity;
import nl.ecci.Hamers.R;
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
    private final String EVENTDESCRIPTION = "beschrijving";

    // Beer
    private final String BEERTYPE = "beer";
    private final String BEERNAME = "name";

    // Review
    private final String REVIEWTYPE = "review";
    private final String REVIEWBEER = "beer_id";
    private final String REVIEWDESCRIPTION = "description";
    private final String REVIEWRATING = "rating";

    // Common
    private final String USER = "user_id";

    private SharedPreferences prefs;

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

        JSONObject quote;
        JSONObject event;
        JSONObject beer;
        JSONObject review;

        // QUOTE
        try {
            quote = new JSONObject(object.getString(QUOTETYPE));
            if (quote.length() != 0) {
                title = quote.getString(QUOTEBODY);

                String userName = null;
                userName = DataManager.UserIDtoUserName(prefs, Integer.valueOf(quote.getString(USER)));
                if (userName != null) {
                    message = "- " + userName;
                } else {
                    message = "- user";
                }
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }

        // EVENT
        try {
            event = new JSONObject(object.getString(EVENTTYPE));
            if (event.length() != 0) {
                title = event.getString(EVENTTITLE);
                message = event.getString(EVENTDESCRIPTION);

            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }

        // BEER
        try {
            beer = new JSONObject(object.getString(BEERTYPE));
            if (beer.length() != 0) {
                title = beer.getString(BEERNAME);
                message = "Is net toegevoegd aan de database!";

            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }

        // Review
        try {
            review = new JSONObject(object.getString(REVIEWTYPE));
            if (review.length() != 0) {

                String userName = null;
                String beerName = null;
                userName = DataManager.UserIDtoUserName(prefs, Integer.valueOf(review.getString(USER)));
                beerName = DataManager.BeerIDtoBeerName(prefs, Integer.valueOf(review.getString(REVIEWBEER)));
                if (userName != null && beerName != null) {
                    title = userName + " / " + beerName;
                } else {
                    title = "Something went wrong";
                }

                message = review.getString(REVIEWRATING) + " - " + review.getString(REVIEWDESCRIPTION);
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }

        // Show notification
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean push = settings.getBoolean("pushPref", true);
        if (push) {
            sendNotification(title, message);
        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String title, String message) {
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

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
