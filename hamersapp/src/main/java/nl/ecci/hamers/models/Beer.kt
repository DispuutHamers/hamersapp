package nl.ecci.hamers.models

import com.google.gson.annotations.SerializedName
import nl.ecci.hamers.utils.Utils
import java.util.*

class Beer {
    val id: Int = Utils.notFound
    var name: String = Utils.unknown
    @SerializedName("soort")
    var kind: String = Utils.unknown
    @SerializedName("picture")
    val imageURL: String = Utils.unknown
    var percentage: String = Utils.unknown
    var brewer: String = Utils.unknown
    var country: String = Utils.unknown
    @SerializedName("cijfer")
    var rating: String = Utils.unknown
    val url: String = Utils.unknown
    @SerializedName("created_at")
    val createdAt: Date = Date()

    companion object {
        val BEER = "BEER"
    }
}