package nl.ecci.hamers.beers

import com.google.gson.annotations.SerializedName
import java.util.*

class Beer(val id: Int,
           var name: String?,
           @SerializedName("soort")
           var kind: String?,
           @SerializedName("picture")
           val imageURL: String,
           var percentage: String?,
           var brewer: String?,
           var country: String?,
           @SerializedName("cijfer")
           var rating: String?,
           val url: String,
           @SerializedName("created_at")
           val createdAt: Date) {

    companion object {
        val BEER = "BEER"
    }
}