package nl.ecci.hamers.users

import com.google.gson.annotations.SerializedName
import java.util.*

class User(val id: Int,
           val name: String,
           val email: String,
           @SerializedName("quotes")
           val quoteCount: Int,
           @SerializedName("reviews")
           val reviewCount: Int,
           @SerializedName("lid")
           val member: User.Member,
           val batch: Int,
           val nicknames: ArrayList<Nickname>,
           @SerializedName("created_at")
           val createdAt: Date) {

    enum class Member {
        @SerializedName("lid")
        LID,
        @SerializedName("a-lid")
        ALID,
        @SerializedName("o-lid")
        OLID,
        @SerializedName("none")
        NONE
    }

    companion object {
        val USER = "USER"
    }
}