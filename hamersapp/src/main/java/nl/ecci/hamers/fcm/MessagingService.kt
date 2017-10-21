package nl.ecci.hamers.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import nl.ecci.hamers.R
import nl.ecci.hamers.data.Loader
import nl.ecci.hamers.models.*
import nl.ecci.hamers.ui.activities.MainActivity
import nl.ecci.hamers.ui.activities.SingleBeerActivity
import nl.ecci.hamers.ui.activities.SingleEventActivity
import nl.ecci.hamers.ui.activities.SingleMeetingActivity
import nl.ecci.hamers.utils.DataUtils
import nl.ecci.hamers.utils.DataUtils.getGravatarURL


class MessagingService : FirebaseMessagingService() {

    var intent: Intent? = null
    private var pendingIntent : PendingIntent? = null
    var gson: Gson = GsonBuilder().setDateFormat(MainActivity.dbDF.toPattern()).create()

    /**
     * Called when message is received.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        intent = Intent(this, MainActivity::class.java)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

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
                "Sticker" -> stickerPush(remoteMessage.data["object"])
            }
        }
    }

    private fun quotePush(quoteString: String?) {
        val quote = gson.fromJson(quoteString, Quote::class.java)
        sendNotification(quote.text, applicationContext.getString(R.string.change_quote_new), quote.userID)
    }

    private fun eventPush(eventString: String?) {
        Loader.getData(this, Loader.EVENTURL, -1, null, null)

        val event = gson.fromJson(eventString, Event::class.java)
        var title = event.title
        if (event.location.isNotBlank()) {
            title += "(@" + event.location + ")"
        }

        intent = Intent(this, SingleEventActivity::class.java)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent?.putExtra(Event.EVENT, event.id)
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        sendNotification(title, applicationContext.getString(R.string.change_event_new), event.userID)
    }

    private fun beerPush(beerString: String?) {
        Loader.getData(this, Loader.BEERURL, -1, null, null)
        val beer = gson.fromJson(beerString, Beer::class.java)

        intent = Intent(this, SingleBeerActivity::class.java)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent?.putExtra(Beer.BEER, beer.id)
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        sendNotification(beer.name, applicationContext.getString(R.string.change_beer_new), -1)
    }

    private fun reviewPush(reviewString: String?) {
        Loader.getData(this, Loader.REVIEWURL, -1, null, null)
        val review = gson.fromJson(reviewString, Review::class.java)

        intent = Intent(this, SingleBeerActivity::class.java)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent?.putExtra(Beer.BEER, review.beerID)
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        sendNotification(review.description, applicationContext.getString(R.string.change_review_new), review.userID)
    }

    private fun newsPush(newsString: String?) {
        val news = gson.fromJson(newsString, News::class.java)
        sendNotification(news.title, applicationContext.getString(R.string.change_news_new), -1)
    }

    private fun meetingPush(meetingString: String?) {
        Loader.getData(this, Loader.MEETINGURL, -1, null, null)
        val meeting = gson.fromJson(meetingString, Meeting::class.java)

        intent = Intent(this, SingleMeetingActivity::class.java)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent?.putExtra(Meeting.MEETING, meeting.id)
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        sendNotification(meeting.subject, applicationContext.getString(R.string.change_meeting_new), meeting.userID)
    }

    private fun stickerPush(stickerString: String?) {
        val sticker = gson.fromJson(stickerString, Sticker::class.java)
        sendNotification(applicationContext.getString(R.string.change_sticker_new), sticker.notes, sticker.userID)
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private fun sendNotification(title: String, summary: String, userId: Int) {
        val user = DataUtils.getUser(applicationContext, userId)
        val icon = Glide.with(this).load(getGravatarURL(user.email)).asBitmap().into(300, 300).get()

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

        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }
        notificationBuilder.setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        val TAG = "MessagingService"
    }
}
