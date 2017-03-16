package nl.ecci.hamers.events

import com.google.gson.annotations.SerializedName
import nl.ecci.hamers.helpers.Utils
import java.util.*

class Event(val id: Int,
            val title: String = Utils.unknown,
            @SerializedName("beschrijving")
            val description: String = Utils.unknown,
            val location: String = Utils.unknown,
            @SerializedName("user_id")
            val userID: Int = 0,
            val date: Date = Date(),
            @SerializedName("end_time")
            val endDate: Date = Date(),
            val deadline: Date = Date(),
            @SerializedName("signups")
            val signUps: ArrayList<SignUp> = ArrayList<SignUp>(),
            @SerializedName("created_at")
            val createdAt: Date = Date(),
            val attendance: Boolean = false) {

    companion object {
        @JvmField val EVENT = "EVENT"
    }

}
