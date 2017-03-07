package nl.ecci.hamers.beers

import com.google.gson.annotations.SerializedName
import java.util.*

internal class Review(val id: Int,
                      @SerializedName("beer_id")
                      val beerID: Int,
                      @SerializedName("user_id")
                      val userID: Int,
                      val description: String,
                      val rating: Int,
                      @SerializedName("created_at")
                      val createdAt: Date,
                      val proefdatum: Date) {

    companion object {
        val REVIEW = "REVIEW"
    }
}