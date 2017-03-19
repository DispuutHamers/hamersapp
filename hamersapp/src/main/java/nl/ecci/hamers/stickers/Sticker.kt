package nl.ecci.hamers.stickers

import com.google.gson.annotations.SerializedName

internal class Sticker(
        val id: Int,
        @SerializedName("user_id")
        val userID: Int,
        val lat: Float,
        val lon: Float,
        val notes: String) {
    companion object {
        val ID = "ID"
    }
}
