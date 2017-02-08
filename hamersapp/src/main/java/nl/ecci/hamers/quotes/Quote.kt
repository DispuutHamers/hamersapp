package nl.ecci.hamers.quotes

import com.google.gson.annotations.SerializedName

import java.util.Date

internal class Quote(val id: Int,
                     val text: String,
                     @SerializedName("user_id")
                     val userID: Int,
                     @SerializedName("created_at")
                     val date: Date)