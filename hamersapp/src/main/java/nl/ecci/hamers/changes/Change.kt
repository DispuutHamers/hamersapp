package nl.ecci.hamers.changes

import com.google.gson.annotations.SerializedName
import java.util.*

class Change(val id: Int,
             @SerializedName("item_type")
             val itemType: ItemType?,
             @SerializedName("item_id")
             val itemId: Int,
             val event: Event,
             @SerializedName("whodunnit")
             val userId: Int,
             @SerializedName("object")
             val newObject: String,
             @SerializedName("created_at")
             val createdAt: Date, // Change created, so date of mutation
             @SerializedName("object_changes")
             val objectChanges: String) {

    enum class ItemType {
        @SerializedName("Quote") QUOTE,
        @SerializedName("Event") EVENT,
        @SerializedName("Signup") SIGNUP,
        @SerializedName("Beer") BEER,
        @SerializedName("Review") REVIEW,
        @SerializedName("News") NEWS,
        @SerializedName("User") USER,
        @SerializedName("Sticker") STICKER,
        @SerializedName("Nickname") NICKNAME,
        @SerializedName("Device") DEVICE,
    }

    enum class Event {
        @SerializedName("create") CREATE,
        @SerializedName("update") UPDATE,
        @SerializedName("destroy") DESTROY,
    }

    companion object {
        val CHANGE = "CHANGE"
    }
}