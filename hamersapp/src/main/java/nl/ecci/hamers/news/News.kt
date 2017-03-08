package nl.ecci.hamers.news

import com.google.gson.annotations.SerializedName

import java.util.Date

internal class News(val id: Int,
                    val title: String,
                    val body: String,
                    @SerializedName("cat")
                    val category: Category,
                    val date: Date,
                    @SerializedName("created_at")
                    val createdAt: Date) {

    enum class Category {
        @SerializedName("d") DISPUUT,
        @SerializedName("e") EXTERN,
        @SerializedName("l") LEDEN
    }
}
