package nl.ecci.hamers.models

import com.google.gson.annotations.SerializedName
import nl.ecci.hamers.utils.Utils
import java.util.*

class SignUp {
    val id: Int = Utils.notFound
    @SerializedName("event_id")
    val eventID: Int = Utils.notFound
    @SerializedName("user_id")
    val userID: Int = Utils.notFound
    @SerializedName("status")
    val isAttending: Boolean = false
    @SerializedName("created_at")
    val createdAt: Date = Date()
    @SerializedName("reason")
    val reason: String? = Utils.unknown

    companion object {
        @JvmField val SIGNUP = "SIGNUP"
    }
}