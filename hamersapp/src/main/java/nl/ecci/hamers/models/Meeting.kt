package nl.ecci.hamers.models

import com.google.gson.annotations.SerializedName
import nl.ecci.hamers.utils.Utils
import java.util.*

data class Meeting(val id: Int = Utils.notFound,
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
        const val MEETING = "MEETING"
    }
}
