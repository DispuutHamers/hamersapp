package nl.ecci.hamers.meetings

import com.google.gson.annotations.SerializedName
import nl.ecci.hamers.helpers.Utils
import java.util.*

class Meeting(val id: Int = Utils.notFound,
              val agenda: String = Utils.unknown,
              val notes: String = Utils.unknown,
              @SerializedName("onderwerp")
              val subject: String = Utils.unknown,
              @SerializedName("user_id")
              val userID: Int = Utils.notFound,
              val date: Date = Date(),
              @SerializedName("created_at")
              val createdAt: Date = Date(),
              @SerializedName("updated_at")
              val updatedAt: Date = Date()) {

    companion object {
        val MEETING = "MEETING"
    }
}
