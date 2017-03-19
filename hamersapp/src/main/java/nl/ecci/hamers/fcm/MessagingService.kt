package nl.ecci.hamers.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nostra13.universalimageloader.core.ImageLoader
import nl.ecci.hamers.MainActivity
import nl.ecci.hamers.R
import nl.ecci.hamers.beers.Beer
import nl.ecci.hamers.beers.Review
import nl.ecci.hamers.events.Event
import nl.ecci.hamers.events.SignUp
import nl.ecci.hamers.helpers.DataUtils
import nl.ecci.hamers.helpers.DataUtils.getGravatarURL
import nl.ecci.hamers.quotes.Quote

class MessagingService : FirebaseMessagingService() {

    var intent: Intent? = null
    var gson: Gson = GsonBuilder().setDateFormat(MainActivity.dbDF.toPattern()).create()

    /**
     * Called when message is received.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        intent = Intent(this, MainActivity::class.java)

        // Check if message contains a notification payload.
        if (remoteMessage?.notification != null) {
            // TODO
            Log.d(TAG, "FCM Notification Body: " + remoteMessage.notification.body!!)
        }

        // Check if message contains a data payload.
        if (remoteMessage?.data?.isNotEmpty() as Boolean) {
            Log.d(TAG, "FCM data payload: " + remoteMessage.data)

            when (remoteMessage.data["type"]) {
                "Quote" -> handleQuotePush(remoteMessage.data["object"])
                "Event" -> handleEventPush(remoteMessage.data["object"])
                "Signup" -> handleSignupPush(remoteMessage.data["object"])
                "Beer" -> handleEventPush(remoteMessage.data["object"])
                "Review" -> handleEventPush(remoteMessage.data["object"])
            }
        }
    }

    fun handleQuotePush(quoteString: String?) {
        val quote = gson.fromJson(quoteString, Quote::class.java)
        val user = DataUtils.getUser(applicationContext, quote.userID)
        val image = ImageLoader.getInstance().loadImageSync(getGravatarURL(user.email))

//        displayImage(getGravatarURL(user.email), itemView.user_image, AnimateFirstDisplayListener())

        sendNotification(quote.text, image)
    }

    fun handleEventPush(eventString: String?) {
        val event = gson.fromJson(eventString, Event::class.java)
    }

    fun handleSignupPush(signupString: String?) {
        val signup = gson.fromJson(signupString, SignUp::class.java)
    }

    fun handleBeerPush(beerString: String?) {
        val beer = gson.fromJson(beerString, Beer::class.java)
    }

    fun handleReviewPush(reviewString: String?) {
        val review = gson.fromJson(reviewString, Review::class.java)
    }


    //        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    //
    //        Intent intent = new Intent(this, MainActivity.class);
    //        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    //        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    //
    //        String title = null;
    //        String message = null;
    //
    //        JSONObject object = new JSONObject();
    //        Set<String> keys = data.keySet();
    //        for (String key : keys) {
    //            try {
    //                object.put(key, data.get(key));
    //            } catch (JSONException ignored) {
    //            }
    //        }
    //
    //        System.out.println(data.toString());

    //        // QUOTE
    //        JSONArray json;
    //        String USERID = "user_id";
    //        try {
    //            String QUOTETYPE = "quote";
    //            JSONObject quote = new JSONObject(object.getString(QUOTETYPE));
    //            if (quote.length() != 0) {
    //                type = Type.QUOTE;
    //                String QUOTEBODY = "text";
    //                title = quote.getString(QUOTEBODY);
    //
    //                User user = Utils.INSTANCE.getUser(prefs, Integer.valueOf(quote.getString(USERID)));
    //                if (user != null) {
    //                    message = user.getName();
    //                } else {
    //                    message = "- user";
    //                }
    //
    //                // Add quote to quote list
    //                json = new JSONArray(prefs.getString(Loader.QUOTEURL, null));
    //                json.put(quote);
    //                prefs.edit().putString(Loader.QUOTEURL, json.toString()).apply();
    //            }
    //        } catch (JSONException | NullPointerException ignored) {
    //        }
    //
    //        // EVENT
    //        try {
    //            String EVENTTYPE = "event";
    //            event = new JSONObject(object.getString(EVENTTYPE));
    //            if (event.length() != 0) {
    //                type = Type.EVENT;
    //                String EVENTTITLE = "title";
    //                title = event.getString(EVENTTITLE);
    //                String EVENTDESCRIPTION = "beschrijving";
    //                message = event.getString(EVENTDESCRIPTION);
    //
    //                // Add event to event list
    //                json = new JSONArray(prefs.getString(Loader.EVENTURL, null));
    //                json.put(event);
    //                prefs.edit().putString(Loader.EVENTURL, json.toString()).apply();
    //
    //                intent = new Intent(this, SingleEventActivity.class);
    //                intent.putExtra(Event.EVENT, event.getInt("id"));
    //                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    //                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    //            }
    //        } catch (JSONException | NullPointerException ignored) {
    //        }
    //
    //        // BEER
    //        try {
    //            String BEERTYPE = "beer";
    //            beer = new JSONObject(object.getString(BEERTYPE));
    //            if (beer.length() != 0) {
    //                type = Type.BEER;
    //                String BEERNAME = "name";
    //                title = "Biertje: " + beer.getString(BEERNAME);
    //                message = "Is net toegevoegd aan de database!";
    //
    //                // Add beer to beer list
    //                json = new JSONArray(prefs.getString(Loader.BEERURL, null));
    //                json.put(beer);
    //                prefs.edit().putString(Loader.BEERURL, json.toString()).apply();
    //
    //                intent = new Intent(this, SingleBeerActivity.class);
    //                intent.putExtra(Beer.BEER, beer.getInt("id"));
    //                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    //                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    //            }
    //        } catch (JSONException | NullPointerException ignored) {
    //        }
    //
    //        // REVIEW
    //        try {
    //            String REVIEWTYPE = "review";
    //            JSONObject review = new JSONObject(object.getString(REVIEWTYPE));
    //            if (review.length() != 0) {
    //                type = Type.REVIEW;
    //                User user = Utils.INSTANCE.getUser(prefs, review.getInt(USERID));
    //                String REVIEWBEER = "beer_id";
    //                Beer beer = Utils.INSTANCE.getBeer(prefs, review.getInt(REVIEWBEER));
    //                String REVIEWRATING = "rating";
    //                if (user != null && beer != null) {
    //                    title = user.getName() + " / " + beer.getName() + " / " + review.getString(REVIEWRATING);
    //                } else if (user != null) {
    //                    title = user.getName() + " / onbekend / " + review.getString(REVIEWRATING);
    //                } else {
    //                    title = "Hamers";
    //                }
    //                String REVIEWDESCRIPTION = "description";
    //                message = review.getString(REVIEWDESCRIPTION);
    //
    //                // Add review to review list
    //                json = new JSONArray(prefs.getString(Loader.REVIEWURL, null));
    //                json.put(review);
    //                prefs.edit().putString(Loader.REVIEWURL, json.toString()).apply();
    //
    //                intent = new Intent(this, SingleBeerActivity.class);
    //                intent.putExtra(Beer.BEER, review.getInt("beer_id"));
    //                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    //                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    //            }
    //        } catch (JSONException | NullPointerException ignored) {
    //        }
    //
    //        if (title != null && message != null) {
    //            sendNotification(title, message, type, pendingIntent);
    //        }

    /**
     * Create and show a simple notification containing the received FCM message.

     * @param image FCM image received.
     */
    private fun sendNotification(title: String, image: Bitmap) {
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val icon = BitmapFactory.decodeResource(resources, R.drawable.launcher_icon)

        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.launcher_icon)
                .setLargeIcon(icon)
                .setContentTitle(title)
//                .setContentText(image)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)

        val style = NotificationCompat.BigPictureStyle()
        style.bigPicture(image)
        style.setSummaryText(title)
        notificationBuilder.setStyle(style)

        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        notificationBuilder.setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        val TAG = "MessagingService"
    }
}
