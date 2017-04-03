package nl.ecci.hamers.models

import com.google.gson.annotations.SerializedName
import java.util.*

internal class Quote(val id: Int,
                     val text: String,
                     @SerializedName("user_id")
                     val userID: Int,
                     @SerializedName("created_at")
                     val date: Date)