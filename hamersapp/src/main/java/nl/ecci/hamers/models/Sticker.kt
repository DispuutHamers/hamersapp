package nl.ecci.hamers.models

import com.google.gson.annotations.SerializedName

data class Sticker(val id: Int,
              @SerializedName("user_id")
              val userID: Int,
              val lat: Float,
              val lon: Float,
              val notes: String) {
    companion object {
        val ID = "ID"
    }
}
