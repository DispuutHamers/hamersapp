package nl.ecci.hamers.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import nl.ecci.hamers.helpers.DataUtils
import nl.ecci.hamers.helpers.DataUtils.getGravatarURL
import nl.ecci.hamers.meetings.Meeting
import nl.ecci.hamers.news.News
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
                "Quote" -> quotePush(remoteMessage.data["object"])
                "Event" -> eventPush(remoteMessage.data["object"])
                "Beer" -> beerPush(remoteMessage.data["object"])
                "Review" -> reviewPush(remoteMessage.data["object"])
                "News" -> newsPush(remoteMessage.data["object"])
                "Meeting" -> meetingPush(remoteMessage.data["object"])
                "Sticker" -> stickerPush()
            }
        }
    }

    fun quotePush(quoteString: String?) {
        val quote = gson.fromJson(quoteString, Quote::class.java)
        sendNotification(quote.text, applicationContext.getString(R.string.change_quote_new), quote.userID)
    }

    fun eventPush(eventString: String?) {
        val event = gson.fromJson(eventString, Event::class.java)
        var title = event.title
        if (event.location.isNotEmpty()) {
            title += "(@" + event.location + ")"
        }
        sendNotification(title, applicationContext.getString(R.string.change_event_new), event.userID)
    }

    fun beerPush(beerString: String?) {
        val beer = gson.fromJson(beerString, Beer::class.java)
        sendNotification(beer.name, applicationContext.getString(R.string.change_beer_new), -1)
    }

    fun reviewPush(reviewString: String?) {
        val review = gson.fromJson(reviewString, Review::class.java)
        sendNotification(review.description, applicationContext.getString(R.string.change_review_new), review.userID)
    }

    fun newsPush(newsString: String?) {
        val news = gson.fromJson(newsString, News::class.java)
        sendNotification(news.title, applicationContext.getString(R.string.change_news_new), -1)
    }

    fun meetingPush(meetingString: String?) {
       val meeting = gson.fromJson(meetingString, Meeting::class.java)
        sendNotification(meeting.subject, applicationContext.getString(R.string.change_news_new), meeting.userID)
    }

    fun stickerPush() {
        sendNotification(applicationContext.getString(R.string.change_sticker_new), "", -1)
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private fun sendNotification(title: String, summary: String, userId: Int) {
        val user = DataUtils.getUser(applicationContext, userId)
        val icon = ImageLoader.getInstance().loadImageSync(getGravatarURL(user.email))

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.launcher_icon)
                .setLargeIcon(icon)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)

        val style = NotificationCompat.BigTextStyle()
        style.setBigContentTitle(title)
        style.bigText(summary)
        style.setSummaryText(summary)
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
