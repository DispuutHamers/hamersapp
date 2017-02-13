package nl.ecci.hamers.events

import com.google.gson.annotations.SerializedName

import java.util.ArrayList
import java.util.Date

class Event(val id: Int,
            val title: String,
            @SerializedName("beschrijving")
            val description: String,
            val location: String,
            val date: Date,
            @SerializedName("end_time")
            val endDate: Date,
            val deadline: Date,
            val signUps: ArrayList<Event.SignUp>,
            @SerializedName("created_at")
            val createdAt: Date,
            @SerializedName("attendance")
            val signupMandatory: Boolean?) {
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
