package nl.ecci.hamers.changes

import com.google.gson.annotations.SerializedName
import java.util.*

class Change(val id: Int,
             val item_type: ItemType,
             val item_id: Int,
             val event: Event,
             val whodunnit: Int,
             @SerializedName("object")
             val newObject: String,
             @SerializedName("created_at")
             val createdAt: Date,
             @SerializedName("object_changes")
             val objectChanges: String) {

    companion object {
        val CHANGE = "CHANGE"
    }

    enum class ItemType {
        @SerializedName("Quote") QUOTE,
        @SerializedName("Event") EVENT,
        @SerializedName("Signup") SIGNUP,
        @SerializedName("Beer") BEER,
        @SerializedName("Review") REVIEW,
        @SerializedName("News") NEWS,
        @SerializedName("User") USER,
        @SerializedName("Device") DEVICE,
    }

    enum class Event {
        @SerializedName("create") CREATE,
        @SerializedName("update") UPDATE,
        @SerializedName("destroy") DESTROY,
    }
}