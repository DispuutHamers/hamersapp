package nl.ecci.hamers.users

import com.google.gson.annotations.SerializedName
import nl.ecci.hamers.helpers.Utils
import java.util.*

class User(val id: Int = Utils.notFound,
           val name: String = Utils.unknown,
           val email: String = Utils.unknown,
           val admin: Int = 0, // Should be bool
           @SerializedName("quotes")
           val quoteCount: Int = Utils.notFound,
           @SerializedName("reviews")
           val reviewCount: Int = Utils.notFound,
           @SerializedName("lid")
           val member: User.Member = User.Member.NONE,
           val batch: Int = Utils.notFound,
           val nicknames: ArrayList<Nickname> = ArrayList(),
           @SerializedName("created_at")
           val createdAt: Date = Date()) {

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