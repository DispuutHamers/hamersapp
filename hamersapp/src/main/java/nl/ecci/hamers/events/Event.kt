package nl.ecci.hamers.events

import com.google.gson.annotations.SerializedName
import nl.ecci.hamers.helpers.Utils
import java.util.*

class Event(val id: Int,
            val title: String = Utils.unknown,
            @SerializedName("beschrijving")
            val description: String = Utils.unknown,
            val location: String = Utils.unknown,
            val date: Date = Date(),
            @SerializedName("end_time")
            val endDate: Date = Date(),
            val deadline: Date = Date(),
            @SerializedName("signups")
            val signUps: ArrayList<Event.SignUp> = ArrayList<Event.SignUp>(),
            @SerializedName("created_at")
            val createdAt: Date = Date(),
            val attendance: Boolean = false) {
    @SerializedName("user_id")
    val userID: Int = 0

    inner class SignUp(val id: Int,
                       @SerializedName("event_id")
                       val eventID: Int,
                       @SerializedName("user_id")
                       val userID: Int,
                       @SerializedName("status")
                       val isAttending: Boolean,
                       @SerializedName("created_at")
                       val createdAt: Date,
                       @SerializedName("reason")
                       val reason: String)

    companion object {
        @JvmField val EVENT = "EVENT"
    }

}
