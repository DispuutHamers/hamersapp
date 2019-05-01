package nl.ecci.hamers.models

import com.google.gson.annotations.SerializedName
import nl.ecci.hamers.utils.Utils
import java.util.*

data class Event(val id: Int = Utils.notFound,
                 val title: String = Utils.unknown,
                 @SerializedName("beschrijving")
                 val description: String = Utils.unknown,
                 val location: String = Utils.unknown,
                 @SerializedName("user_id")
                 val userID: Int = Utils.notFound,
                 val date: Date = Date(),
                 @SerializedName("end_time")
                 val endDate: Date = Date(),
                 val deadline: Date = Date(),
                 @SerializedName("signups")
                 val signUps: ArrayList<SignUp> = ArrayList(),
                 @SerializedName("created_at")
                 val createdAt: Date = Date(),
                 val attendance: Boolean = false) {

    companion object {
        const val EVENT = "EVENT"
    }
}
