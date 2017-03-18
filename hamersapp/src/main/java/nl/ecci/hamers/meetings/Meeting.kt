package nl.ecci.hamers.meetings

import com.google.gson.annotations.SerializedName

import java.util.Date

class Meeting(val id: Int,
              val agenda: String,
              val notes: String,
              @SerializedName("onderwerp")
              val subject: String,
              @SerializedName("user_id")
              val userID: Int,
              val date: Date,
              @SerializedName("created_at")
              val createdAt: Date,
              @SerializedName("updated_at")
              val updatedAt: Date) {

    companion object {
        val MEETING = "MEETING"
    }
}
