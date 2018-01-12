package nl.ecci.hamers.models

import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.util.*

data class Change(val id: Int,
                  @SerializedName("item_type")
                  val itemType: ItemType?,
                  @SerializedName("item_id")
                  val itemId: Int,
                  val event: Event,
                  @SerializedName("whodunnit")
                  val userId: Int,
                  @SerializedName("object")
                  val newObject: JSONObject,
                  @SerializedName("created_at")
                  // Change created, so date of mutation
                  val createdAt: Date) {

    enum class ItemType {
        @SerializedName("Quote")
        QUOTE,
        @SerializedName("Event")
        EVENT,
        @SerializedName("Signup")
        SIGNUP,
        @SerializedName("Beer")
        BEER,
        @SerializedName("Review")
        REVIEW,
        @SerializedName("News")
        NEWS,
        @SerializedName("User")
        USER,
        @SerializedName("Sticker")
        STICKER,
        @SerializedName("Nickname")
        NICKNAME,
        @SerializedName("Meeting")
        MEETING,
        @SerializedName("Device")
        DEVICE,
    }

    enum class Event {
        @SerializedName("create")
        CREATE,
        @SerializedName("update")
        UPDATE,
        @SerializedName("destroy")
        DESTROY,
    }

    companion object {
        val CHANGE = "CHANGE"
    }
}