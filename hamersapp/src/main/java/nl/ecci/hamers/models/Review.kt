package nl.ecci.hamers.models

import com.google.gson.annotations.SerializedName
import nl.ecci.hamers.utils.Utils
import java.util.*

data class Review(val id: Int = Utils.notFound,
                  @SerializedName("beer_id")
                  val beerID: Int = Utils.notFound,
                  @SerializedName("user_id")
                  val userID: Int = Utils.notFound,
                  val description: String = Utils.unknown,
                  val rating: Int = Utils.notFound,
                  @SerializedName("created_at")
                  val createdAt: Date = Date(),
                  val proefdatum: Date = Date()) {

    companion object {
        val REVIEW = "REVIEW"
    }
}