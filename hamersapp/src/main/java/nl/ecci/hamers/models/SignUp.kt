package nl.ecci.hamers.models

import com.google.gson.annotations.SerializedName
import java.util.*

class SignUp(val id: Int,
             @SerializedName("event_id")
             val eventID: Int,
             @SerializedName("user_id")
             val userID: Int,
             @SerializedName("status")
             val isAttending: Boolean,
             @SerializedName("created_at")
             val createdAt: Date,
             @SerializedName("reason")
             val reason: String) {

    companion object {
        @JvmField val SIGNUP = "SIGNUP"
    }
}